# RabbitMQ Order Processing Service

This project is a RabbitMQ-based service for processing and validating order
messages. It is built using **Spring Framework**, **Spring AMQP for RabbitMQ** integration,
and **MongoDB** as the database. The service is designed to handle message validation errors by redirecting invalid messages to a Dead Letter Queue (DLQ).

## Features

- **Order Queue and DLQ**:
    - `orderQueue` for receiving order messages.
    - `deadLetterQueue`for handling rejected messages.

- **Error Handling**:
    - Handles validation errors for messages sent via RabbitMQ broker.
    - Handles validation errors for messages sent through the API endpoint.

- **Message Validation**:
    - Validates incoming messages using `@Valid` and `javax.validation` constraints.

## Getting Started
### Requirements

- **Java**: 17 or higher
- **Maven**: 3.8.7 or higher
- **RabbitMQ**
- **MongoDB**
- **Docker (Optional)** for running RabbitMQ and MongoDB services

### Clone the repository

```bash
git clone https://github.com/agmgomes/orderms.git
cd orderms
```

### Environment Setup

Start RabbitMQ and MongoDB (if not already running). You can use Docker Compose
to quickly set up both services:

```bash
cd docker
docker compose up -d
```
Change the properties at `src/resources/application.properties` if using your own
RabbitMQ and MongoDB setup:

```bash
spring.application.name=orderms

spring.data.mongodb.authentication-database=admin
spring.data.mongodb.auto-index-creation=true
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=ordermsdatabase
spring.data.mongodb.username=admin
spring.data.mongodb.password=password

spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=admin
spring.rabbitmq.password=password
```

### Start the app

To build and run the application:
```bash
mvn clean install
mvn spring-boot:run
```

## Usage

### Using RabbitMQ Management

RabbitMQ provides a management UI to monitor and manage queues, exchanges and 
messages. To access the RabbitMQ Management open your browser and navigate to:
```bash
http://localhost:15672
```
And log in with the default credentials:
- **Username**: admin
- **Password**: password

Publish the message to the main queue (`order-created-queue`) with the following
payload:

```json
{
    "customerId": 3,
    "items":[
        {
            "product": "paper",
            "quantity": 3,
            "price": 2.44
        },
        {
            "product": "chocolate",
            "quantity": 1,
            "price": 1.99
        }
    ]
}
```

### API Endpoints

| Method | Endpoint               | Description                    |
|:-------|:-----------------------|:-------------------------------|
| POST   | /orders/send           | Send order                     |
| GET    | /orders/customer/${id} | Retrieve all orders by customer|


**Example: Sending an Order via API**

To submit an order using the API, send a `POST`request to `orders/send` with the
following JSON body:
```json
{
    "customerId": 1,
    "items":[
        {
            "product": "pencil",
            "quantity": 3,
            "price": 1.25
        }
    ]
}
```

## License

This project is licensed under the MIT License.