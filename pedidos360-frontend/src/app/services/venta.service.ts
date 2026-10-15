import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MsalService } from '@azure/msal-angular';
import { Observable, from, switchMap, catchError, throwError } from 'rxjs';
import { apiScope } from '../auth-config';

export interface Venta {
  id_venta?: number;
  nombreCliente: string;
  nombreVendedor: string;
  fechaVenta: string;
  totalVenta: number;
  metodoPago: string;
  producto_id_producto: number;
}

export interface CrearVenta {
  nombreCliente: string;
  nombreVendedor: string;
  fechaVenta: string;
  totalVenta: number;
  metodoPago: string;
  producto_id_producto: number;
}

@Injectable({
  providedIn: 'root'
})
export class VentaService {
  private get apiUrl(): string {
    if (typeof window !== 'undefined' && window.location.port === '4200') {
      return 'http://localhost:8080/api/v1/ventas';
    }
    return `${window.location.origin}/api/v1/ventas`;
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
        console.error('Error al adquirir token para ventas:', error);
        return throwError(
          () =>
            new Error(
              'No fue posible obtener la autorización para el BFF. Vuelve a iniciar sesión.'
            )
        );
      })
    );
  }

  obtenerVentas(): Observable<Venta[]> {
    return this.getAuthHeaders().pipe(
      switchMap((headers) => this.http.get<Venta[]>(this.apiUrl, { headers }))
    );
  }

  registrarVenta(venta: CrearVenta): Observable<Venta> {
    return this.getAuthHeaders().pipe(
      switchMap((headers) => this.http.post<Venta>(this.apiUrl, venta, { headers }))
    );
  }
}
