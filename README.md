# SupplyFlow Manager

SupplyFlow Manager is an industrial production management system designed to manage raw materials, product compositions, and production processes.

The project consists of a **Spring Boot API** for the backend and a **Vue 3 application** for the frontend.

---

# Technologies

## Backend
- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Maven
- Lombok

## Frontend
- Vue 3
- Vite
- TypeScript
- Vue Router
- Vue I18n
- Vuetify
- TailwindCSS
- Axios

---

# Requirements

Make sure the following tools are installed:

- Java 21
- Maven
- Node.js 22.14.0
- PostgreSQL
- npm

---

## Backend Setup

### 1. Create the PostgreSQL Database

Before running the backend, you must create the database used by the application.

Example using the PostgreSQL CLI (`psql`):

```sql
CREATE DATABASE supplyflow;
```

The application is configured to connect using the default PostgreSQL user:
```
username: postgres
password: 123456
```
If necessary, change it to match your PostgreSQL credentials in the following file: ``` SupplyFlowManagerAPI/src/main/resources/application.properties ```

### 2. Run the API

Navigate to the backend folder ``` SupplyFlowManagerAPI ``` in the terminal and run on Linux:
```bash
mvn spring-boot:run
```
or on Windows:
```bash
./mvnw spring-boot:run
```

## Frontend Setup

### 1. Install Dependencies

Navigate to the frontend folder:

```bash
cd SupplyFlowManagerFront
```

Install the project dependencies:
```bash
npm install
```

### 2. Run the Development Server

Start the development server:
```bash
npm run dev
```