

## TESTING UNITARIO

* JUnit 5 + Mockito
* No se carga Spring
* Se prueban métodos de Java
* No se carga la base de datos
* Rápidos
* Pequeños
* Medibles
* Confiables

## TESTING INTEGRACIÓN PARCIAL

* JUnit 5 + Mockito + Spring Test
* Se carga solo una parte de la aplicación Spring, por ejemplo un controlador
* No se carga la base de datos
* Sí puede probar las rutas de Spring y parámetros de Spring
* Más lento que el unitario ya que tiene que cargar Spring

## TESTING INTEGRACIÓN COMPLETA

* JUnit 5 + Spring Test
* Se carga la aplicación de Spring completa
* Se carga la base de datos y depencias
* Más lento aún
* Prueba la integración de todas las capas tal y como sería una ejecución real en producción

## TESTING DE INTERFAZ DE USUARIO / FUNCIONAL

* JUnit 5 + Selenium
* Pruebas de la interfaz de usuario
* Se carga Spring y una base datos y toda la aplicación entera
* Se navega por la UI como un usuario real realizando acciones
* Más lento aún