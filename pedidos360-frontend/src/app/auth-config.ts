import {
BrowserCacheLocation,
Configuration,
LogLevel
} from '@azure/msal-browser';

export const tenantId =
'86938a4d-8211-441d-8c29-09b3a9a53b48';

export const frontendClientId =
'2d53fcc2-04a2-4f88-b43f-9a48ec27d992';

export const authority =
`https://login.microsoftonline.com/${tenantId}`;

export const redirectUri =
  typeof window !== 'undefined' && window.location && window.location.origin
    ? window.location.origin
    : 'http://localhost:4200';

export const apiScope =
'api://68ea7d4c-e7d0-41bf-80da-b4efd986c72b/access_as_user';

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