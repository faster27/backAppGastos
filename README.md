# Gastos App - Spring Boot (esqueleto)

Contenido del zip:
- pom.xml
- src/main/java/com/example/gastosapp/...
- src/main/resources/application.properties
- src/main/resources/db/migration/init.sql

## Cómo ejecutar (local)
1. Ajusta `src/main/resources/application.properties` con tu DB y GOOGLE_CLIENT_ID y cambia el secreto JWT.
2. Construir:
   mvn clean package
3. Ejecutar:
   java -jar target/gastos-app-0.0.1-SNAPSHOT.jar
4. Endpoints principales:
   - POST /api/auth/google  { "tokenId": "<id_token>" }
   - POST /api/auth/logout  (Authorization: Bearer <jwt>)
   - GET  /api/expenses/{year}/{month} (Authorization: Bearer <jwt>)
   - POST /api/expenses/{year}/{month} (Authorization: Bearer <jwt>)

## Notas
- Verificación de idToken usa el endpoint `https://oauth2.googleapis.com/tokeninfo?id_token=...`
- JWT usa HS256 con el secreto de `app.jwt.secret`.
- Revocación de tokens guardada en tabla `revoked_tokens`.
