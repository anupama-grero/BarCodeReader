# Barcode Reader / Product CRUD

## Overview

This project is a Spring Boot 3.3.2 application for managing products, suppliers, purchase orders, and goods received notes. It combines a REST API with a simple web UI and uses JPA/Hibernate with PostgreSQL for persistence.

The application is built around a product inventory workflow with barcode-based lookup and stock updates driven by purchase-order receiving operations.

## Features

### Product management
- Create, view, update, and delete products
- Barcode validation and uniqueness checks
- Product name, description, price, quantity, and supplier assignment
- Lookup by product ID and barcode
- REST endpoints under `/api/products`

### Supplier management
- Create, view, update, and delete suppliers
- Email uniqueness validation
- Supplier details including name, email, phone, address, company, and status
- REST endpoints under `/api/suppliers`

### Purchase order workflow
- Create purchase orders linked to a supplier
- Add purchase-order items with product, quantity, and unit price
- Track order totals and status
- Thymeleaf pages under `/purchase-orders`

### Goods received notes (GRN)
- Create receiving notes against a purchase order
- Validate received quantities against the remaining ordered quantity
- Update product stock after receiving items
- Update purchase-order receiving status based on the total received quantity
- Thymeleaf pages under `/grn`

### Static web interface
- Product and supplier CRUD form at `src/main/resources/static/index.html`
- Client-side logic in `src/main/resources/static/app.js`
- Styling in `src/main/resources/static/style.css`

## Technology Stack

- Java 21
- Spring Boot 3.3.2
- Spring MVC
- Spring Data JPA
- Hibernate ORM
- PostgreSQL
- Supabase database hosting
- Thymeleaf
- Maven
- H2 in-memory database for tests

## Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── bci/
│   │           └── productcrud/
│   │               ├── controller/
│   │               ├── exception/
│   │               ├── model/
│   │               ├── repository/
│   │               ├── service/
│   │               └── ProductCrudApplication.java
│   └── resources/
│       ├── application.properties
│       ├── static/
│       │   ├── app.js
│       │   ├── index.html
│       │   └── style.css
│       └── templates/
│           ├── grn/
│           └── purchase-orders/
└── test/
    └── java/
        └── com/
            └── bci/
                └── productcrud/
                    ├── controller/
                    └── service/
```

## Application Architecture

The application follows a standard Spring MVC + JPA layering pattern:

```text
Browser / Client
      ↓
Controller Layer
      ↓
Service Layer
      ↓
Repository Layer
      ↓
Hibernate / PostgreSQL
```

### Layer responsibilities
- Controllers handle HTTP requests and return JSON or rendered pages
- Services implement business validation and workflow logic
- Repositories provide persistence access using Spring Data JPA
- Entities map the database tables used by the application

## Database and Configuration

The project is configured in `src/main/resources/application.properties`.

Current configuration includes:
- PostgreSQL driver
- Supabase connection URL
- JPA schema validation mode (`spring.jpa.hibernate.ddl-auto=validate`)
- Port `8081`
- Thymeleaf cache disabled for development

```properties
spring.datasource.url=jdbc:postgresql://.../postgres?sslmode=require
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.open-in-view=false
server.port=8081
```

This project intentionally keeps the database schema validated rather than recreating it automatically, so any mismatch between the entity model and the live database is surfaced clearly during startup.

## Main Entities

- `Product` — inventory item with barcode, name, description, price, quantity, and supplier
- `Supplier` — vendor details and contact information
- `PurchaseOrder` — supplier order record with items and totals
- `PurchaseOrderItem` — line item for a purchase order
- `GoodsReceivedNote` — receiving record linked to a purchase order
- `GoodsReceivedNoteItem` — received product lines for a GRN

## REST API

### Products
- `GET /api/products` — list all products
- `GET /api/products/{id}` — get product by ID
- `GET /api/products/barcode/{barcode}` — get product by barcode
- `POST /api/products` — create a product
- `PUT /api/products/{id}` — update a product
- `DELETE /api/products/{id}` — delete a product

### Suppliers
- `GET /api/suppliers` — list all suppliers
- `GET /api/suppliers/{id}` — get supplier by ID
- `GET /api/suppliers/email/{email}` — get supplier by email
- `POST /api/suppliers` — create a supplier
- `PUT /api/suppliers/{id}` — update a supplier
- `DELETE /api/suppliers/{id}` — delete a supplier

## Web Pages

The application includes Thymeleaf views for operational workflows:

- `/purchase-orders` — purchase order list and form pages
- `/grn` — goods received note list and form pages

These pages are rendered from templates in:
- `src/main/resources/templates/purchase-orders`
- `src/main/resources/templates/grn`

## Business Workflow

A typical flow in this project is:

1. Create a supplier
2. Add one or more products for that supplier
3. Create a purchase order
4. Add ordered items with quantity and unit price
5. Create a goods received note for the purchase order
6. Validate the received quantity against the remaining order quantity
7. Update product stock
8. Update the purchase order status to `PARTIALLY_RECEIVED` or `RECEIVED`

## Validation and Error Handling

The project uses Jakarta Bean Validation and service-layer checks to enforce data quality. Key checks include:

- required product and supplier fields
- duplicate barcode detection
- duplicate email detection
- invalid quantity checks
- over-receipt prevention for a purchase order
- centralized exception handling for not found and invalid-input cases

Exception types include:
- `DuplicateBarcodeException`
- `DuplicateSupplierException`
- `ProductNotFoundException`
- `SupplierNotFoundException`
- `ResourceNotFoundException`
- `InvalidPurchaseOrderException`
- `InvalidGoodsReceivedNoteException`
- `InsufficientQuantityException`

## Testing

The project includes integration tests under `src/test/java` for API and workflow validation.

Examples:
- `SupplierControllerIntegrationTest`
- `PoGrnWorkflowIntegrationTest`

Run tests with:

```bash
mvn test
```

The application is also configured to use H2 test data sources for isolated test execution.

## Running the Project

From the project root:

```bash
mvn spring-boot:run
```

Then open:

```text
http://localhost:8081
```

## Notes

- The project is designed for PostgreSQL/Supabase use in the main runtime environment.
- The database schema is validated on startup, so entity mappings should match the live database structure.
- The project preserves the existing Product CRUD and supplier management functionality while supporting the order-receiving workflow used in the application.

## Academic / Project Context

This project is a Java Spring Boot CRUD and warehouse workflow application created for enterprise application development coursework. It demonstrates core JPA mapping, validation, MVC structure, PostgreSQL integration, and business-process implementation.


This addresses the reported lazy-init Jackson failure while preserving the working Product, Supplier, PO, and GRN behavior.

## 15. Future Improvements

These are not currently implemented, but are reasonable future enhancements:
- Authentication and role-based authorization
- Dashboard analytics and reporting
- Advanced inventory forecasting
- Barcode scanner integration across more workflows
- Email or notification alerts for low stock and pending orders

## 16. License

Academic project — no license specified.
