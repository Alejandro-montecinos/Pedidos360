import { CommonModule } from '@angular/common'; 
import { Component, OnInit } from '@angular/core'; 
import { AccountInfo } from '@azure/msal-browser'; 
import { MsalService } from '@azure/msal-angular'; 
    
import { 
  loginRequest, 
  redirectUri 
} from './auth-config'; 
 
@Component({ 
  selector: 'app-root', 
  standalone: true, 
  imports: [CommonModule], 
  templateUrl: './app.html', 
  styleUrl: './app.css' 
}) 
export class App implements OnInit { 
 
  title = 'Pedidos360'; 
  usuario: AccountInfo | null = null; 
  cargando = true; 
  mensajeError = ''; 
 
  constructor(private readonly authService: MsalService) {} 
 
  async ngOnInit(): Promise<void> { 
    try { 
      /* 
       * Inicializa MSAL antes de realizar cualquier operación 
       * relacionada con la autenticación. 
       */ 
      await this.authService.instance.initialize(); 
 
      /* 
       * Procesa la respuesta enviada por Microsoft Entra ID 
    
       * después de iniciar sesión. 
       */ 
      const resultado = 
        await this.authService.instance.handleRedirectPromise(); 
 
      if (resultado?.account) { 
        this.authService.instance.setActiveAccount(resultado.account); 
      } 
 
      this.actualizarUsuario(); 
    } catch (error) { 
      console.error('Error al inicializar MSAL:', error); 
 
      this.mensajeError = 
        'No fue posible inicializar la autenticación.'; 
    } finally { 
      this.cargando = false; 
    } 
  } 
 
  iniciarSesion(): void { 
    this.mensajeError = ''; 
 
    this.authService.loginRedirect(loginRequest).subscribe({ 
      error: (error) => { 
        console.error('Error al iniciar sesión:', error); 
 
        this.mensajeError = 
          'No fue posible iniciar sesión con Microsoft.'; 
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
 
        this.mensajeError = 
          'No fue posible cerrar la sesión.'; 
      } 
    }); 
  } 
 
  private actualizarUsuario(): void { 
    const cuentaActiva = 
      this.authService.instance.getActiveAccount(); 
 
    const cuentas = 
      this.authService.instance.getAllAccounts(); 
 
    this.usuario = cuentaActiva ?? cuentas[0] ?? null; 
 
    if (this.usuario && !cuentaActiva) { 
      this.authService.instance.setActiveAccount(this.usuario); 
    } 
  } 
}