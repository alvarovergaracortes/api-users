# 🧩 Hexagonal User-API - Sermaluc

Aplicación de ejemplo con arquitectura hexagonal (puertos y adaptadores) utilizando **Spring Boot 3.4.4**, **Java 21** y **HSQLDB** en memoria.  
Exposición de una API RESTful para la creación de usuarios con validaciones, autenticación JWT, documentación Swagger, pruebas unitarias e integración.

## 📌 Endpoints

```plaintext
| Método HTTP | Endpoint                          | Descripción                    |
|-------------|-----------------------------------|--------------------------------|
| POST        | http://localhost:8082/users       | Crea un nuevo usuario          |
| GET         | http://localhost:8082/users       | Obtiene todos los usuarios     |
| GET         | http://localhost:8082/users/{id}  | Obtiene un usuario por ID      |
| PUT         | http://localhost:8082/users/{id}  | Actualiza un usuario existente |
| DELETE      | http://localhost:8082/users/{id}  | Elimina un usuario por ID      |
```

---

## 🚀 Características Destacadas

- Arquitectura hexagonal con enfoque vertical slicing
- API RESTful para gestión de usuarios
- Validaciones con Bean Validation
- Seguridad JWT (HS256)
- Base de datos en memoria (HSQLDB)
- Documentación Swagger/OpenAPI
- Pruebas unitarias con JUnit 5 y Mockito
- Logging por consola y archivo (`logs/api-users.log`)

---

## 🛠️ Tecnologías Usadas

- Java 21  
- Spring Boot 3.4.4  
- Spring Web / Security / Validation / Data JPA  
- HSQLDB  
- JSON Web Token (JJWT)  
- Swagger / Springdoc OpenAPI  
- JUnit 5 + Mockito  
- Log4j2 (vía SLF4J)
- Notebook Linux (Ubuntu)

---

## ▶️ Cómo ejecutar la aplicación

### Prerrequisitos
- Java 21  
- Maven 3.9+

### Clonar y compilar

```bash
git clone https://github.com/alvarovergaracortes/api-users.git
cd api-users
mvn clean install
```

### Ejecutar la aplicación localmente

```bash
mvn spring-boot:run
```

La aplicación estará disponible en:  
📍 http://localhost:8082

---

## 📤 Ejemplo de solicitud (POST /users)

```json
{
  "name": "Juan Perez",
  "email": "juanperez@perez.com",
  "password": "hunteR2",
  "phones": [
    {
      "number": "1234567",
      "citycode": "1",
      "contrycode": "57"
    }
  ]
}
```

---

## 📥 Ejemplo de respuesta exitosa

```json
{
  "id": "f19d0995-864c-4b44-87ea-f3a251d8d98c",
  "name": "Juan Rodriguez",
  "email": "juanperez@perez.com",
  "created": "2025-04-25T18:26:43.595000366",
  "modified": "2025-04-25T18:26:43.595000366",
  "lastLogin": "2025-04-25T18:26:43.595000366",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ...",
  "active": true
}
```

---

## 🔐 Flujo de Autenticación JWT

1. Al crear un usuario (POST `/users`), se genera un **JWT** automáticamente.
2. Este token se devuelve en el campo `token` del JSON de respuesta.
3. Para acceder a los endpoints protegidos (`GET`, `PUT`, `DELETE`), se debe incluir el token como encabezado:

```http
Authorization: Bearer <TOKEN>
```

4. Un filtro personalizado (`AuthenticationFilter`) valida el token y permite el acceso si es válido.
5. La clave se configura en `application.properties`:

```properties
jwt.secret=clave_secreta_con_al_menos_32_caracteres
```

6. Lo correcto seria dejar la clave en una **variable de entorno**:  export JWT_SECRET=.....
7. Luego en el properties quedaria de la forma: jwt.secret=${JWT_SECRET}

---

## 🌐 Pruebas con Postman

Se incluye una colección exportada en:  
📁 `docs/api-sermaluc.postman_collection.json`

> ⚠️ **Recuerda:** Para ejecutar `GET`, `PUT`, `DELETE`, se requiere agregar en el header el token generado en el login:  

```http
Authorization: Bearer <TOKEN>
```

---

## 🧪 Pruebas Unitarias

Para ejecutar las pruebas:

```bash
mvn test
```

- Usa `@WebMvcTest` y `@MockitoBean`  
- Mock de JWT y capa de servicio  
- Valida status HTTP, JSON y estructura

---

## 🗃️ Base de Datos

- Se usa **HSQLDB** en modo **en memoria**  
- Script de creación en:
  📄 `src/main/resources/schema.sql`

---

## 📘 Documentación Técnica

En la carpeta `docs/` encontrarás:

- Proyecto Postman con nombre: `api-users.postman_collection.json`
- `DiagramaSolucion.docx`
- `Ejercicio_JAVA-Especialista_Integracion-BCI.PDF`

Swagger UI disponible en:

- 📍 `http://localhost:8082/swagger-ui/index.html`
- 📍 `http://localhost:8082/v3/api-docs`

---

## 👤 Autor

Álvaro Vergara Cortés  
📧 alvaro.vergara.cl@gmail.com