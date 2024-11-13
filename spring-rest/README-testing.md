

## TESTING UNITARIO

* JUnit 5 + Mockito
* No se carga Spring
* Se prueban métodos de Java
* No se carga la base de datos
* Rápidos
* Pequeños
* Medibles
* Confiables

* @ExtendWith con Mockito a nivel de clase de test
* @Mock de Mockito para crear Mocks de repositorios, servicios,....
* @InjectMocks para inyectar los mocks en la clase a testear (System Under Test - SUT)

* https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html
* https://junit.org/junit5/docs/current/user-guide/#writing-tests


## TESTING INTEGRACIÓN PARCIAL

* JUnit 5 + Mockito + Spring Test
* Se carga solo una parte de la aplicación Spring, por ejemplo un controlador
* No se carga la base de datos
* Sí puede probar las rutas de Spring y parámetros de Spring
* Más lento que el unitario ya que tiene que cargar Spring

* @WebMvcTest(ProductController.class)
* @MockBean para crear mocks de repositorios, servicios
* @Autowired MockMvc objeto que sirve para lanzar y testear peticiones HTTP a los controladores



## TESTING INTEGRACIÓN COMPLETA

* JUnit 5 + Spring Test
* Se carga la aplicación de Spring completa
* Se carga la base de datos y depencias
* Más lento aún
* Prueba la integración de todas las capas tal y como sería una ejecución real en producción
* No usaríamos Mocks.
* Desafío: los sistemas externos
* Uso de TestContainers para sistemas externos

* @SpringBootTest


## TESTING DE INTERFAZ DE USUARIO / FUNCIONAL

* JUnit 5 + Selenium
* Pruebas de la interfaz de usuario
* Se carga Spring y una base datos y toda la aplicación entera
* Se navega por la UI como un usuario real realizando acciones
* Más lento aún