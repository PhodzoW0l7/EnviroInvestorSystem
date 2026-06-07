# Enviro365 Investments: Investor Withdrawal Automation System (InvestorSystem)
<img width="1433" height="970" alt="image" src="https://github.com/user-attachments/assets/8253dc7e-f3d8-4339-bbf7-61ae6ed7f5c5" />

A robust, production-ready Spring Boot REST API that automates the compliance validation, balance modification, and transactional logging of client withdrawal notices for **Enviro350 Investments**.

---

## 🛠️ Tech Stack & Constraints
* **Java Version:** 17
* **Framework:** Spring Boot 3.x (with Web, Data JPA, and Validation starter engines)
* **Database Platform:** H2 Embedded In-Memory Engine
* **Reporting Dependency:** Apache Commons CSV (`org.apache.commons:commons-csv:1.10.0`)
* **Project Base Package Prefix:** `com.enviro.assessment.junior.phodzo.nagana`

---

## ⚙️ System Business Compliance Rules
The backend engine enforces these strict business regulations within the `@Transactional` service layer before committing state modifications:
1. **Age Threshold Restriction:** Investors holding a `RETIREMENT` product must be strictly older than 65 years old (`age > 65`) to initiate any withdrawal notice.
2. **Sufficiency Factor Check:** A withdrawal request is instantly rejected if the requested amount exceeds the current product balance.
3. **90% Balance Cap Limitation:** An investor cannot withdraw more than **90%** of their active product balance inside a single transactional request.

---

## 🚀 Getting Started & Setup Guide

### 1. Prerequisites
Ensure you have the following installed on your local environment terminal path:
* **Java Development Kit (JDK):** Version 17 or higher
* **Apache Maven:** Version 3.8+ (or use the included wrapper `./mvnw`)

### 2. Configuration (`application.yaml`)
The application is pre-configured to initialize an in-memory database workspace instantly. The access keys used under `src/main/resources/application.yaml` are:
* **JDBC Target Connection URL:** `jdbc:h2:mem:investorsystem_db`
* **Secure Access ID:** `Phodzo`
* **Secure Authorization Password:** `Phodzo123`

### 3. Launching the Backend Server
Navigate to the root directory of your Java project using your terminal and run:
```bash
mvn spring-boot:run
```
The server will compile the layers, seed mock data vectors automatically via `CommandLineRunner`, and open a live server port link at `http://localhost:8080`.

### 4. Accessing the H2 Visual Database Console
You can query the underlying relational tables visually while the system is actively running:
1. Navigate to: `http://localhost:8080/h2-console`
2. Change the default JDBC connection fields to match your custom keys:
   * **JDBC URL:** `jdbc:h2:mem:investorsystem_db`
   * **User Name:** `Phodzo`
   * **Password:** `Phodzo123`
3. Click **Connect** to execute direct SQL queries against the active tables.

---

## 📡 REST API Endpoint Documentation Matrix

All outgoing API traffic payloads exchange clear data structures with the frontend client.


| HTTP Method | API URL Endpoint Path | Content Payload Required / Query Filters | Description |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/investors/{id}/portfolio` | None (*Pass investor identifier variable in path*) | Fetches custom investor demographic fields and maps nested product array listings safely to a clean `InvestorDto`. |
| **POST** | `/api/withdrawals` | `WithdrawalRequestDto` JSON payload (e.g., `{"productId": 1, "amount": 5000.00}`) | Runs input rules through `@Valid`, evaluates compliance math filters, modifies balance states, and logs transaction records. |
| **GET** | `/api/products/{productId}/export` | Optional HTTP URL Query parameters: `?status=SUCCESS&fromDate=2026-01-01&toDate=2026-12-31` | Filters transactional records and streams a dynamic comma-separated spreadsheet straight into the user's browser buffer RAM. |

---

## 🧠 Advanced Engineering Standards Implemented
This project includes several advanced structural features to improve safety and maintainability:
* **Data Transfer Objects (DTOs):** Structural mapping classes ensure your persistence entities never leak directly out to public HTTP layers, breaking infinite relationship serialization paths.
* **Input Schema Interception Validation:** Standard validation annotations (like `@NotNull` and `@Positive`) verify incoming network traffic formats right at the controller gateway.
* **Global Controller Advice Exception Interceptor:** A centralized `@RestControllerAdvice` class handles custom validation failures (e.g., `BusinessLogicValidationException`), returning explicit `400 Bad Request` or `404 Not Found` JSON metadata responses containing structured timestamps instead of throwing standard server crashes.

---

## 🤖 AI Usage Disclosure Statement
* **AI Tool Integration utilized:** ChatGPT / Claude LLM models.
* **Usage Context Scope:** This tool was explicitly used to construct baseline architectural layer patterns, resolve strict TypeScript type-casting constraints across Angular service modules, and design the formatting for the core spreadsheet streaming pipeline. 
* **Manual Verification:** All business rule conditions, precision `BigDecimal` comparison operations, and relational database table definitions were manually refactored, cross-checked, and thoroughly tested locally to guarantee full compliance with the assessment criteria.
