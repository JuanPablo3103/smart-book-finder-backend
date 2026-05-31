# Smart Book Finder - Backend

API REST desarrollada con Spring Boot para búsqueda inteligente de libros consumiendo la Open Library API.

## Tecnologías
- Java 21
- Spring Boot 3.5
- Spring Data JPA
- H2 Database (en memoria)
- Maven

## Requisitos
- Java 21+
- Maven 3.9+

## Cómo iniciar el backend

1. Clona el repositorio:
   git clone https://github.com/JuanPablo3103/smart-book-finder-backend.git
   cd smart-book-finder-backend

2. Ejecuta el proyecto:
   mvn spring-boot:run

3. El backend estará disponible en: http://localhost:8080

4. Consola H2 disponible en: http://localhost:8080/h2-console
    - JDBC URL: jdbc:h2:mem:smartbookdb
    - Usuario: sa
    - Contraseña: (vacía)

## Endpoints disponibles

| Método | Endpoint                | Descripción        |
|--------|-------------------------|--------------------|
| POST   | /api/books/search       | Buscar libros      |
| POST   | /api/books/favorites    | Guardar favorito   |
| GET    | /api/books/history      | Ver historial      |
| GET    | /api/books/favorites    | Ver favoritos      |

## Ejecutar pruebas

mvn test

## Ejecutar mutation testing (PIT)

$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot"
mvn org.pitest:pitest-maven:mutationCoverage

## Resultados de pruebas
- 34 pruebas unitarias y MockMvc — 0 fallos
- PIT Mutation Coverage: 100% (31/31 mutantes eliminados)