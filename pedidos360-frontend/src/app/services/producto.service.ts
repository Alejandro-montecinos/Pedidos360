import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MsalService } from '@azure/msal-angular';
import { Observable, from, switchMap, catchError, throwError } from 'rxjs';
import { apiScope } from '../auth-config';

export interface Producto {
  id_producto?: number;
  nombre: string;
  descripcion: string;
  precio: number;
}

@Injectable({
  providedIn: 'root'
})
export class ProductoService {
  private get apiUrl(): string {
    // Using relative URL so all traffic goes through nginx (no mixed content issues)
    return `${window.location.origin}/api/v1/productos`;
  }

  constructor(
    private readonly http: HttpClient,
    private readonly authService: MsalService
  ) {}

  private getAuthHeaders(): Observable<HttpHeaders> {
    const cuentaActiva =
      this.authService.instance.getActiveAccount() ||
      this.authService.instance.getAllAccounts()[0];

    if (!cuentaActiva) {
      return throwError(() => new Error('No hay una sesión activa de usuario.'));
    }

    return from(
      this.authService.instance.acquireTokenSilent({
        scopes: [apiScope],
        account: cuentaActiva
      })
    ).pipe(
      switchMap((resultado) => {
        return [
          new HttpHeaders({
            'Content-Type': 'application/json',
            Authorization: `Bearer ${resultado.accessToken}`
          })
        ];
      }),
      catchError((error) => {
        console.error('Error al adquirir token en segundo plano:', error);
        return throwError(
          () =>
            new Error(
              'No fue posible obtener la autorización para el BFF. Vuelve a iniciar sesión.'
            )
        );
      })
    );
  }

  obtenerProductos(): Observable<Producto[]> {
    return this.getAuthHeaders().pipe(
      switchMap((headers) => this.http.get<Producto[]>(this.apiUrl, { headers }))
    );
  }

  agregarProducto(producto: Omit<Producto, 'id_producto'>): Observable<Producto> {
    return this.getAuthHeaders().pipe(
      switchMap((headers) =>
        this.http.post<Producto>(this.apiUrl, producto, { headers })
      )
    );
  }

  actualizarProducto(
    id: number,
    producto: Omit<Producto, 'id_producto'>
  ): Observable<Producto> {
    return this.getAuthHeaders().pipe(
      switchMap((headers) =>
        this.http.put<Producto>(`${this.apiUrl}/${id}`, producto, { headers })
      )
    );
  }

  eliminarProducto(id: number): Observable<string> {
    return this.getAuthHeaders().pipe(
      switchMap((headers) =>
        this.http.delete(`${this.apiUrl}/${id}`, {
          headers,
          responseType: 'text'
        })
      )
    );
  }
}
