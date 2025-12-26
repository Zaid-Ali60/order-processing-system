# Order Processing System

A mini order processing system built with Java, Spring Boot, and Apache Camel that provides REST APIs for order management with JWT authentication and asynchronous message processing.

## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Prerequisites](#prerequisites)
- [Setup and Installation](#setup-and-installation)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Authentication](#authentication)
- [Apache Camel Routes](#apache-camel-routes)
- [Project Structure](#project-structure)

## Features

### Part A - REST API (Mandatory)
- ✅ Create Order API with validation
- ✅ Get Order by ID
- ✅ List Orders by Customer
- ✅ In-memory storage
- ✅ Proper exception handling
- ✅ SLF4J/Logback logging

### BONUS 1 - Apache Camel Integration
- ✅ File output after order creation
- ✅ Apache Camel route from File to ActiveMQ
- ✅ Order validation in Camel route
- ✅ ActiveMQ consumer with logging
- ✅ Error handling with file movement

### BONUS 2 - Authentication & Authorization
- ✅ JWT-based authentication
- ✅ Role-based authorization (ADMIN, USER)
- ✅ Secure REST endpoints
- ✅ Authorization rules implemented

## Technologies Used

- **Java**: 11
- **Spring Boot**: 2.7.14
- **Apache Camel**: 3.18.4
- **ActiveMQ**: Embedded broker
- **JWT**: io.jsonwebtoken 0.11.5
- **Maven**: Build tool
- **Lombok**: Code generation
- **SLF4J/Logback**: Logging

## Prerequisites

- Java 11 or higher
- Maven 3.6+
- Internet connection (for downloading dependencies)

## Setup and Installation

1. **Extract the project**
   ```bash
   unzip order-processing-system.zip
   cd order-processing-system
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Verify build**
   ```bash
   mvn verify
   ```

## Running the Application

### Start the application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Verify startup

Check the console logs for:
- "Started OrderProcessingApplication"
- "Apache Camel routes started"
- "ActiveMQ broker started"

## API Documentation

### Base URL
```
http://localhost:8080/api
```

### 1. Authentication

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "roles": ["ROLE_ADMIN"]
}
```

**Available Users:**
- Admin: `username: admin, password: admin123`
- User 1: `username: CUST1001, password: user123`
- User 2: `username: CUST1002, password: user123`

### 2. Create Order

```http
POST /api/orders
Authorization: Bearer <token>
Content-Type: application/json

{
  "customerId": "CUST1001",
  "product": "Laptop",
  "amount": 75000
}
```

**Response (201 CREATED):**
```json
{
  "orderId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "CREATED"
}
```

**Validation Rules:**
- `customerId`: Must not be null or empty
- `product`: Must not be null or empty
- `amount`: Must be greater than 0

### 3. Get Order by ID

```http
GET /api/orders/{orderId}
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "orderId": "550e8400-e29b-41d4-a716-446655440000",
  "customerId": "CUST1001",
  "product": "Laptop",
  "amount": 75000,
  "createdAt": "2024-01-15T10:30:00",
  "status": "CREATED"
}
```

**Authorization:**
- ADMIN: Can view all orders
- USER: Can only view their own orders

### 4. List Orders by Customer

```http
GET /api/orders?customerId=CUST1001
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "orderId": "550e8400-e29b-41d4-a716-446655440000",
    "customerId": "CUST1001",
    "product": "Laptop",
    "amount": 75000,
    "createdAt": "2024-01-15T10:30:00",
    "status": "CREATED"
  }
]
```

**Authorization:**
- ADMIN: Can view all customer orders
- USER: Can only view their own orders

## Authentication

### JWT Token

All order APIs require JWT authentication. Include the token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

### Roles and Permissions

**ADMIN Role:**
- Can create orders
- Can view all orders
- Can list orders for any customer

**USER Role:**
- Can create orders
- Can view only their own orders
- Can list only their own orders

## Apache Camel Routes

### Route 1: File to ActiveMQ

**Purpose:** Monitors the `input/orders/` directory and processes order files.

**Flow:**
1. Polls files from `input/orders/` directory
2. Reads JSON payload
3. Converts JSON to Order object
4. Validates:
   - orderId not null
   - customerId not null
   - amount > 0
5. On success: Sends to ActiveMQ queue `ORDER.CREATED.QUEUE`
6. On failure: Moves file to `error/orders/`

**Logging:**
```
File validated successfully | FileName=order-xxx.json | OrderId=xxx
Message sent to ActiveMQ | OrderId=xxx
```

### Route 2: ActiveMQ Consumer

**Purpose:** Consumes messages from ActiveMQ and logs order details.

**Flow:**
1. Consumes from `ORDER.CREATED.QUEUE`
2. Deserializes to Order object
3. Logs order information
4. Acknowledges message

**Sample Log:**
```
Order processed | OrderId=xxx | CustomerId=CUST1001 | Amount=75000
```

### Testing Camel Routes

1. Create an order via REST API
2. Check `input/orders/` directory for generated JSON file
3. Camel will automatically process the file
4. Check logs for processing confirmation
5. Message will be sent to ActiveMQ
6. Consumer will process and log the order

## Project Structure

```
order-processing-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/cashinvoice/orderprocessing/
│   │   │       ├── camel/
│   │   │       │   ├── ActiveMQConsumerRoute.java
│   │   │       │   └── FileToActiveMQRoute.java
│   │   │       ├── config/
│   │   │       │   ├── ActiveMQConfig.java
│   │   │       │   ├── AppConfig.java
│   │   │       │   └── SecurityConfig.java
│   │   │       ├── controller/
│   │   │       │   ├── AuthController.java
│   │   │       │   └── OrderController.java
│   │   │       ├── dto/
│   │   │       │   ├── AuthRequest.java
│   │   │       │   ├── AuthResponse.java
│   │   │       │   ├── CreateOrderRequest.java
│   │   │       │   └── CreateOrderResponse.java
│   │   │       ├── exception/
│   │   │       │   ├── ErrorResponse.java
│   │   │       │   ├── GlobalExceptionHandler.java
│   │   │       │   └── OrderNotFoundException.java
│   │   │       ├── model/
│   │   │       │   └── Order.java
│   │   │       ├── repository/
│   │   │       │   └── OrderRepository.java
│   │   │       ├── security/
│   │   │       │   ├── CustomUserDetailsService.java
│   │   │       │   ├── JwtAuthenticationFilter.java
│   │   │       │   └── JwtUtil.java
│   │   │       ├── service/
│   │   │       │   └── OrderService.java
│   │   │       └── OrderProcessingApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── logback.xml
├── input/orders/          (Created at runtime)
├── error/orders/          (Created at runtime)
├── logs/                  (Created at runtime)
├── pom.xml
└── README.md
```

## Example Usage Scenarios

### Scenario 1: Admin Creating and Viewing Orders

```bash
# 1. Login as admin
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 2. Create order
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"customerId":"CUST1001","product":"Laptop","amount":75000}'

# 3. View order
curl -X GET http://localhost:8080/api/orders/{orderId} \
  -H "Authorization: Bearer <token>"

# 4. List customer orders
curl -X GET "http://localhost:8080/api/orders?customerId=CUST1001" \
  -H "Authorization: Bearer <token>"
```

### Scenario 2: User Creating and Viewing Own Orders

```bash
# 1. Login as user
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"CUST1001","password":"user123"}'

# 2. Create order
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"customerId":"CUST1001","product":"Mouse","amount":1500}'

# 3. View own orders
curl -X GET "http://localhost:8080/api/orders?customerId=CUST1001" \
  -H "Authorization: Bearer <token>"
```

## Troubleshooting

### Port Already in Use
If port 8080 is already in use, change it in `application.properties`:
```properties
server.port=8081
```

### ActiveMQ Connection Issues
Ensure no other ActiveMQ instance is running on port 61616.

### File Processing Not Working
Check that `input/orders/` and `error/orders/` directories have proper write permissions.

## Code Quality Features

✅ Clean package structure with separation of concerns  
✅ No business logic in controllers  
✅ Proper exception handling with global exception handler  
✅ Comprehensive logging throughout  
✅ Validation at multiple layers  
✅ Security implemented with JWT and role-based access  
✅ Thread-safe in-memory storage with ConcurrentHashMap  
✅ Readable and maintainable code with Lombok  

## Support

For issues or questions, please review the logs in the `logs/` directory or check the console output.

---

**Developed as part of Cashinvoice Java Coding Assignment**
