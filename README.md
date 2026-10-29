Nisum User API

API REST desarrollada con Java 17 y Spring Boot para la gestión de usuarios y sus teléfonos asociados.

El proyecto implementa operaciones CRUD, autenticación mediante JWT, cifrado de contraseñas con BCrypt, persistencia mediante Spring Data JPA y una base de datos H2 en memoria.

Tecnologías utilizadas

* Java 17
* Spring Boot 3.5.12
* Spring Web
* Spring Security
* Spring Data JPA
* H2 Database
* JWT (JJWT 0.12.6)
* BCrypt
* Maven
* Lombok
* JUnit 5 y Mockito
* Springdoc OpenAPI / Swagger UI

Funcionalidades

La API permite:

* Registrar usuarios.
* Consultar usuarios mediante UUID.
* Actualizar completamente sus datos.
* Modificar parcialmente sus datos.
* Eliminar usuarios.
* Asociar múltiples teléfonos a un usuario.
* Validar correos electrónicos y contraseñas mediante expresiones regulares configurables.
* Evitar el registro de correos electrónicos duplicados.
* Generar y validar tokens JWT.
* Proteger los endpoints mediante Spring Security.
* Devolver errores en formato JSON.

Arquitectura

El proyecto utiliza una arquitectura por capas:

* Controller: recepción de solicitudes HTTP.
* Service: implementación de reglas de negocio.
* Repository: acceso a datos mediante Spring Data JPA.
* Entity: representación del modelo de persistencia.
* DTO: objetos utilizados para solicitudes y respuestas.
* Security: autenticación mediante JWT.
* Exception: manejo centralizado de errores.
* Mapper: conversión entre entidades y DTO.

El diagrama de arquitectura está disponible en:

docs/architecture.md

El script SQL de creación de tablas se encuentra en:

src/main/resources/db/schema.sql

Requisitos

Para ejecutar el proyecto se necesita:

* JDK 17 o superior.
* Git para clonar el repositorio.
* Un terminal o IDE compatible con Maven.

El proyecto incluye Maven Wrapper, por lo que no es necesario instalar Maven globalmente.

Configuración

La aplicación utiliza una base de datos H2 en memoria.

La configuración se encuentra en:

src/main/resources/application.properties

Variable de entorno JWT_SECRET

Antes de ejecutar la aplicación, es necesario configurar la variable de entorno JWT_SECRET.

El valor debe ser una clave criptográfica codificada en Base64, con al menos 256 bits de longitud.

En macOS o Linux se puede generar una clave mediante:

openssl rand -base64 32

Luego se configura la variable de entorno:

export JWT_SECRET="CLAVE_GENERADA_EN_BASE64"

La aplicación utiliza esta propiedad:

app.jwt.secret=${JWT_SECRET}

Importante: no se debe publicar la clave real en el repositorio.

La expiración de los tokens está configurada en 24 horas:

app.jwt.expiration-ms=86400000

Ejecución

Clonar el repositorio:

git clone URL_DEL_REPOSITORIO

Ingresar al directorio:

cd user-api

Ejecutar la aplicación:

./mvnw spring-boot:run

En Windows:

mvnw.cmd spring-boot:run

La API estará disponible en el puerto 8080.

Endpoints

Método	Endpoint	Descripción	Autenticación
POST	/users	Registrar usuario	No
GET	/users/{id}	Consultar usuario	JWT
PUT	/users/{id}	Actualizar usuario	JWT
PATCH	/users/{id}	Modificar parcialmente un usuario	JWT
DELETE	/users/{id}	Eliminar usuario	JWT

Ejemplo de registro

POST /users

{
"name": "Juan Rodriguez",
"email": "juan@rodriguez.org",
"password": "hunter2",
"phones": [
{
"number": "1234567",
"citycode": "1",
"contrycode": "57"
}
]
}

El campo contrycode conserva el nombre definido en la especificación del desafío.

Cuando el registro es exitoso, la API responde con HTTP 201 Created e información del usuario, incluyendo:

* UUID.
* Fecha de creación.
* Fecha de modificación.
* Último inicio de sesión.
* Token JWT.
* Estado activo.

La contraseña no se incluye en la respuesta.

Autenticación

Después de registrar un usuario, se debe utilizar el JWT obtenido para acceder a los endpoints protegidos.

Ejemplo:

curl -X GET "http://localhost:8080/users/UUID_DEL_USUARIO" \
-H "Authorization: Bearer TOKEN_JWT"

La aplicación valida la firma y vigencia del JWT, comprueba que el usuario exista, que esté activo y que el token coincida con el almacenado en la base de datos.

Respuestas de error

Los errores se devuelven mediante un objeto JSON con la propiedad mensaje.

Ejemplo de correo duplicado:

{
"mensaje": "El correo ya registrado"
}

Ejemplo de usuario inexistente:

{
"mensaje": "El usuario no existe"
}

Ejemplo de solicitud no autorizada:

{
"mensaje": "No autorizado"
}

Swagger / OpenAPI

La documentación interactiva está disponible en:

http://localhost:8080/swagger-ui/index.html

Swagger permite consultar los endpoints, visualizar sus esquemas y ejecutar solicitudes.

Para probar operaciones protegidas:

1. Registrar un usuario mediante POST.
2. Copiar el JWT devuelto.
3. Seleccionar Authorize.
4. Introducir el token.
5. Ejecutar las operaciones protegidas.

Base de datos

Se utiliza H2 en memoria.

La configuración de Hibernate es:

spring.jpa.hibernate.ddl-auto=create-drop

Por lo tanto, las tablas se crean al iniciar la aplicación y se eliminan al detenerla.

El modelo contiene dos tablas principales:

* users
* phones

La relación es de uno a muchos: un usuario puede tener varios teléfonos.

El script SQL de referencia se encuentra en:

src/main/resources/db/schema.sql

Pruebas automatizadas

El proyecto incluye pruebas unitarias implementadas mediante JUnit 5 y Mockito.

Se verifican los siguientes escenarios:

* Rechazo de correos electrónicos duplicados.
* Registro exitoso y generación del JWT.
* Actualización de los datos de un usuario.
* Eliminación de usuarios existentes.

Para ejecutar las pruebas:

./mvnw test

Consideraciones

* La base de datos es temporal y se reinicia con la aplicación.
* Las contraseñas se almacenan mediante BCrypt.
* El secreto JWT se obtiene desde una variable de entorno.
* Los tokens tienen una vigencia configurable.
* La API utiliza respuestas JSON.
* La documentación Swagger está disponible sin autenticación.