# Personal Finance Manager

A production-ready Personal Finance Manager REST API built with Java 21, Spring Boot 3, Spring Security, JWT authentication, and Spring Data JPA (MySQL).

## Tech Stack
- Java 21
- Spring Boot 3.3.4
- Spring Security + JWT (jjwt)
- Spring Data JPA / Hibernate
- MySQL
- Maven
- Lombok
- Bean Validation
- Apache PDFBox (PDF export)
- Apache POI (Excel export)

## Getting Started in IntelliJ IDEA

1. Extract the ZIP file.
2. Open IntelliJ IDEA → `File > Open` → select the extracted `personal-finance-manager` folder.
3. Let Maven resolve dependencies automatically (or right-click `pom.xml` → `Maven > Reload project`).
4. Create a MySQL database (or let the app auto-create it) and update credentials in
   `src/main/resources/application.properties`:
   ```
   spring.datasource.url=jdbc:mysql://localhost:3306/finance_manager_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=root
   ```
5. Run `PersonalFinanceManagerApplication.java`. Tables are created automatically via `spring.jpa.hibernate.ddl-auto=update`.
6. The API will be available at `http://localhost:8080`.

## Authentication

Register: `POST /api/auth/register`
Login: `POST /api/auth/login`

Both return a JWT `accessToken`. Send it as `Authorization: Bearer <token>` on all other endpoints.

## Key Endpoints

| Feature | Endpoint |
|---|---|
| Register / Login | `/api/auth/register`, `/api/auth/login` |
| Profile | `/api/users/me` (GET, PUT), `/api/users/me/password` (PUT) |
| Categories | `/api/categories` (GET, POST), `/api/categories/{id}` (PUT, DELETE) |
| Incomes | `/api/incomes` (GET with pagination/search/filter, POST), `/api/incomes/{id}` (GET, PUT, DELETE) |
| Expenses | `/api/expenses` (GET with pagination/search/filter, POST), `/api/expenses/{id}` (GET, PUT, DELETE) |
| Receipt Upload | `POST /api/expenses/{id}/receipt` (multipart/form-data, field `file`) |
| Budgets | `/api/budgets` (GET, POST), `/api/budgets/{id}` (PUT, DELETE), `/api/budgets/alerts` (GET) |
| Dashboard | `/api/dashboard?month=&year=` |
| Reports | `/api/reports/monthly?month=&year=`, `/api/reports/yearly?year=` |
| Report Export | `/api/reports/monthly/export/pdf`, `/api/reports/monthly/export/excel` |
| Recurring Transactions | `/api/recurring-transactions` (GET, POST), `/api/recurring-transactions/{id}` (PUT, DELETE) |

## Notes
- Uploaded receipts are stored under `uploads/receipts` (configurable via `app.file.upload-dir`) and served at `/uploads/**`.
- Budget limit alerts are calculated automatically whenever an expense is created/updated against a category that has a budget for that month.
- Recurring transactions are processed automatically once a day via a scheduled job (`RecurringTransactionScheduler`), generating actual income/expense records.
- Global exception handling returns consistent JSON error responses for validation errors, not-found resources, duplicate resources, authentication failures, and unexpected errors.
