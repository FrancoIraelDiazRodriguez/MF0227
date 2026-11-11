# MF0227 · Iparhotel

Gestión de reservas del hotel rural **Iparhotel**, construida en tres fases sobre un único proyecto Maven (`iparhotel/`).

| Fase | Contenido | Estado |
|------|-----------|--------|
| 1 | Modelo orientado a objetos: jerarquía `Habitacion` (Individual, Doble, Suite), interfaz `Promocionable`, `Reserva`, `ReservaException`, polimorfismo y streams | ✔ |
| 2 | Aplicación Spring Boot: JPA (`SINGLE_TABLE`), servicios con DTOs, vistas Thymeleaf y Spring Security | ✔ |
| 3 | API REST con JWT, Angular y ciclo de vida | pendiente |

## Requisitos

- Java 21 y Maven 3.9+
- MySQL 8 en `localhost:3306` (la base de datos `iparhotel` se crea sola)

Usuario y contraseña de MySQL por defecto: `root` / `root`. Se cambian con variables de entorno:

```bash
export DB_USER=root
export DB_PASSWORD=tu_contraseña
```

## Arrancar la web (fase 2)

```bash
cd iparhotel
mvn spring-boot:run
```

Abre http://localhost:8080. Al arrancar con la base de datos vacía se cargan los datos de ejemplo:

| Usuario | Contraseña | Rol |
|---------|------------|-----|
| admin@iparhotel.com | admin123 | ADMINISTRADOR |
| ana@iparhotel.com | ana123 | CLIENTE |
| javier@iparhotel.com | javier123 | CLIENTE |

Habitaciones: 101 y 102 (Individual), 201 y 202 (Doble, la 202 con cama supletoria y ya reservada por Ana), 301 y 302 (Suite).

| Ruta | Acceso | Contenido |
|------|--------|-----------|
| `/` | público | habitaciones disponibles (tipo, capacidad, precio/noche) |
| `/habitacion/{id}` | usuarios autenticados | formulario de reserva (fecha de entrada y noches) |
| `/reservas` | usuarios autenticados | reservas del usuario |
| `/admin` | solo ADMINISTRADOR | todas las reservas con el total |
| `/login` | público | página de login propia |

## Prueba de polimorfismo (fase 1)

```bash
cd iparhotel
mvn package
java -cp target/classes com.iparhotel.PruebaPolimorfismo
```

## Pruebas

```bash
cd iparhotel
mvn test
```

Las pruebas usan H2 en memoria, así que no necesitan MySQL: modelo (fase 1), servicio con Mockito,
consultas derivadas con `@DataJpaTest` y web + seguridad con MockMvc.
