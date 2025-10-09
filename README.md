# 💰 Gastos App - API REST

> **Sistema de gestión de finanzas personales con autenticación Google y análisis de gastos**

Una API REST robusta desarrollada en Spring Boot que permite a los usuarios gestionar sus finanzas personales de manera segura y eficiente. La aplicación ofrece funcionalidades completas para el registro, visualización, edición y análisis de gastos mensuales.

## 🎯 Propósito

La API de Gastos App está diseñada para ofrecer un conjunto de endpoints RESTful que permiten la gestión segura y persistente de las finanzas personales. Todos los datos se sincronizan a través de una base de datos central (Supabase), garantizando la persistencia y disponibilidad de la información entre diferentes dispositivos.

## ✨ Características Principales

### 🔐 Autenticación Segura
- **Autenticación con Google OAuth2**: Acceso rápido y seguro sin necesidad de crear cuentas adicionales
- **JWT Tokens**: Sistema de autenticación basado en tokens JWT con algoritmo HS256
- **Revocación de Tokens**: Gestión segura de sesiones con capacidad de revocación

### 💳 Gestión de Gastos
- **Registro de Gastos Diarios**: Captura detallada de gastos con descripción, monto, categoría y método de pago
- **Visualización Mensual**: Organización de gastos por mes con agrupación por categorías
- **Edición y Eliminación**: Mantenimiento del historial financiero con operaciones CRUD completas
- **Gastos Repetidos**: Soporte para múltiples gastos en el mismo día con fechas independientes

### 📊 Análisis e Insights
- **Resúmenes por Categoría**: Cálculo automático de totales por categoría y mes
- **Dashboard Inteligente**: Visualización de patrones de gasto y tendencias
- **Alertas de Ahorro**: Sugerencias basadas en análisis de gastos recurrentes

## 🚀 Historias de Usuario

### 📝 Gestión de Gastos Mensuales
**COMO** usuario registrado **QUIERO** registrar mis gastos diarios **PARA** tener un control mensual de mis finanzas.

**Criterios de Aceptación:**
- ✅ DADO que el usuario está autenticado, CUANDO agrega un gasto con descripción, monto, categoría y método de pago, ENTONCES el sistema guarda el gasto en la base de datos
- ✅ DADO que el usuario ingresa gastos repetidos en el mismo día, CUANDO los guarda, ENTONCES el sistema los muestra de forma independiente con su fecha correspondiente

### 📈 Visualización Mensual de Gastos
**COMO** usuario **QUIERO** visualizar mis gastos organizados por mes **PARA** analizar en qué categorías gasto más.

**Criterios de Aceptación:**
- ✅ DADO que existen registros de gastos, CUANDO el usuario consulta el mes actual, ENTONCES el sistema muestra los gastos agrupados por categoría y fecha
- ✅ DADO que el usuario selecciona otro mes, CUANDO cambia el filtro, ENTONCES el sistema actualiza la vista con los gastos correspondientes al mes seleccionado

### ✏️ Edición y Eliminación de Gastos
**COMO** usuario **QUIERO** editar o eliminar un gasto registrado **PARA** mantener mi historial actualizado y correcto.

**Criterios de Aceptación:**
- ✅ DADO que el usuario visualiza un gasto existente, CUANDO presiona editar y modifica los datos, ENTONCES el sistema actualiza el registro en la base de datos
- ✅ DADO que el usuario visualiza un gasto existente, CUANDO presiona eliminar, ENTONCES el sistema elimina el gasto y actualiza la lista en pantalla

### 🔑 Autenticación con Google
**COMO** usuario **QUIERO** iniciar sesión con mi cuenta de Google **PARA** acceder fácilmente sin crear otra cuenta.

**Criterios de Aceptación:**
- ✅ DADO que el usuario no ha iniciado sesión, CUANDO presiona 'Continuar con Google', ENTONCES el sistema redirige al flujo de autenticación de Google
- ✅ DADO que la autenticación fue exitosa, CUANDO Google devuelve el token, ENTONCES el sistema crea o asocia el usuario en la base de datos y muestra el dashboard de gastos

### 📊 Análisis e Insights de Gastos
**COMO** usuario **QUIERO** ver resúmenes e insights de mis gastos **PARA** entender mis patrones financieros.

**Criterios de Aceptación:**
- ✅ DADO que el usuario tiene gastos registrados, CUANDO entra al dashboard, ENTONCES el sistema calcula totales por categoría y mes
- ✅ DADO que existen gastos recurrentes, CUANDO el sistema identifica patrones, ENTONCES muestra sugerencias o alertas de ahorro

## 🛠️ Tecnologías Utilizadas

- **Backend**: Spring Boot 2.x
- **Base de Datos**: Supabase (PostgreSQL)
- **Autenticación**: Google OAuth2 + JWT
- **Build Tool**: Maven
- **Java**: JDK 11+

## 📋 Estructura del Proyecto

```
backAppGastos/
├── src/main/java/com/example/gastosapp/
│   ├── controller/          # Controladores REST
│   ├── service/            # Lógica de negocio
│   ├── repository/         # Acceso a datos
│   ├── model/              # Entidades y DTOs
│   └── config/             # Configuraciones
├── src/main/resources/
│   ├── application.properties
│   └── db/migration/
│       └── init.sql        # Scripts de inicialización
└── pom.xml                 # Configuración Maven
```

## 🚀 Instalación y Configuración

### Prerrequisitos
- Java 11 o superior
- Maven 3.6+
- Base de datos Supabase configurada
- Google OAuth2 Client ID

### Configuración Local

1. **Configurar variables de entorno**:
   ```bash
   # Editar src/main/resources/application.properties
   # Configurar tu base de datos y GOOGLE_CLIENT_ID
   # Cambiar el secreto JWT por uno seguro
   ```

2. **Construir la aplicación**:
   ```bash
   mvn clean package
   ```

3. **Ejecutar la aplicación**:
   ```bash
   java -jar target/gastos-app-0.0.1-SNAPSHOT.jar
   ```

## 🔌 Endpoints Principales

### Autenticación
- `POST /api/auth/google` - Autenticación con Google
  ```json
  {
    "tokenId": "<id_token>"
  }
  ```
- `POST /api/auth/logout` - Cerrar sesión
  ```
  Authorization: Bearer <jwt>
  ```

### Gestión de Gastos
- `GET /api/expenses/{year}/{month}` - Obtener gastos del mes
  ```
  Authorization: Bearer <jwt>
  ```
- `POST /api/expenses/{year}/{month}` - Crear nuevo gasto
  ```
  Authorization: Bearer <jwt>
  ```

## 🔒 Seguridad

- **Verificación de Tokens**: Utiliza el endpoint oficial de Google `https://oauth2.googleapis.com/tokeninfo?id_token=...`
- **JWT Seguro**: Algoritmo HS256 con secreto personalizable
- **Revocación de Tokens**: Gestión de tokens revocados en tabla `revoked_tokens`
- **Autenticación Bearer**: Todos los endpoints protegidos requieren token JWT válido

## 👥 Equipo de Desarrollo

- **Autor Intelectual**: Andrés Jaramillo
- **Desarrolladores**: 
  - Andrés Jaramillo
  - Daniel Correa
  - Jose Luis Ayala

## 📝 Notas Técnicas

- La aplicación utiliza Spring Boot como framework principal
- Integración completa con Google OAuth2
- Base de datos centralizada en Supabase para sincronización multi-dispositivo
- API RESTful siguiendo las mejores prácticas de desarrollo
- Sistema de migraciones de base de datos incluido

---

**Gastos App** - Transformando la gestión de finanzas personales con tecnología moderna y seguridad de primer nivel.