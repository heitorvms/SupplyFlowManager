# SupplyFlow Manager

SupplyFlow Manager is an industrial production management system designed to manage raw materials, product compositions and production processes.

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
- Vue i18n
- Vuetify
- TailwindCSS
- Axios

---

# Requirements

Make sure the following tools are installed:

- Java 21
- Maven
- Node.js 18+
- PostgreSQL
- npm

---

# Backend Setup

Navigate to the backend folder: ```SupplyFlowManagerAPI/src/main/resources/application.properties``` and insert the follow configuration on it:

``` 
spring.application.name=SupplyFlow-Manager

spring.datasource.url=jdbc:postgresql://localhost:5432/supplyflow?currentSchema=public
spring.datasource.username=username
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```


