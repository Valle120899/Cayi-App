# Cayi-App
Proyecto de programación móvil- UCA

Link de descarga de la app en la PlayStore: https://play.google.com/store/apps/details?id=com.drma.mycayiapp

Quickblox android SDK

En conjunto:
Quickblox es un proveedor de servicios de chat y videollamadas. La plataforma proporciona chat utilizando el protocolo XMPP, señalización WebRTC para llamadas de video / voz y API para enviar notificaciones push. En el proyecto, la parte empleada fue la videollamada.

Configuración de credenciales:
Dentro de cada proyecto utilizando el API de Quickblox, se es necesario primero que nada, mandar a pedir el servicio, y para ello se debe crear previamente una cuenta en https://quickblox.com/ .
Posterior a ello, se procede a crear una app dentro del sitio web y obtener las credenciales correspondientes dentro de la administración de la aplicación creada.
En Android Studio, se busca la clase “App”, y dentro de ella se encontrarán cuatro constantes: APPLICATION_ID, AUTH_KEY, AUTH_SECRET y ACCOUNT_KEY; Habiendo encontrado dichas constantes, se procede a buscar, en el menú de administración de la app, los respectivos códigos para poder empezar a trabajar ya con la API de Quickblox.

![alt text](https://raw.githubusercontent.com/Valle120899/Cayi-App/master/Quick.jpg)

Dependencias quickblox:
dependencies {

    implementation "com.quickblox:quickblox-android-sdk-messages:3.9.2"
    
    implementation "com.quickblox:quickblox-android-sdk-chat:3.9.2"
    
    implementation "com.quickblox:quickblox-android-sdk-content:3.9.2"
    
    implementation "com.quickblox:quickblox-android-sdk-videochat-webrtc:3.9.2"
    
    implementation "com.quickblox:quickblox-android-sdk-conference:3.9.2"
    
    implementation "com.quickblox:quickblox-android-sdk-customobjects:3.9.2"
    
}

Firebase Android:

En conjunto:
Se trata de una plataforma disponible para diferentes plataformas (Android, iOS, web), con lo que de esta forma presentan una alternativa seria a otras opciones para ahorro de tiempo en el desarrollo de aplicaciones móviles.

Configuración:
Firebase trabaja por medio de cuentas asociadas con google, como es el caso de gmail. Posterior entrar a https://firebase.google.com , e iniciar sesión con la respectiva cuenta, se procede a entrar a la consola de firebase y se añade un nuevo proyecto, el cual está relacionado con la app creada.
Se añade la aplicación en el botón “añadir aplicación” y con el archivo “googe-services.json” obtenido, se agrega al directorio principal de la aplicación.
Posterior a ello, se configura la consola de firebase, modificando que la autentificación sea mediante correo, la database esté habilitada y finalmente, que el storage se encuentre en modo de prueba en tiempo real.

![alt text](https://raw.githubusercontent.com/Valle120899/Cayi-App/master/firebase.jpg)

Dependencias de firebase:
dependences{

    implementation "com.google.firebase:firebase-messaging:20.2.0"
    
    implementation 'com.google.firebase:firebase-analytics:17.4.2'
    
    implementation 'com.google.firebase:firebase-database:19.3.0'
    
    implementation 'com.google.firebase:firebase-storage:19.1.1'
    
    implementation 'com.google.firebase:firebase-auth:19.3.1'
    
    implementation 'com.firebaseui:firebase-ui-database:3.2.2'
    
    implementation 'com.squareup.picasso:picasso:2.71828'
    
    implementation "com.google.firebase:firebase-core:17.4.2"
    
}
