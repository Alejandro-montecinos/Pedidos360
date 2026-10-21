package com.duocuc.administrativo.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Service;

import com.duocuc.administrativo.model.AdminApiResponse;
import com.duocuc.administrativo.model.BindingRequest;
import com.duocuc.administrativo.model.ExchangeRequest;
import com.duocuc.administrativo.model.QueueRequest;

@Service
public class RabbitAdminService {

    private final RabbitAdmin rabbitAdmin;

    public RabbitAdminService(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }

    // ==========================================
    // 1. GESTIÓN DE COLAS (QUEUES)
    // ==========================================

    public AdminApiResponse crearCola(QueueRequest request) {
        try {
            QueueBuilder builder = request.isDurable() 
                    ? QueueBuilder.durable(request.getName()) 
                    : QueueBuilder.nonDurable(request.getName());

            if (request.isAutoDelete()) {
                builder.autoDelete();
            }
            if (request.isExclusive()) {
                builder.exclusive();
            }
            if (request.getDeadLetterExchange() != null && !request.getDeadLetterExchange().trim().isEmpty()) {
                builder.deadLetterExchange(request.getDeadLetterExchange().trim());
            }
            if (request.getDeadLetterRoutingKey() != null && !request.getDeadLetterRoutingKey().trim().isEmpty()) {
                builder.deadLetterRoutingKey(request.getDeadLetterRoutingKey().trim());
            }

            String declaredName = rabbitAdmin.declareQueue(builder.build());

            Map<String, Object> details = new HashMap<>();
            details.put("queueName", declaredName);
            details.put("durable", request.isDurable());
            details.put("autoDelete", request.isAutoDelete());
            details.put("exclusive", request.isExclusive());
            if (request.getDeadLetterExchange() != null && !request.getDeadLetterExchange().trim().isEmpty()) {
                details.put("deadLetterExchange", request.getDeadLetterExchange());
                details.put("deadLetterRoutingKey", request.getDeadLetterRoutingKey());
            }

            return AdminApiResponse.ok("Cola '" + declaredName + "' creada/declarada exitosamente en RabbitMQ.", details);
        } catch (Exception e) {
            return AdminApiResponse.error("Error al crear la cola: " + e.getMessage());
        }
    }

    public AdminApiResponse eliminarCola(String nombreCola, boolean unused, boolean empty) {
        try {
            if (!unused && !empty) {
                boolean eliminada = rabbitAdmin.deleteQueue(nombreCola);
                if (eliminada) {
                    return AdminApiResponse.ok("Cola '" + nombreCola + "' eliminada exitosamente de RabbitMQ.");
                } else {
                    return AdminApiResponse.error("No se pudo eliminar la cola '" + nombreCola + "' (posiblemente no existía en el broker).");
                }
            } else {
                rabbitAdmin.deleteQueue(nombreCola, unused, empty);
                return AdminApiResponse.ok("Petición de eliminación para la cola '" + nombreCola + "' procesada con filtros (unused=" + unused + ", empty=" + empty + ").");
            }
        } catch (Exception e) {
            return AdminApiResponse.error("Error al eliminar la cola '" + nombreCola + "': " + e.getMessage());
        }
    }

    public AdminApiResponse obtenerInfoCola(String nombreCola) {
        try {
            Properties props = rabbitAdmin.getQueueProperties(nombreCola);
            if (props == null) {
                return AdminApiResponse.error("La cola '" + nombreCola + "' no fue encontrada en RabbitMQ.");
            }
            Map<String, Object> details = new HashMap<>();
            props.forEach((k, v) -> details.put(k.toString(), v));
            return AdminApiResponse.ok("Propiedades de la cola '" + nombreCola + "' obtenidas correctamente.", details);
        } catch (Exception e) {
            return AdminApiResponse.error("Error al consultar la cola '" + nombreCola + "': " + e.getMessage());
        }
    }

    public AdminApiResponse purgarCola(String nombreCola) {
        try {
            rabbitAdmin.purgeQueue(nombreCola, false);
            return AdminApiResponse.ok("Cola '" + nombreCola + "' purgada exitosamente en RabbitMQ.");
        } catch (Exception e) {
            return AdminApiResponse.error("Error al purgar la cola '" + nombreCola + "': " + e.getMessage());
        }
    }

    // ==========================================
    // 2. GESTIÓN DE EXCHANGES
    // ==========================================

    public AdminApiResponse crearExchange(ExchangeRequest request) {
        try {
            String type = request.getType() != null ? request.getType().trim().toUpperCase() : "DIRECT";
            Exchange exchange;

            switch (type) {
                case "FANOUT":
                    exchange = new FanoutExchange(request.getName(), request.isDurable(), request.isAutoDelete());
                    break;
                case "TOPIC":
                    exchange = new TopicExchange(request.getName(), request.isDurable(), request.isAutoDelete());
                    break;
                case "HEADERS":
                    exchange = new HeadersExchange(request.getName(), request.isDurable(), request.isAutoDelete());
                    break;
                case "DIRECT":
                default:
                    exchange = new DirectExchange(request.getName(), request.isDurable(), request.isAutoDelete());
                    type = "DIRECT";
                    break;
            }

            rabbitAdmin.declareExchange(exchange);

            Map<String, Object> details = new HashMap<>();
            details.put("exchangeName", request.getName());
            details.put("type", type);
            details.put("durable", request.isDurable());
            details.put("autoDelete", request.isAutoDelete());

            return AdminApiResponse.ok("Exchange '" + request.getName() + "' [" + type + "] creado/declarado exitosamente.", details);
        } catch (Exception e) {
            return AdminApiResponse.error("Error al crear el exchange: " + e.getMessage());
        }
    }

    public AdminApiResponse eliminarExchange(String nombreExchange) {
        try {
            boolean eliminado = rabbitAdmin.deleteExchange(nombreExchange);
            if (eliminado) {
                return AdminApiResponse.ok("Exchange '" + nombreExchange + "' eliminado exitosamente de RabbitMQ.");
            } else {
                return AdminApiResponse.error("No se pudo eliminar el exchange '" + nombreExchange + "' (posiblemente no existía).");
            }
        } catch (Exception e) {
            return AdminApiResponse.error("Error al eliminar el exchange '" + nombreExchange + "': " + e.getMessage());
        }
    }

    // ==========================================
    // 3. GESTIÓN DE BINDINGS (VINCULACIONES)
    // ==========================================

    public AdminApiResponse crearBinding(BindingRequest request) {
        try {
            Binding binding = new Binding(
                    request.getQueueName(),
                    Binding.DestinationType.QUEUE,
                    request.getExchangeName(),
                    request.getRoutingKey() != null ? request.getRoutingKey() : "",
                    null
            );

            rabbitAdmin.declareBinding(binding);

            Map<String, Object> details = new HashMap<>();
            details.put("queueName", request.getQueueName());
            details.put("exchangeName", request.getExchangeName());
            details.put("routingKey", request.getRoutingKey());

            return AdminApiResponse.ok("Binding creado: Cola '" + request.getQueueName() 
                    + "' vinculada al Exchange '" + request.getExchangeName() 
                    + "' con routing key '" + request.getRoutingKey() + "'.", details);
        } catch (Exception e) {
            return AdminApiResponse.error("Error al crear el binding: " + e.getMessage());
        }
    }

    public AdminApiResponse eliminarBinding(BindingRequest request) {
        try {
            Binding binding = new Binding(
                    request.getQueueName(),
                    Binding.DestinationType.QUEUE,
                    request.getExchangeName(),
                    request.getRoutingKey() != null ? request.getRoutingKey() : "",
                    null
            );

            rabbitAdmin.removeBinding(binding);

            Map<String, Object> details = new HashMap<>();
            details.put("queueName", request.getQueueName());
            details.put("exchangeName", request.getExchangeName());
            details.put("routingKey", request.getRoutingKey());

            return AdminApiResponse.ok("Binding eliminado entre Cola '" + request.getQueueName() 
                    + "' y Exchange '" + request.getExchangeName() 
                    + "' con routing key '" + request.getRoutingKey() + "'.", details);
        } catch (Exception e) {
            return AdminApiResponse.error("Error al eliminar el binding: " + e.getMessage());
        }
    }
}
