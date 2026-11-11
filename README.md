# MF0227 · Iparhotel

Gestión de reservas del hotel rural **Iparhotel**, construida en tres fases:

- `iparhotel/`: proyecto Java (Maven) que va creciendo fase a fase: modelo, web Spring Boot y API REST.
- `iparhotel-web/`: aplicación Angular para que los clientes reserven (fase 3).
- `docs/Iparhotel_Memoria.pdf`: memoria del proyecto con el análisis y diseño (historias de usuario y diagramas UML) y capturas de cada fase.

| Fase | Contenido | Estado |
|------|-----------|--------|
| 1 | Modelo orientado a objetos: jerarquía `Habitacion` (Individual, Doble, Suite), interfaz `Promocionable`, `Reserva`, `ReservaException`, polimorfismo y streams | ✔ |
| 2 | Aplicación Spring Boot: JPA (`SINGLE_TABLE`), servicios con DTOs, vistas Thymeleaf y Spring Security | ✔ |
| 3 | API REST con JWT bajo `/api/v2`, aplicación Angular y memoria de análisis y diseño (PDF) | ✔ |

## Requisitos

- Java 21 y Maven 3.9+
- MySQL 8 en `localhost:3306` (la base de datos `iparhotel` se crea sola)
- Node.js 22 para la aplicación Angular

Usuario y contraseña de MySQL por defecto: `root` / `root`. Se cambian con variables de entorno:

```bash
export DB_USER=root
export DB_PASSWORD=tu_contraseña
```

Secreto para firmar los JWT (obligatorio, al menos 32 caracteres; sin él la aplicación no arranca):

```bash
export JWT_SECRETO=un-secreto-largo-de-al-menos-32-caracteres
```

## Arrancar la web y la API (fases 2 y 3)

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

## API REST (fase 3)

| Método y ruta | Acceso | Respuesta |
|---------------|--------|-----------|
| `GET /api/v2/habitaciones` | público | habitaciones disponibles |
| `POST /api/v2/usuarios/login` | público | `{"accessToken":"...","tokenType":"Bearer"}` o 401 |
| `POST /api/v2/reservas` | con token | 201 con la reserva, 400 datos no válidos, 404 no existe, 409 no disponible |

## Aplicación Angular (fase 3)

```bash
cd iparhotel-web
npm install
npx ng serve
```

Abre http://localhost:4200 (la API tiene que estar en marcha en el 8080).

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
consultas derivadas con `@DataJpaTest`, web + seguridad y API con MockMvc (39 pruebas).

```bash
cd iparhotel-web
npx ng test --watch=false
```

## Memoria en PDF

`docs/Iparhotel_Memoria.pdf` se genera desde `docs/fuente/memoria.html`:

```bash
python3 docs/fuente/diagramas.py docs/capturas
node docs/fuente/generar-pdf.js
```
