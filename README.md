# Enviro365 — Investor Withdrawal Automation System
<img width="1863" height="991" alt="603927570-bb2b75d8-14b1-4ae6-a9d0-47aae6551e81" src="https://github.com/user-attachments/assets/03454f9c-e367-49ca-9b78-1343fb766ab2" />


A full-stack application that automates the compliance validation, balance modification, and transactional logging of client withdrawal notices for **Enviro365 Investments**.

---

## 📁 Project Structure

```
enviro365-investorsystem/
├── InvestorSystem/          # Spring Boot backend (Java 17)
└── InvestorSystemFrontend/  # Angular 22 frontend (TypeScript)
```

---

## 🛠️ Tech Stack

### Backend
| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.x (Web, Data JPA, Validation) |
| Database | H2 Embedded In-Memory |
| Reporting | Apache Commons CSV 1.10.0 |
| Base Package | `com.enviro.assessment.junior.phodzo.nagana` |

### Frontend
| Layer | Technology |
|---|---|
| Framework | Angular CLI 22.0.0 |
| Language | TypeScript (61.7%) |
| Styling | CSS / SCSS (35.6%) |
| Testing | Vitest |

---

## ⚙️ Business Compliance Rules

The backend enforces the following regulations within the `@Transactional` service layer before committing any state changes:

| Rule | Description |
|---|---|
| **Age Threshold** | Investors holding a `RETIREMENT` product must be strictly older than **65 years** (`age > 65`) to initiate a withdrawal. |
| **Sufficiency Check** | A withdrawal is rejected if the requested amount **exceeds the current product balance**. |
| **90% Balance Cap** | An investor cannot withdraw more than **90% of their active product balance** in a single transaction. |

---

## 🚀 Getting Started

### Prerequisites

Make sure the following tools are installed and available on your system PATH:

- **JDK 17+**
- **Apache Maven 3.8+** (or use the included `./mvnw` wrapper)
- **Node.js** (LTS recommended) + **npm**
- **Angular CLI 22**: `npm install -g @angular/cli`

---

## 🔧 Backend Setup

### 1. Configuration (`application.yaml`)

The backend is pre-configured with an in-memory H2 database:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:investorsystem_db
    username: Phodzo
    password: Phodzo123
```

### 2. Run the Backend

Navigate to the `InvestorSystem/` directory and run:

```bash
mvn spring-boot:run
```

The server will:
- Compile all layers
- Auto-seed mock data via `CommandLineRunner`
- Start at **[http://localhost:8080](http://localhost:8080)**

### 3. H2 Database Console

While the server is running, access the visual database console at:

**[http://localhost:8080/h2-console](http://localhost:8080/h2-console)**

Use the following connection details:

| Field | Value |
|---|---|
| JDBC URL | `jdbc:h2:mem:investorsystem_db` |
| User Name | `Phodzo` |
| Password | `Phodzo123` |

---

## 🌐 Frontend Setup

Navigate to the `InvestorSystemFrontend/` directory.

### Install Dependencies

```bash
npm install
```

### Start Development Server

```bash
ng serve
```

Open your browser at **[http://localhost:4200](http://localhost:4200)**. The app hot-reloads automatically on file changes.

### Build for Production

```bash
ng build
```

Build artifacts are output to the `dist/` directory, optimised for performance.

---

## 📡 REST API Reference

All API endpoints exchange structured JSON payloads. Base URL: `http://localhost:8080`

### `GET /api/investors/{id}/portfolio`

Fetches investor demographic data and their nested product listings.

**Path Variable:** `id` — investor identifier

**Response:** `InvestorDto` with nested product array

---

### `POST /api/withdrawals`

Submits a withdrawal request. Runs `@Valid` input validation, evaluates all compliance rules, modifies balance state, and logs a transaction record.

**Request Body:**
```json
{
  "productId": 1,
  "amount": 5000.00
}
```

---

### `GET /api/products/{productId}/export`

Streams a filtered CSV of transaction records directly to the browser.

**Query Parameters (all optional):**

| Parameter | Example | Description |
|---|---|---|
| `status` | `SUCCESS` | Filter by transaction status |
| `fromDate` | `2026-01-01` | Start of date range |
| `toDate` | `2026-12-31` | End of date range |

---

## 🧪 Running Tests

### Frontend Unit Tests

```bash
ng test
```

Uses the [Vitest](https://vitest.dev/) test runner.

### Frontend End-to-End Tests

```bash
ng e2e
```

> Angular CLI does not bundle an e2e framework by default — configure one that suits your project (e.g. Playwright, Cypress).

---

## 🧠 Engineering Standards

| Feature | Detail |
|---|---|
| **DTOs** | Structural mapping classes prevent persistence entities from leaking to public HTTP layers and break infinite serialization cycles. |
| **Input Validation** | `@NotNull`, `@Positive`, and other annotations intercept malformed request payloads at the controller gateway. |
| **Global Exception Handling** | A `@RestControllerAdvice` class centralises error handling, returning structured `400 Bad Request` / `404 Not Found` JSON responses with timestamps instead of server stack traces. |

---

## 🤖 AI Usage Disclosure

**Tools used:** ChatGPT / Claude

**Scope:** Used to scaffold baseline architectural layer patterns, resolve TypeScript type constraints across Angular service modules, and design the CSV streaming pipeline structure.

**Manual work:** All business rule conditions, `BigDecimal` precision comparisons, and relational database schema definitions were manually refactored, cross-checked, and tested locally.

---

## 🔗 Additional Resources

- [Angular CLI Documentation](https://angular.dev/tools/cli)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Apache Commons CSV](https://commons.apache.org/proper/commons-csv/)
- [Vitest](https://vitest.dev/)

---

*Developed by **Phodzo Nagana** — Enviro365 Junior Assessment*
