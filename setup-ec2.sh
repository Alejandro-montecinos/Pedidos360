#!/usr/bin/env bash
# ==============================================================================
# SCRIPT DE APROVISIONAMIENTO Y DESPLIEGUE EN AWS EC2 (UBUNTU)
# Proyecto: Pedidos360 (Microservicios + Kafka + RabbitMQ + Nginx)
# Uso: chmod +x setup-ec2.sh && ./setup-ec2.sh
# ==============================================================================

set -e

echo "=========================================="
echo " [1/6] Actualizando paquetes del sistema..."
echo "=========================================="
sudo apt update && sudo apt upgrade -y
sudo apt install -y curl wget git htop unzip ufw ca-certificates gnupg lsb-release

echo "=========================================="
echo " [2/6] Configurando Swap de 2GB..."
echo "=========================================="
if [ ! -f /swapfile ]; then
    sudo fallocate -l 2G /swapfile
    sudo chmod 600 /swapfile
    sudo mkswap /swapfile
    sudo swapon /swapfile
    echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
    echo "Swap configurado exitosamente."
else
    echo "Swap ya se encuentra configurado."
fi

echo "=========================================="
echo " [3/6] Instalando Docker Engine & Compose..."
echo "=========================================="
if ! command -v docker &> /dev/null; then
    sudo mkdir -m 0755 -p /etc/apt/keyrings
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
    echo \
      "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
      $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

    sudo apt update
    sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
    sudo usermod -aG docker $USER
    echo "Docker instalado correctamente."
else
    echo "Docker ya se encuentra instalado."
fi

echo "=========================================="
echo " [4/6] Instalando y configurando Nginx..."
echo "=========================================="
sudo apt install -y nginx

# Crear configuración de Reverse Proxy para Pedidos360
sudo tee /etc/nginx/sites-available/pedidos360 > /dev/null << 'EOF'
server {
    listen 80;
    server_name _;

    # Frontend Angular
    location / {
        proxy_pass http://localhost:3000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # BFF API Gateway
    location /api/ {
        proxy_pass http://localhost:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Microservicio Administrativo (Kafka & RabbitMQ)
    location /admin/ {
        proxy_pass http://localhost:9090/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
EOF

sudo ln -sf /etc/nginx/sites-available/pedidos360 /etc/nginx/sites-enabled/pedidos360
sudo rm -f /etc/nginx/sites-enabled/default
sudo nginx -t
sudo systemctl restart nginx
sudo systemctl enable nginx

echo "=========================================="
echo " [5/6] Preparando variables de entorno..."
echo "=========================================="
if [ ! -f .env ]; then
    cp .env.example .env
    echo ".env creado a partir de .env.example"
fi

echo "=========================================="
echo " [6/6] Construyendo y levantando contenedores con Docker Compose..."
echo "=========================================="
sudo docker compose up -d --build

echo "=========================================="
echo " DESPLIEGUE COMPLETADO CON ÉXITO"
echo "  - Web / Frontend : http://<TU_IP_PUBLICA>/"
echo "  - BFF API        : http://<TU_IP_PUBLICA>/api/"
echo "  - Admin Panel    : http://<TU_IP_PUBLICA>/admin/admin.html"
echo "=========================================="

