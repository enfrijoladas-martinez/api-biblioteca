# API Biblioteca

API REST para gestionar una biblioteca: el catálogo de **libros**, los **usuarios** (socios) y los **préstamos** de libros. Controla cuántos ejemplares quedan disponibles, registra la fecha de préstamo automáticamente y deja la fecha de devolución vacía mientras el préstamo siga activo.

## Tecnologías

- Java 25 (compila con JDK 25 o superior)
- Spring Boot 4.1.1: Spring Web MVC y Spring Data JPA
- MySQL 8 o superior (driver `mysql-connector-j`)
- Maven (incluido con `mvnw`)
- Postman para las pruebas

## Estructura

```
src/main/java/com/api/libreria/
├── models/         Entidades JPA: LibroModel, UsuarioModel, PrestamoModel
├── repositories/   Interfaces que extienden JpaRepository
├── services/       Lógica de negocio y validaciones
├── controllers/    Controladores REST (@RestController)
└── exceptions/     Manejo global de errores (400 / 404 en JSON)
database/libreria.sql                     Script de la base de datos con datos de prueba
postman/Biblioteca.postman_collection.json Colección de Postman
```

## Instalación y ejecución

1. Clona el repositorio:
   ```bash
   git clone https://github.com/enfrijoladas-martinez/api-biblioteca.git
   cd api-biblioteca
   ```
2. Crea la base de datos: abre `database/libreria.sql` en MySQL Workbench y ejecútalo. Crea la base `libreria` con datos de prueba. Si te saltas este paso, la API crea las tablas vacías al arrancar.
3. Configura las variables de entorno: copia `.env.example` como `.env` y pon tu contraseña de MySQL.
   ```bash
   cp .env.example .env
   ```
4. Ejecuta la API:
   ```bash
   ./mvnw spring-boot:run
   ```
   En Windows: `mvnw.cmd spring-boot:run`. También puedes abrir la carpeta en VS Code y ejecutar `LibreriaApplication.java`.
5. La API queda en `http://localhost:8081`. Prueba con `GET http://localhost:8081/libros`.

## Variables de entorno

Se leen del sistema o del archivo `.env` en la raíz del proyecto. El archivo `.env` no se sube al repositorio.

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `DB_HOST` | Servidor de MySQL | `localhost` |
| `DB_PORT` | Puerto de MySQL | `3306` |
| `DB_NAME` | Nombre de la base de datos | `libreria` |
| `DB_USERNAME` | Usuario de MySQL | `root` |
| `DB_PASSWORD` | Contraseña de MySQL | *(obligatoria)* |
| `SERVER_PORT` | Puerto de la API | `8081` |

## Endpoints

### Libros `/libros`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/libros` | Lista todos los libros |
| GET | `/libros/{isbn}` | Obtiene un libro |
| POST | `/libros` | Crea un libro |
| PUT | `/libros/{isbn}` | Actualiza un libro |
| DELETE | `/libros/{isbn}` | Elimina un libro (si no tiene préstamos) |
| GET | `/libros/{isbn}/disponibles` | Ejemplares disponibles (total − préstamos activos) |
| GET | `/libros/autor/{autor}` | Busca por autor |
| GET | `/libros/titulo/{titulo}` | Busca por título |

### Usuarios `/usuarios`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/usuarios` | Lista todos los usuarios |
| GET | `/usuarios/{id}` | Obtiene un usuario |
| POST | `/usuarios` | Crea un usuario (estado `Activo` y fecha de creación automáticos) |
| PUT | `/usuarios/{id}` | Actualiza un usuario |
| DELETE | `/usuarios/{id}` | Elimina un usuario (si no tiene préstamos) |

### Préstamos `/prestamos`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/prestamos` | Lista todos los préstamos |
| GET | `/prestamos/{id}` | Obtiene un préstamo |
| GET | `/prestamos/activos` | Préstamos sin devolver |
| GET | `/prestamos/usuario/{id}` | Préstamos de un usuario |
| GET | `/prestamos/libro/{isbn}` | Préstamos de un libro |
| POST | `/prestamos` | Presta un libro (fecha de préstamo = hoy) |
| PUT | `/prestamos/{id}/devolver` | Registra la devolución (fecha de devolución = hoy) |
| DELETE | `/prestamos/{id}` | Elimina un préstamo ya devuelto |

## Reglas y validaciones

- Título y autor son obligatorios; el número de ejemplares no puede ser negativo ni menor que los préstamos activos.
- Nombre, apellidos, correo válido y contraseña de al menos 6 caracteres son obligatorios. El correo es único.
- La contraseña se recibe pero **nunca se devuelve** en las respuestas.
- Solo se presta un libro si quedan ejemplares, si el usuario está `Activo` y si no tiene ya ese libro prestado.
- Un préstamo no se puede devolver dos veces ni eliminar mientras esté activo.
- No se puede eliminar un libro o usuario con préstamos registrados.

| Código | Cuándo |
|---|---|
| 200 OK | Consulta o actualización correcta |
| 201 Created | Recurso creado |
| 204 No Content | Usuario eliminado |
| 400 Bad Request | Dato inválido o regla de negocio incumplida |
| 404 Not Found | El recurso no existe |

## Ejemplos

### Crear un libro

`POST /libros`

```json
{
  "titulo": "Rayuela",
  "autor": "Julio Cortázar",
  "añoPublicacion": "1963",
  "numeroEjemplares": 2
}
```

Respuesta `201 Created`:

```json
{
  "autor": "Julio Cortázar",
  "añoPublicacion": "1963",
  "isbn": 5,
  "numeroEjemplares": 2,
  "titulo": "Rayuela"
}
```

### Crear un usuario

`POST /usuarios`

```json
{
  "nombre": "Carlos",
  "apellidos": "Ramírez Díaz",
  "correo": "carlos@correo.com",
  "contrasena": "demo1234",
  "rol": "Socio",
  "fechaNacimiento": "1999-07-21",
  "telefono": "3017778899",
  "direccion": "Calle 45 # 10-12"
}
```

Respuesta `201 Created` (sin la contraseña):

```json
{
  "nombre": "Carlos",
  "apellidos": "Ramírez Díaz",
  "correo": "carlos@correo.com",
  "rol": "Socio",
  "estado": "Activo",
  "fechaCreacion": "2026-10-13",
  "fechaNacimiento": "1999-07-21",
  "telefono": "3017778899",
  "direccion": "Calle 45 # 10-12",
  "id": 5
}
```

### Prestar un libro

`POST /prestamos`

```json
{
  "libro":   { "isbn": 4 },
  "usuario": { "id": 3 }
}
```

Respuesta `201 Created` (resumida):

```json
{
  "id": 4,
  "activo": true,
  "fechaPrestamo": "2026-10-13",
  "fechaDevolucion": null,
  "libro":   { "isbn": 4, "titulo": "Don Quijote de la Mancha", "autor": "Miguel de Cervantes", "numeroEjemplares": 5 },
  "usuario": { "id": 3, "nombre": "María", "apellidos": "Rodríguez", "estado": "Activo" }
}
```

### Devolver un libro

`PUT /prestamos/4/devolver` → `200 OK` con `"activo": false` y `"fechaDevolucion": "2026-10-13"`.

### Error de validación

`POST /prestamos` con un libro sin ejemplares disponibles → `400 Bad Request`:

```json
{
  "timestamp": "2026-10-13T11:38:49.335142400",
  "status": 400,
  "error": "Bad Request",
  "message": "No quedan ejemplares disponibles de \"Clean Code\""
}
```

## Pruebas con Postman

Importa `postman/Biblioteca.postman_collection.json` y ejecuta las carpetas en orden (Libros → Usuarios → Préstamos) sobre la base recién creada con `database/libreria.sql`. Incluye casos de error marcados con `(400)` o `(404)`.
