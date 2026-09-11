import {
BrowserCacheLocation,
Configuration,
LogLevel
} from '@azure/msal-browser';

export const tenantId =
'092406df-0491-4c9e-8221-43d77ece03ff';

export const frontendClientId =
'e0f115f7-d16f-4f4f-88e8-959e891083e2';

export const authority =
`https://login.microsoftonline.com/${tenantId}`;

export const redirectUri =
  typeof window !== 'undefined' && window.location && window.location.origin
    ? window.location.origin
    : 'http://localhost:4200';

export const apiScope =
'api://b419846b-7b38-46ba-96a5-7e18e287a476/access_as_user';

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