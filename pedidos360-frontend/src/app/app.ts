import { CommonModule } from '@angular/common';
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AccountInfo } from '@azure/msal-browser';
import { MsalService } from '@azure/msal-angular';

import { loginRequest, redirectUri } from './auth-config';
import { ProductoService, Producto } from './services/producto.service';
import { VentaService, Venta, CrearVenta } from './services/venta.service';

export interface ItemCarrito {
  producto: Producto;
  cantidad: number;
  subtotal: number;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  title = 'Pedidos360';
  usuario: AccountInfo | null = null;
  cargandoAuth = true;
  cargandoProductos = false;
  cargandoVentas = false;
  guardando = false;
  comprando = false;
  mensajeError = '';
  mensajeExito = '';

  // Lista de productos (Tabla 2)
  productos: Producto[] = [];

  // Modelo del formulario de productos (Tabla 1)
  formProducto = {
    id_producto: null as number | null,
    nombre: '',
    descripcion: '',
    precio: 0
  };

  modoEdicion = false;

  // Carrito de compras (Tabla 3)
  carrito: ItemCarrito[] = [];
  metodoPago = 'TARJETA_CREDITO';
  nombreVendedor = 'Vendedor Web';

  // Ventas registradas (Tabla 4)
  ventas: Venta[] = [];
  ultimaVenta: Venta | null = null;

  constructor(
    private readonly authService: MsalService,
    private readonly productoService: ProductoService,
    private readonly ventaService: VentaService,
    private readonly cdr: ChangeDetectorRef
  ) {}

  async ngOnInit(): Promise<void> {
    try {
      await this.authService.instance.initialize();

      const resultado =
        await this.authService.instance.handleRedirectPromise();

      if (resultado?.account) {
        this.authService.instance.setActiveAccount(resultado.account);
      }

      this.actualizarUsuario();

      if (this.usuario) {
        this.cargarProductos();
        this.cargarVentas();
      }
    } catch (error) {
      console.error('Error al inicializar MSAL:', error);
      this.mensajeError =
        'No fue posible inicializar la autenticación con Microsoft Entra ID.';
    } finally {
      this.cargandoAuth = false;
      this.cdr.detectChanges();
    }
  }

  iniciarSesion(): void {
    this.limpiarMensajes();

    this.authService.loginRedirect(loginRequest).subscribe({
      error: (error) => {
        console.error('Error al iniciar sesión:', error);
        this.mensajeError =
          'No fue posible iniciar sesión con Microsoft.';
        this.cdr.detectChanges();
      }
    });
  }

  cerrarSesion(): void {
    this.authService.logoutRedirect({
      account: this.usuario ?? undefined,
      postLogoutRedirectUri: redirectUri
    }).subscribe({
      error: (error) => {
        console.error('Error al cerrar sesión:', error);
        this.mensajeError = 'No fue posible cerrar la sesión.';
        this.cdr.detectChanges();
      }
    });
  }

  cargarProductos(): void {
    this.cargandoProductos = true;
    this.limpiarMensajes();

    this.productoService.obtenerProductos().subscribe({
      next: (data) => {
        this.productos = data;
        this.cargandoProductos = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar productos:', err);
        this.mensajeError =
          'Error al cargar el inventario de productos desde el BFF.';
        this.cargandoProductos = false;
        this.cdr.detectChanges();
      }
    });
  }

  cargarVentas(): void {
    this.cargandoVentas = true;

    this.ventaService.obtenerVentas().subscribe({
      next: (data) => {
        this.ventas = data.reverse(); // Mostrar las más recientes primero
        this.cargandoVentas = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar ventas:', err);
        this.cargandoVentas = false;
        this.cdr.detectChanges();
      }
    });
  }

  guardarProducto(): void {
    if (!this.formProducto.nombre.trim()) {
      this.mensajeError = 'El nombre del producto es obligatorio.';
      return;
    }

    if (this.formProducto.precio <= 0) {
      this.mensajeError = 'El precio debe ser un valor numérico mayor a 0.';
      return;
    }

    this.guardando = true;
    this.limpiarMensajes();

    const payload = {
      nombre: this.formProducto.nombre.trim(),
      descripcion: this.formProducto.descripcion.trim(),
      precio: Number(this.formProducto.precio)
    };

    if (this.modoEdicion && this.formProducto.id_producto) {
      this.productoService
        .actualizarProducto(this.formProducto.id_producto, payload)
        .subscribe({
          next: (productoActualizado) => {
            this.mensajeExito = `Producto #${productoActualizado.id_producto} ("${productoActualizado.nombre}") actualizado correctamente.`;
            this.guardando = false;
            this.cancelarEdicion();
            this.cargarProductos();
          },
          error: (err) => {
            console.error('Error al actualizar producto:', err);
            this.mensajeError =
              'Error al actualizar el producto en el servidor.';
            this.guardando = false;
            this.cdr.detectChanges();
          }
        });
    } else {
      this.productoService.agregarProducto(payload).subscribe({
        next: (nuevoProducto) => {
          this.mensajeExito = `Producto #${nuevoProducto.id_producto} ("${nuevoProducto.nombre}") creado y guardado en la tabla.`;
          this.guardando = false;
          this.cancelarEdicion();
          this.cargarProductos();
        },
        error: (err) => {
          console.error('Error al agregar producto:', err);
          this.mensajeError = 'Error al registrar el nuevo producto.';
          this.guardando = false;
          this.cdr.detectChanges();
        }
      });
    }
  }

  seleccionarParaEditar(prod: Producto): void {
    this.limpiarMensajes();
    this.modoEdicion = true;
    this.formProducto = {
      id_producto: prod.id_producto || null,
      nombre: prod.nombre,
      descripcion: prod.descripcion || '',
      precio: prod.precio
    };

    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cancelarEdicion(): void {
    this.modoEdicion = false;
    this.formProducto = {
      id_producto: null,
      nombre: '',
      descripcion: '',
      precio: 0
    };
  }

  eliminarProducto(id: number): void {
    if (!confirm(`¿Estás seguro de que deseas eliminar el producto #${id}?`)) {
      return;
    }

    this.limpiarMensajes();

    this.productoService.eliminarProducto(id).subscribe({
      next: () => {
        this.mensajeExito = `Producto #${id} eliminado correctamente.`;
        // Si el producto estaba en el carrito, removerlo también
        this.carrito = this.carrito.filter(item => item.producto.id_producto !== id);
        this.cargarProductos();
      },
      error: (err) => {
        console.error('Error al eliminar producto:', err);
        this.mensajeError = `No fue posible eliminar el producto #${id}.`;
        this.cdr.detectChanges();
      }
    });
  }

  // =========================================================================
  // MÉTODOS DEL CARRITO DE COMPRAS (TABLA 3)
  // =========================================================================

  agregarAlCarrito(prod: Producto): void {
    this.limpiarMensajes();
    const itemExistente = this.carrito.find(
      item => item.producto.id_producto === prod.id_producto
    );

    if (itemExistente) {
      itemExistente.cantidad++;
      itemExistente.subtotal = itemExistente.cantidad * itemExistente.producto.precio;
    } else {
      this.carrito.push({
        producto: prod,
        cantidad: 1,
        subtotal: prod.precio
      });
    }

    this.mensajeExito = `"${prod.nombre}" añadido al carrito de compras.`;
    this.cdr.detectChanges();
  }

  cambiarCantidad(item: ItemCarrito, delta: number): void {
    item.cantidad += delta;
    if (item.cantidad <= 0) {
      this.eliminarDelCarrito(this.carrito.indexOf(item));
    } else {
      item.subtotal = item.cantidad * item.producto.precio;
      this.cdr.detectChanges();
    }
  }

  eliminarDelCarrito(index: number): void {
    if (index >= 0 && index < this.carrito.length) {
      const removido = this.carrito.splice(index, 1);
      this.mensajeExito = `"${removido[0].producto.nombre}" quitado del carrito.`;
      this.cdr.detectChanges();
    }
  }

  vaciarCarrito(): void {
    this.carrito = [];
    this.cdr.detectChanges();
  }

  getTotalCarrito(): number {
    return this.carrito.reduce((acc, item) => acc + item.subtotal, 0);
  }

  getCantidadTotalItems(): number {
    return this.carrito.reduce((acc, item) => acc + item.cantidad, 0);
  }

  // =========================================================================
  // ACCIÓN DE COMPRA (BFF -> VENTAS -> RABBITMQ -> BD -> FRONT)
  // =========================================================================

  comprarCarrito(): void {
    if (this.carrito.length === 0) {
      this.mensajeError = 'El carrito está vacío. Agrega productos desde el inventario.';
      return;
    }

    this.comprando = true;
    this.limpiarMensajes();

    const hoy = new Date().toISOString().split('T')[0];
    const cliente = this.usuario?.name || this.usuario?.username || 'Cliente Web';

    // Procesamos la compra de cada ítem del carrito hacia el BFF
    const ventasRealizadasTemp: Venta[] = [];
    let errores = 0;
    let procesados = 0;
    const totalItems = this.carrito.length;

    for (const item of [...this.carrito]) {
      const payload: CrearVenta = {
        nombreCliente: cliente,
        nombreVendedor: this.nombreVendedor,
        fechaVenta: hoy,
        totalVenta: item.subtotal,
        metodoPago: this.metodoPago,
        producto_id_producto: item.producto.id_producto!
      };

      this.ventaService.registrarVenta(payload).subscribe({
        next: (ventaCreada) => {
          ventasRealizadasTemp.push(ventaCreada);
          this.ultimaVenta = ventaCreada;
          procesados++;

          if (procesados === totalItems) {
            this.finalizarCompra(ventasRealizadasTemp, errores);
          }
        },
        error: (err) => {
          console.error('Error al registrar venta de producto:', item.producto.nombre, err);
          errores++;
          procesados++;

          if (procesados === totalItems) {
            this.finalizarCompra(ventasRealizadasTemp, errores);
          }
        }
      });
    }
  }

  private finalizarCompra(nuevasVentas: Venta[], errores: number): void {
    this.comprando = false;

    if (nuevasVentas.length > 0) {
      // Guardar en la lista visual del front
      this.ventas = [...nuevasVentas, ...this.ventas];
      this.vaciarCarrito();

      if (errores === 0) {
        this.mensajeExito = `🎉 ¡Compra exitosa! Se registraron ${nuevasVentas.length} venta(s) en la base de datos y se notificaron por RabbitMQ.`;
      } else {
        this.mensajeExito = `Se registraron ${nuevasVentas.length} venta(s), pero ${errores} producto(s) no se pudieron procesar.`;
      }
    } else {
      this.mensajeError = 'No fue posible registrar la venta. Verifica que los productos existan y el servicio de ventas esté activo.';
    }

    this.cargarVentas();
    this.cdr.detectChanges();
  }

  limpiarMensajes(): void {
    this.mensajeError = '';
    this.mensajeExito = '';
  }

  private actualizarUsuario(): void {
    const cuentaActiva = this.authService.instance.getActiveAccount();
    const cuentas = this.authService.instance.getAllAccounts();

    this.usuario = cuentaActiva ?? cuentas[0] ?? null;

    if (this.usuario && !cuentaActiva) {
      this.authService.instance.setActiveAccount(this.usuario);
    }
  }

  getInicialesUsuario(): string {
    if (!this.usuario?.name) return 'U';
    const partes = this.usuario.name.split(' ');
    if (partes.length >= 2) {
      return (partes[0][0] + partes[1][0]).toUpperCase();
    }
    return this.usuario.name.substring(0, 2).toUpperCase();
  }

  getNombreProducto(idProducto: number): string {
    const p = this.productos.find(prod => prod.id_producto === idProducto);
    return p ? p.nombre : `Producto #${idProducto}`;
  }
}