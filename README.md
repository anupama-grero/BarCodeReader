# Supermarket Management System

## 1. Project Overview

This project is a Spring Boot-based Supermarket Management System built with Java, Spring MVC, Spring Data JPA, Hibernate, PostgreSQL, Supabase, Maven, and Thymeleaf. It provides a lightweight inventory and purchasing workflow for managing products, suppliers, purchase orders (POs), and goods received notes (GRNs).

The application is structured around the standard MVC flow and is designed to run locally at http://localhost:8081. It combines REST-style data APIs for product and supplier management with Thymeleaf-based web pages for purchase order and goods reception workflows.

## 2. Features

### Product Management
- Add Product
- View Products
- Edit Product
- Delete Product
- Product validation
- Barcode-based lookup
- Product price management
- Product quantity / stock tracking
- Product-supplier association

### Supplier Management
- Add Supplier
- View Suppliers
- Edit Supplier
- Delete Supplier
- Supplier validation
- Duplicate email protection

### Purchase Order Management
- Create PO
- View PO list
- View PO details
- Edit PO
- Delete PO
- Supplier selection
- Product item selection
- Quantity input
- Unit price input
- Line total and total amount calculation
- PO status tracking

### Goods Received Note Management
- Create GRN from a PO
- View GRNs
- Receive quantities against ordered items
- Validate ordered vs received quantities
- Prevent over-receipt beyond remaining ordered quantity
- Calculate GRN totals
- Update product stock after GRN creation
- Update PO receiving status

## 3. Technology Stack

- Java 21
- Spring Boot 3.3.2
- Spring MVC
- Spring Data JPA
- Hibernate ORM
- PostgreSQL
- Supabase
- Maven
- Thymeleaf
- HTML / CSS / JavaScript
- H2 in-memory database for tests

## 4. Architecture

The project follows the MVC architecture pattern:

```text
Client / Browser
      ↓
Controller
      ↓
Service
      ↓
Repository
      ↓
JPA / Hibernate
      ↓
PostgreSQL / Supabase
```

### Layer responsibilities
- Controller: handles HTTP requests and returns views or JSON responses
- Service: contains business rules and validation logic
- Repository: performs database access through Spring Data JPA interfaces
- Entity: represents the database model and JPA mappings
- Thymeleaf templates and static frontend assets: present data to the user

## 5. Project Structure

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

## 6. Database

The application uses PostgreSQL and is configured to connect to a Supabase-hosted database. Database configuration is defined in [src/main/resources/application.properties](src/main/resources/application.properties).

The main entities in the project include:

- Supplier
- Product
- PurchaseOrder
- PurchaseOrderItem
- GoodsReceivedNote
- GoodsReceivedNoteItem

### Relationship overview

```text
Supplier
   ↓
Purchase Order
   ↓
Purchase Order Items
   ↓
Product
```

```text
Purchase Order
   ↓
Goods Received Note
   ↓
Goods Received Note Items
   ↓
Product
```

The stock update flow is implemented through GRN processing. When a GRN is created, the received quantities are validated against the remaining ordered quantity and the related product stock is increased accordingly.

## 7. Prerequisites

- JDK 21
- Maven
- PostgreSQL / Supabase access credentials
- Git (optional, for source control)

## 8. Configuration

The project reads database and application settings from [src/main/resources/application.properties](src/main/resources/application.properties).

Use placeholders in your local configuration and provide your own Supabase credentials:

```properties
spring.datasource.url=YOUR_SUPABASE_DATABASE_URL
spring.datasource.username=YOUR_SUPABASE_USERNAME
spring.datasource.password=YOUR_SUPABASE_PASSWORD
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
server.port=8081
```

Do not commit real database credentials to the repository. The application expects the local environment to provide valid Supabase connection details.

## 9. How to Run

From the project root, run:

```bash
mvn clean
mvn spring-boot:run
```

Then open the application in a browser at:

```text
http://localhost:8081
```

## 10. Testing

The project includes integration tests covering key business behavior.

Run tests with:

```bash
mvn test
```

Current test coverage includes:
- Supplier API validation and duplicate email prevention
- Purchase order creation and GRN stock update flow
- Validation of over-receipt beyond the remaining ordered quantity

## 11. Database / Entity Relationships

### Product
- Represents inventory items
- Has a barcode, name, description, price, quantity, and supplier reference
- Mapped as a JPA entity with a many-to-one relation to Supplier

### Supplier
- Represents vendors or suppliers
- Stores business details such as name, phone, email, address, and company name
- Contains a list of associated products

### PurchaseOrder
- Represents a purchase request issued to a supplier
- Contains supplier, order date, expected delivery date, status, and total amount
- Contains multiple PurchaseOrderItem entries

### PurchaseOrderItem
- Represents one ordered product line in a PO
- Stores product, quantity, unit price, and line total

### GoodsReceivedNote
- Represents the receiving record for part or all of a purchase order
- Links to a PurchaseOrder
- Stores received date, total amount, remarks, and status
- Contains multiple GoodsReceivedNoteItem entries

### GoodsReceivedNoteItem
- Represents one received product line within a GRN
- Stores product, ordered quantity, received quantity, unit price, and line total

## 12. Business Workflow

The primary workflow implemented by the project is:

```text
Supplier
   ↓
Purchase Order
   ↓
Goods Received Note
   ↓
Product Stock Update
```

A typical flow is:
1. Create a Supplier
2. Add products linked to that supplier
3. Create a Purchase Order against a supplier
4. Add ordered products with quantity and unit price
5. Create a Goods Received Note from that PO
6. Enter received quantities
7. Validate that the received quantity does not exceed the remaining ordered quantity
8. Update product stock
9. Update the PO status to PARTIALLY_RECEIVED or RECEIVED

## 13. Validation and Error Handling

The application includes validation and centralized exception handling:

- Product and Supplier validation is enforced using Jakarta Validation annotations
- Duplicate email and duplicate barcode checks are enforced at the service layer
- Purchase order validation checks supplier, required dates, and line items
- GRN validation checks received quantities and remaining order quantities
- Global exception handling returns structured HTTP error responses

Examples of implemented application exceptions include:
- DuplicateBarcodeException
- DuplicateSupplierException
- ProductNotFoundException
- SupplierNotFoundException
- ResourceNotFoundException
- InvalidPurchaseOrderException
- InvalidGoodsReceivedNoteException
- InsufficientQuantityException

## 14. Verification

I verified the fix with the relevant regression tests:

- Command: `mvn -q -Dtest='SupplierControllerIntegrationTest,PoGrnWorkflowIntegrationTest' test`
- Evidence:
- `com.bci.productcrud.controller.SupplierControllerIntegrationTest.txt`: Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
- `com.bci.productcrud.service.PoGrnWorkflowIntegrationTest.txt`: Tests run: 2, Failures: 0, Errors: 0, Skipped: 0

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
