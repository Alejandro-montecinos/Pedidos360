import { 
  BrowserCacheLocation, 
  Configuration, 
  LogLevel 
} from '@azure/msal-browser'; 
 
export const tenantId = 
  '092406df-0491-4c9e-8221-43d77ece03ff'; 
 
export const frontendClientId = 
  'f966785a-fe55-4f0b-bbe7-5dcd020b7408'; 
 
export const authority = 
  `https://login.microsoftonline.com/${tenantId}`; 
 
export const redirectUri = 
  'http://localhost:4200'; 

 
export const apiScope = 
  'api://3eeb07f5-efda-489c-a155-f998e6602e7f/access_as_user'; 
 
export const msalConfig: Configuration = { 
  auth: { 
    clientId: frontendClientId, 
    authority: authority, 
    redirectUri: redirectUri, 
    postLogoutRedirectUri: redirectUri 
  }, 
  cache: { 
    cacheLocation: BrowserCacheLocation.LocalStorage 
  }, 
  system: { 
    allowPlatformBroker: false, 
    loggerOptions: { 
      loggerCallback: ( 
        logLevel: LogLevel, 
        message: string, 
        containsPii: boolean 
      ): void => { 
        if (containsPii) { 
          return; 
        } 
 
        console.log(`[MSAL ${LogLevel[logLevel]}] ${message}`); 
      }, 
      logLevel: LogLevel.Info, 
      piiLoggingEnabled: false 
    } 
  } 
}; 
 
export const loginRequest = { 
  scopes: [ 
    'openid', 
    'profile', 
    apiScope 
  ] 
};