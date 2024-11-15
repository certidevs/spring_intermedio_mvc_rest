

## SPRING SECURITY

Spring Security es un framework de seguridad que se puede utilizar de forma independiente a Spring.

* Controladores MVC con autenticación básica con sesiones
  * almacena datos de la sesión en memoria y por tanto afecta a la escalabilidad
  * UserDetails
  * UserDetailsServiceImpl
  * SecurityConfig @Configuration

* Controladores API REST con autenticación tokens JWT
  * no almacena datos en sesión en memoria
  * almacena datos no sensibles como id de user, rol, en el token base64

Proceso de autenticación JWT:

* /api/users/register creas un usuario en base de datos con contraseña cifrada BCryptPasswordEncoder

* /api/users/login verificar usuario y contraseña y si todo OK entonces genera un token JWT firmado con clave secreta en el server

* El login devuelve un token que usará el cliente (angular, spring boot, test integración) para enviarlo en las siguientes peticiones en la cabecera Authentication

* El backend recibe la nueva petición , extrae el token de header Authentication y lo valida con la clave secreta a ver si la firma es correcta y el usuario existe y le pasa el usuario a Spring Security en SecurityContextHolder.

* SecurityConfig configura la seguridad de las rutas de los controladores y Spring ya sabiendo quien es el usuario juzga si puede pasar o no