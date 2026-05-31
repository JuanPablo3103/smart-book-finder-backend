# Lista de CURLs - Smart Book Finder

## 1. Buscar libros por título
curl -X POST http://localhost:8080/api/books/search \
-H "Content-Type: application/json" \
-d "{\"title\": \"Harry Potter\", \"author\": null, \"language\": null, \"publishedAfter\": null}"

## 2. Buscar libros por autor
curl -X POST http://localhost:8080/api/books/search \
-H "Content-Type: application/json" \
-d "{\"title\": null, \"author\": \"Rowling\", \"language\": null, \"publishedAfter\": null}"

## 3. Buscar libros por título y autor
curl -X POST http://localhost:8080/api/books/search \
-H "Content-Type: application/json" \
-d "{\"title\": \"Harry Potter\", \"author\": \"Rowling\", \"language\": \"eng\", \"publishedAfter\": 1990}"

## 4. Buscar con idioma y año
curl -X POST http://localhost:8080/api/books/search \
-H "Content-Type: application/json" \
-d "{\"title\": \"Harry Potter\", \"author\": null, \"language\": \"spa\", \"publishedAfter\": 2000}"

## 5. Guardar libro favorito
curl -X POST http://localhost:8080/api/books/favorites \
-H "Content-Type: application/json" \
-d "{\"bookKey\": \"/works/OL82563W\", \"title\": \"Harry Potter\", \"author\": \"J.K. Rowling\", \"coverUrl\": \"https://covers.openlibrary.org/b/id/10521270-M.jpg\"}"

## 6. Ver historial de búsquedas
curl -X GET http://localhost:8080/api/books/history

## 7. Ver libros favoritos
curl -X GET http://localhost:8080/api/books/favorites