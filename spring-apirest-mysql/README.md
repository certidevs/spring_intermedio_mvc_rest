

## SEGURIDAD CON SPRING SECURITY:

Spring Security es un framework en sí para la seguridad.

Dependencias:

* jjwt-api
* jjwt-impl
* jjwt-jackson
* spring-boot-starter-security
* spring-security-test

* MVC: Autenticación básica
    * UserDetails
    * UserDetailsServiceImpl
    * SecurityConfig

* API REST: token JWT
    * AuthController / UserController: login, register
    * Entidad User
    * Enum Role o Entidad Role @ManyToMany
    * DTO: Login, Register, Token
    * Filtro de Seguridad: RequestJWTFilter intercepta peticiones y extrae la cabecera Authentication y valida token JWT
    * SecurityConfig @Configuration decirle a spring que use el filtro y securizar rutas a nivel central
    * JwtAuthenticationEntryPoint : customizar errores de seguridad devolver status unauthorized 401

* Esquema de pasos:
  * UserController.register(): crea el usuario en base de datos con la password cifrada
  * UserController.login(): comprueba el usuario y si todo OK crea un token JWT firmado
  * clientes envían peticiones al API REST enviando el token JWT en la cabecera Authorization
  * RequestJWTFilter es un filtro que intercepta las peticiones entrantes al API REST, si todo OK entonces estás autenticado y le pasamos el usuario a Spring en SecurityContextHolder.
  * Spring juzga y permite pasar la petición al destino API REST, o lo rechaza en base a SecurityConfig y anotaciones de seguridad sobre los controladores.

Desde Testing:
* Utilizar el /api/users/login para autenticar un usuario con rol ADMIN y obtener un token JWT
* Utilizar el /api/users/login para autenticar un usuario con rol USER y obtener un token JWT

* mockMvc.perform header Authorization con el token JWT
* andExpect


Seguridad de Rutas:

* SecurityConfig: SecurityFilterChain configura acceso seguro a los controladores de forma centralizada

* (Opcional) Enfoque más granular basado en anotaciones a nivel de métodos de controlador o servicio
  * @Secured
  * @RolesAllowed
  * Para que funcionen hay que activarlas: @EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
  * @PreAuthorize y @PostAuthorize permite agregar más condiciones además de los roles a la evaluación de la seguridad mediante el uso de expresion SpEL.