# Nisum User API

API REST desarrollada con **Java 17 y Spring Boot** para la gestión de usuarios y sus teléfonos asociados.

El proyecto implementa operaciones CRUD, autenticación mediante JWT, cifrado de contraseñas con BCrypt, persistencia mediante Spring Data JPA y una base de datos H2 en memoria.

## Tecnologías utilizadas

- **Java 17**
- **Spring Boot 3.5.12**
- Spring Web
- Spring Security
- Spring Data JPA
- H2 Database
- JWT (JJWT 0.12.6)
- BCrypt
- Maven
- Lombok
- JUnit 5 y Mockito
- Springdoc OpenAPI / Swagger UI

## Funcionalidades

La API permite:

- Registrar usuarios.
- Consultar usuarios mediante UUID.
- Actualizar completamente sus datos.
- Modificar parcialmente sus datos.
- Eliminar usuarios.
- Asociar múltiples teléfonos a un usuario.
- Validar correos electrónicos y contraseñas mediante expresiones regulares configurables.
- Evitar el registro de correos electrónicos duplicados.
- Generar y validar tokens JWT.
- Proteger los endpoints mediante Spring Security.
- Devolver errores en formato JSON.

## Arquitectura

El proyecto utiliza una **arquitectura por capas**:

| Capa | Responsabilidad |
|---|---|
| **Controller** | Recepción de solicitudes HTTP. |
| **Service** | Implementación de reglas de negocio. |
| **Repository** | Acceso a datos mediante Spring Data JPA. |
| **Entity** | Representación del modelo de persistencia. |
| **DTO** | Objetos utilizados para solicitudes y respuestas. |
| **Security** | Autenticación mediante JWT. |
| **Exception** | Manejo centralizado de errores. |
| **Mapper** | Conversión entre entidades y DTO. |

- **Diagrama de arquitectura:** [`docs/architecture.md`](docs/architecture.md)
- **Script SQL de referencia:** [`src/main/resources/db/schema.sql`](src/main/resources/db/schema.sql)

## Requisitos

- JDK 17 o superior.
- Git para clonar el repositorio.
- Terminal o IDE compatible con Maven.

> El proyecto incluye **Maven Wrapper**, por lo que no es necesario instalar Maven globalmente.

## Configuración

La aplicación utiliza una base de datos **H2 en memoria**. La configuración principal se encuentra en [`src/main/resources/application.properties`](src/main/resources/application.properties).

### Variable de entorno `JWT_SECRET`

Antes de ejecutar la aplicación **o las pruebas**, configura la variable de entorno `JWT_SECRET`.

El secreto debe contener **al menos 256 bits (32 bytes)** y estar codificado en Base64. En macOS o Linux puedes generar uno mediante:

```bash
openssl rand -base64 32
```

Después, configura la variable en la misma terminal donde ejecutarás Maven:

```bash
export JWT_SECRET="CLAVE_GENERADA_EN_BASE64"
```

La aplicación lee el secreto mediante:

```properties
app.jwt.secret=${JWT_SECRET}
```

> **Seguridad:** Nunca publiques el secreto real en GitHub ni lo incluyas en archivos versionados. La variable configurada con `export` se mantiene únicamente en la sesión actual de terminal.

Los tokens tienen una vigencia de **24 horas**, configurable mediante:

```properties
app.jwt.expiration-ms=86400000
```

## Ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/Castletco/nisum-user-api.git
cd nisum-user-api
```

### 2. Configurar el secreto JWT

Sigue las instrucciones de la sección [Configuración](#configuración).

### 3. Iniciar la aplicación

En macOS o Linux:

```bash
./mvnw spring-boot:run
```

En Windows:

```cmd
mvnw.cmd spring-boot:run
```

La API estará disponible en el puerto **8080**.

## Endpoints

| Método | Endpoint | Descripción | Autenticación |
|---|---|---|---|
| `POST` | `/users` | Registrar usuario | No |
| `GET` | `/users/{id}` | Consultar usuario | JWT |
| `PUT` | `/users/{id}` | Actualizar completamente un usuario | JWT |
| `PATCH` | `/users/{id}` | Modificar parcialmente un usuario | JWT |
| `DELETE` | `/users/{id}` | Eliminar usuario | JWT |

## Ejemplo de registro

**Endpoint:** `POST /users`

**Cuerpo de la solicitud:**

```json
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
```

> El campo `contrycode` conserva intencionalmente el nombre definido en la especificación del desafío.

Cuando el registro es exitoso, la API devuelve **HTTP 201 Created** e información del usuario:

- UUID.
- Fecha de creación.
- Fecha de modificación.
- Último inicio de sesión.
- Token JWT.
- Estado activo.

La contraseña **no se incluye en la respuesta**.

## Autenticación

Después de registrar un usuario, utiliza el JWT obtenido para acceder a los endpoints protegidos.

**Ejemplo:**

```bash
curl -X GET "http://localhost:8080/users/UUID_DEL_USUARIO" \
  -H "Authorization: Bearer TOKEN_JWT"
```

La aplicación valida la firma y vigencia del JWT, comprueba que el usuario exista, que esté activo y que el token coincida con el almacenado en la base de datos.

## Respuestas de error

Los errores se devuelven mediante un objeto JSON con la propiedad `mensaje`.

### Correo electrónico duplicado — `409 Conflict`

```json
{
  "mensaje": "El correo ya registrado"
}
```

### Usuario inexistente — `404 Not Found`

```json
{
  "mensaje": "El usuario no existe"
}
```

### Solicitud no autorizada — `401 Unauthorized`

```json
{
  "mensaje": "No autorizado"
}
```

### Contraseña inválida — `400 Bad Request`

```json
{
  "mensaje": "La contraseña no cumple con el formato requerido"
}
```

### Códigos HTTP principales

| Código | Significado |
|---|---|
| `200 OK` | Operación exitosa. |
| `201 Created` | Usuario creado correctamente. |
| `204 No Content` | Usuario eliminado correctamente. |
| `400 Bad Request` | Datos de entrada inválidos. |
| `401 Unauthorized` | Solicitud no autorizada. |
| `404 Not Found` | Usuario inexistente. |
| `409 Conflict` | Correo electrónico duplicado. |

## Swagger / OpenAPI

Con la aplicación en ejecución, la documentación interactiva está disponible en:

**Swagger UI:** http://localhost:8080/swagger-ui/index.html

Swagger permite consultar los endpoints, visualizar sus esquemas, revisar los códigos HTTP y ejecutar solicitudes.

Para probar operaciones protegidas:

1. Registrar un usuario mediante `POST /users`.
2. Copiar el JWT devuelto.
3. Seleccionar **Authorize** en Swagger.
4. Introducir el token.
5. Ejecutar las operaciones protegidas.

## Base de datos

Se utiliza **H2 en memoria**, con la siguiente configuración de Hibernate:

```properties
spring.jpa.hibernate.ddl-auto=create-drop
```

Las tablas se crean al iniciar la aplicación y se eliminan al detenerla. Los datos no persisten entre reinicios.

El modelo contiene dos tablas principales:

- `users`
- `phones`

La relación es **uno a muchos (1:N)**: un usuario puede tener varios teléfonos.

El script SQL de referencia se encuentra en [`src/main/resources/db/schema.sql`](src/main/resources/db/schema.sql).

## Pruebas automatizadas

El proyecto incluye pruebas unitarias implementadas mediante **JUnit 5 y Mockito**, además de una prueba de inicialización del contexto Spring Boot.

Se verifican los siguientes escenarios:

- Rechazo de correos electrónicos duplicados.
- Registro exitoso y generación del JWT.
- Actualización de los datos de un usuario.
- Eliminación de usuarios existentes.
- Inicialización del contexto de la aplicación.

**Ejecutar las pruebas:**

```bash
./mvnw test
```

**Realizar una compilación limpia y ejecutar las verificaciones:**

```bash
./mvnw clean verify
```

> **Importante:** `JWT_SECRET` debe estar configurada antes de ejecutar la aplicación o las pruebas, ya que estas requieren inicializar el contexto de Spring Boot.

## Consideraciones

- La base de datos es temporal y se reinicia con la aplicación.
- Las contraseñas se almacenan mediante BCrypt.
- El secreto JWT se obtiene desde una variable de entorno.
- Los tokens tienen una vigencia configurable.
- La API utiliza respuestas JSON.
- La documentación Swagger está disponible sin autenticación.

---

**Nisum User API — Desafío técnico Backend Java**
