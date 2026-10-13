-- =====================================================================
--  API Biblioteca - script para MySQL Workbench
--  Crea la base de datos, las 3 tablas y datos de prueba.
--  Ejecutar completo con el rayo (Execute). Se puede volver a ejecutar
--  para dejar los datos como al inicio.
-- =====================================================================

DROP DATABASE IF EXISTS libreria;
CREATE DATABASE libreria CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE libreria;

-- Libro
CREATE TABLE libros (
    isbn              BIGINT       NOT NULL AUTO_INCREMENT,
    titulo            VARCHAR(255) NOT NULL,
    autor             VARCHAR(255) NOT NULL,
    año_publicacion   VARCHAR(255),
    numero_ejemplares INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (isbn)
);

-- Usuario (socio)
CREATE TABLE usuarios (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    nombre           VARCHAR(255) NOT NULL,
    apellidos        VARCHAR(255) NOT NULL,
    correo           VARCHAR(255) NOT NULL,
    contrasena       VARCHAR(255) NOT NULL,
    rol              VARCHAR(255),
    estado           VARCHAR(255),
    fecha_creacion   VARCHAR(255),
    fecha_nacimiento VARCHAR(255),
    telefono         VARCHAR(255),
    direccion        VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT uk_usuarios_correo UNIQUE (correo)
);

-- Préstamo: relaciona un libro con el usuario que lo tiene
CREATE TABLE prestamos (
    id               BIGINT NOT NULL AUTO_INCREMENT,
    libro_isbn       BIGINT NOT NULL,
    usuario_id       BIGINT NOT NULL,
    fecha_prestamo   DATE   NOT NULL,
    fecha_devolucion DATE,             -- NULL mientras el préstamo siga activo
    PRIMARY KEY (id),
    CONSTRAINT fk_prestamos_libro   FOREIGN KEY (libro_isbn) REFERENCES libros (isbn),
    CONSTRAINT fk_prestamos_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);

-- =====================================================================
--  Datos de prueba (contraseñas de ejemplo, no reales)
-- =====================================================================
INSERT INTO libros (titulo, autor, año_publicacion, numero_ejemplares) VALUES
('Cien años de soledad',        'Gabriel García Márquez', '1967', 3),
('El principito',               'Antoine de Saint-Exupéry', '1943', 2),
('Clean Code',                  'Robert C. Martin',       '2008', 1),
('Don Quijote de la Mancha',    'Miguel de Cervantes',    '1605', 4);

INSERT INTO usuarios (nombre, apellidos, correo, contrasena, rol, estado, fecha_creacion, fecha_nacimiento, telefono, direccion) VALUES
('Ana',   'García López',  'ana@correo.com',   'demo1234', 'Socio',         'Activo',   '2026-09-01', '2000-05-10', '3001234567', 'Calle 10 # 5-20'),
('Luis',  'Pérez Gómez',   'luis@correo.com',  'demo1234', 'Socio',         'Activo',   '2026-09-05', '1998-11-02', '3109876543', 'Carrera 7 # 45-10'),
('María', 'Rodríguez',     'maria@correo.com', 'demo1234', 'Bibliotecario', 'Activo',   '2026-08-20', '1995-03-15', '3204567890', 'Av. 68 # 12-30'),
('Pedro', 'Suspendido',    'pedro@correo.com', 'demo1234', 'Socio',         'Inactivo', '2026-07-10', '2001-01-20', '3151112233', 'Calle 80 # 20-15');

INSERT INTO prestamos (libro_isbn, usuario_id, fecha_prestamo, fecha_devolucion) VALUES
(1, 1, '2026-09-20', '2026-10-01'),   -- devuelto
(3, 2, '2026-10-05', NULL),           -- activo (Clean Code no tiene más ejemplares)
(2, 1, '2026-10-08', NULL);           -- activo
