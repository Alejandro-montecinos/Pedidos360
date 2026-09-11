import { CommonModule } from '@angular/common';
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AccountInfo } from '@azure/msal-browser';
import { MsalService } from '@azure/msal-angular';

import { loginRequest, redirectUri } from './auth-config';
import { ProductoService, Producto } from './services/producto.service';

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
  guardando = false;
  mensajeError = '';
  mensajeExito = '';

  // Lista de productos (Tabla 2)
  productos: Producto[] = [];

  // Modelo del formulario (Tabla 1)
  formProducto = {
    id_producto: null as number | null,
    nombre: '',
    descripcion: '',
    precio: 0
  };

  modoEdicion = false;

  constructor(
    private readonly authService: MsalService,
    private readonly productoService: ProductoService,
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
        this.cargarProductos();
      },
      error: (err) => {
        console.error('Error al eliminar producto:', err);
        this.mensajeError = `No fue posible eliminar el producto #${id}.`;
        this.cdr.detectChanges();
      }
    });
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
}