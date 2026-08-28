# 📚 Library API

A Spring Boot REST API for managing borrowers, physical books, and book
borrowing transactions.

This project is implemented as a backend take-home assignment with a focus
on clean architecture, transactional consistency, concurrency safety,
database integrity, validation, testing, and maintainability.

---

## 👤 Author

**Muhammad Jafar Shodiq**

🎯 Senior Java Developer

- 📧 Email: jafarshodiq0412@gmail.com
- 🔗 LinkedIn: https://www.linkedin.com/in/jafar-shodiq-498354194/

---

# 🚀 Tech Stack

| Technology              | Purpose                             |
|-------------------------|-------------------------------------|
| ☕ Java 17               | Programming language                |
| 🍃 Spring Boot 3.5.x    | Application framework               |
| 🌐 Spring Web           | REST API                            |
| 🗃️ Spring Data JPA     | Data access                         |
| 🧩 Hibernate            | ORM                                 |
| 🐬 MySQL 8+             | Relational database                 |
| 🔄 Flyway               | Database migration                  |
| ✅ Jakarta Validation    | Request validation                  |
| 📖 Springdoc OpenAPI    | Swagger / API documentation         |
| 💚 Spring Boot Actuator | Health and metrics                  |
| 🧪 JUnit 5              | Testing                             |
| 🎭 Mockito              | Mocking                             |
| 🐳 Testcontainers       | Integration testing with real MySQL |
| 📊 JaCoCo               | Code coverage                       |
| 🔍 SonarQube            | Static code analysis                |
| 📦 Maven                | Build and dependency management     |

> **FYI**  
> This codebase complies with **SonarQube standard quality rules** and contains **no critical, major, or blocker issues
**.

# Twelve-Factor App Principles

This application follows the Twelve-Factor App principles where applicable:

- **Codebase** — maintained as a single Git repository.
- **Dependencies** — managed explicitly using Maven.
- **Config** — environment-specific configuration is externalized through environment variables.
- **Backing Services** — database is treated as an external backing service.
- **Processes** — the application runs as a stateless Spring Boot process.
- **Port Binding** — exposes HTTP REST APIs through the configured application port.
- **Concurrency** — designed to support horizontal scaling and handles concurrent book borrowing using database-level pessimistic locking.
- **Logs** — application logs are written through the application logging framework.
- **Dev/Prod Parity** — environment-specific settings are externalized to minimize configuration differences.

---

# 🏗️ Architecture

The application follows a layered architecture with responsibilities
separated by domain.

```text
                         🌐 REST API
                             │
             ┌───────────────┼───────────────┐
             │               │               │
             ▼               ▼               ▼
     BorrowerController  BookController  LoanController
             │               │               │
             ▼               ▼               ▼
     BorrowerService     BookService     LoanService
             │               │               │
             │         ┌─────┴─────┐         │
             │         │           │         │
             ▼         ▼           ▼         ▼
     BorrowerRepo  ISBN Repo    BookRepo   LoanRepo
             │         │           │         │
             └─────────┴───────────┴─────────┘
                             │
                             ▼
                         🐬 MySQL

```

# 🎯 Domain Responsibility

Each controller and service is responsible for a specific domain.

```text
👤 BorrowerController
        │
        ▼
   BorrowerService
        │
        ▼
   BorrowerRepository
```

```text
📚 BookController
        │
        ▼
     BookService
        │
        ├── IsbnCatalogRepository
        │
        └── BookRepository
```

```text
🔄 LoanController
        │
        ▼
     LoanService
        │
        ├── BookRepository
        │      └── 🔒 Pessimistic Lock
        │
        ├── BorrowerRepository
        │
        └── LoanRepository
```

This separation keeps each business responsibility isolated and avoids a
large service containing unrelated operations.

# 🔒 Concurrency Control

One of the most important business requirements is:

```text 
A physical book must not be borrowed by two borrowers simultaneously. 
```

Without locking:

```text 
Request A                 Request B
    │                         │
    ▼                         ▼
Check available          Check available
    │                         │
    ▼                         ▼
    YES                       YES
    │                         │
    ▼                         ▼
Create loan               Create loan
```

Both requests could potentially succeed.

The application prevents this using:

🔄 Database transaction
🔒 Pessimistic row locking
🗃️ Database constraints

# 🔒 Pessimistic Locking

The borrow operation locks the physical book before checking its active
loan.

```text 
BEGIN TRANSACTION
        │
        ▼
🔒 SELECT BOOK FOR UPDATE
        │
        ▼
Check Active Loan
        │
        ▼
Create Loan
        │
        ▼
COMMIT
```

# 🗂️ Database Schema

The main tables are:

- isbn_catalog
- borrower
- book
- loan

Relationships:

```text
isbn_catalog
     │
     │ 1:N
     ▼
    book
     │
     │ 1:N
     ▼
    loan
     │
     │ N:1
     ▼
  borrower
```

The database uses:

- 🔐 Foreign keys
- Unique constraints
- 🔄 Transactions
- 🔒 Row-level locking

# 🤔 Why Separate isbn_catalog and book?

An ISBN identifies book metadata, while a Book represents a physical
copy.

For example:

```text
isbn_catalog
    ISBN   = 9780132350884
    Title  = Clean Code
    Author = Robert C. Martin

            │
            ├──────────────┐
            │              │
            ▼              ▼
         Book #1        Book #2
```

---

# ▶️ How to Run

---
## Run with Docker Compose

Make sure Docker Desktop is running.
```bash
docker info
```
### Start

Check Status
```bash
docker compose ps
```
Expected services:
- library-api
- library-mysql

Stop
```bash
docker compose down
```

Restart
```bash
docker compose up -d
```

Check:
```bash
docker images
docker ps
```

delete images
```bash
docker rmi library-api:1.0.0
```
```bash
docker rmi ghcr.io/muhammadjafarshodiq0412/library-api:latest
```
```bash
docker rmi mysql:8.4   
```
### Rebuild/Build the image and start all services
If there are changes to the application code or Dockerfile:

Reset Database

To stop the containers and remove the MySQL volume:
```bash
docker compose down -v
```

Then start again:
```bash
docker compose up -d --build
```
OR
```bash
docker compose up -d --build --no-cache
```

> Warning: docker compose down -v will delete the local MySQL database and all stored data. Flyway migrations will run again from the beginning when the application starts.

--- 

# Manual Deployment to Kubernetes

The Kubernetes cluster is currently running locally using Docker Desktop.
The GitHub Actions deployment step is intentionally not used yet because the GitHub 
Actions runner cannot access the local 
Docker Desktop Kubernetes cluster.

## Prerequisites

Make sure:

- Docker Desktop is running
- Kubernetes is enabled in Docker Desktop
- `kubectl` is installed
- The current Kubernetes context is `docker-desktop`
- The required Docker image has already been pushed to GHCR/ECR (Service Registry)

Check the current context:

```bash
kubectl config get-contexts
```
Expected:
```text
CURRENT   NAME             CLUSTER          AUTHINFO         NAMESPACE
*         docker-desktop   docker-desktop   docker-desktop
```

### 0. Reset Kubernetes
```bash
kubectl delete -f k8s/deployment.yaml
```
```bash
kubectl delete -f k8s/secret.yaml
```
```bash
kubectl delete -f k8s/mysql.yaml
```

Check:
```bash
kubectl get pods
kubectl get deployments
kubectl get services
kubectl get pvc
kubectl get secrets
```

### 1. Verify the Kubernetes Cluster
Check the nodes:
```bash
kubectl get nodes
```
The node should be in Ready status.

Check existing pods:
```bash
kubectl get pods
```

### 2. Deploy MySQL
Apply the MySQL configuration:
```bash
kubectl apply -f k8s/mysql.yaml
```
Verify:
```bash
kubectl get pods
```
Wait until the MySQL pod is:
>1/1   Running

### 3. Deploy Database Secret
Apply the database secret:
```bash
kubectl apply -f k8s/secret.yaml
```
Verify:
```bash
kubectl get secrets
```

The following secret should exist:
> library-db

### 4. Deploy Library API
Apply the application deployment:
```bash
kubectl apply -f k8s/deployment.yaml
```

Check the deployment:
```bash 
kubectl get deployments
```
Expected:
```text
NAME          READY
library-api   2/2
mysql         1/1
```
Check the pods:
```bash
kubectl get pods
```
The Library API should have two running pods because the deployment is configured with:
> replicas: 2

### 5. Update the Docker Image
The Docker image is stored in GitHub Container Registry (GHCR).

To deploy a specific image version:
```bash
kubectl set image deployment/library-api \
  library-api=ghcr.io/muhammadjafarshodiq0412/library-api:<IMAGE_TAG>
```

For example:
```bash
kubectl set image deployment/library-api \
  library-api=ghcr.io/muhammadjafarshodiq0412/library-api:latest
```

### 6. Check Rollout Status
Check the deployment rollout:
```bash
kubectl rollout status deployment/library-api
```
Expected:
>deployment "library-api" successfully rolled out

### 7. Access the Application Locally
The library-api Kubernetes Service uses ClusterIP, so it is not directly accessible from the host machine.

Use port forwarding:
```bash
kubectl port-forward service/library-api 8080:80
```
Or use Lens to create the port forward.

---
## Run without Docker Compose and K8s

## 📦 Prerequisites

Before running this project, make sure your environment meets the following requirements:

```markdown
☕ JDK 17+
📦 Maven 3.9+
🐬 MySQL 8+
```

## 1️⃣ Create Database

Before running the application, create the database manually:

```sql
CREATE
DATABASE library;
```

> ℹ️ Hibernate is configured with ddl-auto: validate, so the database
schema is managed by Flyway rather than Hibernate.

## 2️⃣ Configure Database

### 🔹 Option A — Environment Variables

Set the required environment variables:

```bash
export DB_URL="jdbc:mysql://localhost:3306/library?useSSL=false&serverTimezone=Asia/Jakarta"
export DB_USERNAME="root"
export DB_PASSWORD="your_password"
```
Then start the application:
```bash
mvn spring-boot:run
```

### 🔹 Option B — Maven -D Properties
You can also provide the configuration directly through Maven without
setting environment variables:
```bash
mvn spring-boot:run \
  -Dspring-boot.run.profiles=dev \
  -Dspring-boot.run.jvmArguments="-DDB_URL=jdbc:mysql://localhost:3306/library?useSSL=false&serverTimezone=Asia/Jakarta -DDB_USERNAME=root -DDB_PASSWORD=your_password"
```

> ℹ️ The application reads DB_URL, DB_USERNAME, and DB_PASSWORD
from environment variables. When using Maven -D, the properties are
passed to the JVM as system properties.

If the application configuration is written to resolve system properties
directly, the simpler form can be used:
```bash
mvn spring-boot:run \
  -Dspring-boot.run.profiles=dev \
  -DDB_URL="jdbc:mysql://localhost:3306/library?useSSL=false&serverTimezone=Asia/Jakarta" \
  -DDB_USERNAME="root" \
  -DDB_PASSWORD="your_password"
```

## 📝 Logging Configuration

The application writes application logs to a configurable directory.

The logging directory is configured using:

```yaml
logging:
  location:
    saved: /your/local/path/library-api/logging
```

### 🔧 Update Logging Directory
1. Open:
```text
src/main/resources/application.yml
```
2. Locate:
```properties
logging.location.saved=/Users/jafarshodiq/Documents/library-api/logging
```

3. Change it to a valid directory on your machine:
```properties
logging.location.saved=/your/local/path/library-api/logging
```
For example:
logging.location.saved=/Users/yourname/Documents/library-api/logging

> ⚠️ Make sure the application has permission to write to the configured
directory.

# 🌐 REST API

Base URL:

```curl
http://localhost:8080/library-service
```

> Swagger UI:
http://localhost:8080/library-service/swagger-ui/index.html

---

### 👤 Borrower API

➕ Register Borrower

```http request
POST /borrowers
```

Request

```json
{
  "name": "Jafar",
  "email": "jafar@test.com"
}
```

Response

```http request
201 Created
```

```json
{
  "id": "c1bec5c1-ddb8-481a-bca5-85686d25f6be",
  "name": "Jafar",
  "email": "jafar@test.com"
}
```

Possible Errors

```http request
400 Bad Request
409 Conflict
```

---

### 👤 Book API

➕ Register Book

ISBN-13 uses a custom validation annotation:
The validator checks:

- 🔢 ISBN contains 13 digits
- 🔢 ISBN starts with 978 or 979
- 🔢 ISBN-13 check digit is valid

```http request
POST /books
```

Request

```json
{
  "isbn": "9780132350884",
  "title": "Become as expertise Java 21",
  "author": "Nika Jr"
}
```

Response

```http request
201 Created
```

```json
{
  "id": "6d751664-63e9-49a7-b238-c0a3da4713ac",
  "isbn": "9780132350884",
  "title": "Become as expertise Java 17",
  "author": "Nika Jr",
  "available": true
}
```

Possible Errors

```http request
400 Bad Request
409 Conflict
```

📋 List Books

```http request
GET /books
```

Returns all registered physical books together with their current
availability.

Response

```json
[
  {
    "id": "07dbfed5-66a5-4e0c-a4cb-9bae511b8b5c",
    "isbn": "9780132350884",
    "title": "Become as expertise Java 17",
    "author": "Nika Jr",
    "available": true
  },
  {
    "id": "6d751664-63e9-49a7-b238-c0a3da4713ac",
    "isbn": "9780132350884",
    "title": "Become as expertise Java 17",
    "author": "Nika Jr",
    "available": true
  },
  {
    "id": "88c73832-d162-431c-ab52-c995b7ea3cf4",
    "isbn": "9780132350884",
    "title": "Become as expertise Java 17",
    "author": "Nika Jr",
    "available": true
  }
]
```

---

### 🔄 Loan API

📥 Borrow Book

```http request
POST /loans/borrow
```

Request

```http request
curl -X 'POST' \
'http://localhost:8080/library-service/loans/borrow?borrowerId=c1bec5c1-ddb8-481a-bca5-85686d25f6be&bookId=07dbfed5-66a5-4e0c-a4cb-9bae511b8b5c' \
-H 'accept: */*' \
-d ''
```

Response success

```http request
201 Created
```

### Testing Concurrent Borrowing

The concurrency behavior can be tested by sending multiple requests for the
same `bookId` using different `borrowerId` values:

```bash
BOOK_ID="7cf0b96f-013d-4522-b811-1457237408c6"

REQUEST_NO=1

for BORROWER_ID in \
  "4e301360-7fe2-486d-b36e-fce971526ad0" \
  "7d3d0a15-ddcd-4bbb-a11b-84a981cfdfa9" \
  "82d22c57-19d5-4e57-8c7c-489adefe62d6"
do
  (
    echo "===== REQUEST ${REQUEST_NO} ====="
    echo "Borrower ID: ${BORROWER_ID}"
    echo "Book ID: ${BOOK_ID}"

    curl --location --request POST \
      "http://localhost:8080/library-service/loans/borrow?borrowerId=${BORROWER_ID}&bookId=${BOOK_ID}" \
      --header "X-Tracking-Ref: concurrent-${REQUEST_NO}" \
      --write-out "\nSTATUS: %{http_code}\n"

    echo
  ) &

  ((REQUEST_NO++))
done

wait
```

```json
{
  "loanId": "0ecb4802-3a43-42b5-97a7-f0f0e9ed009c",
  "bookId": "07dbfed5-66a5-4e0c-a4cb-9bae511b8b5c",
  "borrowerId": "c1bec5c1-ddb8-481a-bca5-85686d25f6be",
  "borrowedAt": "2026-08-27T09:31:23.612982Z"
}
```

Response conflict

```json
{
  "timestamp": "2026-08-27T16:33:08.702432+07:00",
  "status": 409,
  "error": "Conflict",
  "message": "Book is already borrowed: 07dbfed5-66a5-4e0c-a4cb-9bae511b8b5c",
  "path": "/library-service/loans/borrow",
  "violations": []
}
```

📥 Return Book

```http request
POST /loans/{loanId}/return
```

Request

```http request
curl -X 'POST' \
'http://localhost:8080/library-service/loans/0ecb4802-3a43-42b5-97a7-f0f0e9ed009c/return?borrowerId=c1bec5c1-ddb8-481a-bca5-85686d25f6be' \
-H 'accept: */*' \
-d ''
```

# ✨ Features

- 👤 Borrower registration with unique email validation
- 📚 Physical book registration
- 📖 ISBN-13 validation
- 🗂️ Shared ISBN metadata through `IsbnCatalog`
- 📋 Book listing with real-time availability
- 📥 Borrow book
- 📤 Return book
- 🔒 Concurrent borrow protection using pessimistic locking
- 🔄 Transaction-safe loan operations
- 🗃️ MySQL database with Flyway migrations
- 📖 Swagger / OpenAPI documentation
- 💚 Actuator health and metrics
- ⚙️ Environment-specific Spring profiles
- 🧪 Unit and integration testing
- 🐳 MySQL integration testing with Testcontainers
- 📊 JaCoCo code coverage
- 🔍 SonarQube static analysis
- ❌ Centralized exception handling
- ✅ Jakarta Bean Validation