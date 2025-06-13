# E-Commerce Store API

A fully-featured RESTful API for an e-commerce platform built with Spring Boot.

## Features

- **Authentication & Authorization**: Secure JWT-based authentication system with role-based access control
- **Product Management**: CRUD operations for products with category management
- **Shopping Cart**: Cart management with product quantities
- **Order Processing**: Complete order management system
- **User Management**: User registration and profile management
- **Payment Integration**: Payment processing capabilities through Stripe
- **Database Migrations**: Flyway migration system for database versioning

## Technology Stack

- **Framework**: Spring Boot
- **Security**: Spring Security with JWT
- **Database**: MySQL with Flyway migrations
- **Documentation**: OpenAPI/Swagger
- **Build Tool**: Maven

## Deployment

The API is deployed and accessible at:
- Production URL: https://store-api-production-5fd7.up.railway.app
- Swagger Documentation: https://store-api-production-5fd7.up.railway.app/swagger-ui.html

The application is hosted on Railway, a modern application platform that handles deployment, scaling, and management of the infrastructure.

## Project Structure

```
src/
 main/
    java/com/codewithmosh/store/
       admin/      # Admin functionalities
       auth/       # Authentication & security
       carts/      # Shopping cart operations
       orders/     # Order processing
       products/   # Product management
       users/      # User management
    resources/
        db/migration/ # Database migrations
```

