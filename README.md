# Library Management System
### Java OOP Project — MVC + SOLID + GRASP + Design Patterns

---

## 📁 Folder Structure

```
LibraryManagementSystem/
├── LibraryManagementSystem.jar        ← Runnable JAR
├── sources.txt                        ← Compiler source list
├── manifest.txt                       ← JAR manifest
│
└── src/main/java/com/library/
    │
    ├── Main.java                      ← Entry point & DI wiring
    │
    ├── enums/
    │   ├── BookStatus.java            ← AVAILABLE, ISSUED, DAMAGED, UNDER_REPAIR, REMOVED
    │   ├── TransactionStatus.java     ← ISSUED, RETURNED, OVERDUE, CLOSED
    │   ├── FineStatus.java            ← UNPAID, PAID
    │   └── StaffRole.java             ← CLEANER, SECURITY, REPAIR_STAFF
    │
    ├── model/                         ← M in MVC (Plain domain objects)
    │   ├── Book.java
    │   ├── User.java
    │   ├── Librarian.java
    │   ├── Admin.java
    │   ├── Transaction.java
    │   ├── Fine.java
    │   ├── Notification.java
    │   └── MaintenanceStaff.java
    │
    ├── repository/                    ← Data Access layer (Repository Pattern)
    │   ├── Repository.java            ← Generic interface
    │   ├── BookRepository.java
    │   ├── UserRepository.java
    │   ├── LibrarianRepository.java
    │   ├── TransactionRepository.java
    │   ├── FineRepository.java
    │   ├── NotificationRepository.java
    │   └── MaintenanceStaffRepository.java
    │
    ├── singleton/
    │   └── LibraryDatabase.java       ← Singleton in-memory store
    │
    ├── factory/                       ← Factory Pattern
    │   ├── EntityFactory.java         ← Generic factory interface
    │   ├── BookFactory.java
    │   ├── UserFactory.java
    │   ├── TransactionFactory.java
    │   └── FineFactory.java
    │
    ├── observer/                      ← Observer Pattern
    │   ├── Observable.java            ← Subject interface
    │   ├── LibraryObserver.java       ← Observer interface
    │   ├── UserNotificationObserver.java
    │   └── MaintenanceStaffObserver.java
    │
    ├── strategy/                      ← Strategy Pattern
    │   ├── BookSearchStrategy.java    ← Search strategy interface
    │   ├── SearchByTitleStrategy.java
    │   ├── SearchByAuthorStrategy.java
    │   ├── SearchByCategoryStrategy.java
    │   ├── SearchByISBNStrategy.java
    │   ├── FineCalculationStrategy.java ← Fine strategy interface
    │   ├── StandardFineStrategy.java    ← ₹5/day flat
    │   └── ProgressiveFineStrategy.java ← ₹5→10→20/day escalating
    │
    ├── service/                       ← Business logic layer
    │   ├── BookService.java
    │   ├── UserService.java
    │   ├── LibrarianService.java
    │   ├── TransactionService.java    ← Core: issue, return, fine, pay
    │   ├── AdminService.java
    │   ├── MaintenanceService.java
    │   └── ReportService.java
    │
    ├── controller/                    ← C in MVC
    │   ├── BookController.java
    │   ├── UserController.java
    │   ├── LibrarianController.java
    │   ├── TransactionController.java
    │   ├── AdminController.java
    │   └── MaintenanceController.java
    │
    ├── view/                          ← V in MVC (Swing UI)
    │   └── LibraryApp.java            ← Full dark-themed Swing GUI
    │
    └── util/
        ├── IDGenerator.java
        └── DataSeeder.java            ← Seeds 10 books, 5 users, 2 librarians on startup
```

---

## 🏗️ Architecture & Design Principles

### MVC Architecture
| Layer       | Package        | Responsibility                                      |
|-------------|----------------|-----------------------------------------------------|
| **Model**   | `model/`       | Pure domain objects — no logic, no UI               |
| **View**    | `view/`        | Swing UI — only talks to Controllers                |
| **Controller** | `controller/` | Receives UI events, delegates to Services          |
| **Service** | `service/`     | All business logic lives here                       |
| **Repository** | `repository/` | Data access — reads/writes to LibraryDatabase     |

---

### SOLID Principles Applied

| Principle | Where Applied |
|-----------|--------------|
| **S** – Single Responsibility | Each class has one job: `BookService` manages books, `TransactionService` manages transactions, `ReportService` generates reports |
| **O** – Open/Closed | `BookSearchStrategy` & `FineCalculationStrategy` — add new strategies without modifying existing code |
| **L** – Liskov Substitution | All strategy implementations are substitutable; all repository implementations honour the `Repository<T,ID>` contract |
| **I** – Interface Segregation | `Observable` and `LibraryObserver` are small, focused interfaces; `Repository<T,ID>` is generic and minimal |
| **D** – Dependency Inversion | Services depend on `Repository` interfaces; `Main.java` wires concrete implementations via constructor injection |

---

### GRASP Principles Applied

| Principle | Where Applied |
|-----------|--------------|
| **Creator** | `TransactionService` creates `Transaction` and `Fine` objects (it has the data needed) |
| **Controller** | `TransactionController`, `BookController` etc. handle system events from the UI |
| **Information Expert** | `Transaction.isOverdue()` and `Transaction.getDaysLate()` live on `Transaction` since it owns the dates |
| **Low Coupling** | Controllers only depend on their single Service; Services depend on Repository interfaces |
| **High Cohesion** | Each service/controller is focused on exactly one domain concept |
| **Polymorphism** | Strategy pattern gives polymorphic behaviour for search and fine calculation |
| **Pure Fabrication** | `ReportService` and `DataSeeder` are non-domain classes created for system organisation |
| **Indirection** | Repositories act as an indirection layer between Services and the Singleton database |

---

### Design Patterns Applied

| Pattern | Class(es) | Purpose |
|---------|-----------|---------|
| **Singleton** | `LibraryDatabase` | Single shared in-memory data store |
| **Factory Method** | `BookFactory`, `UserFactory`, `TransactionFactory`, `FineFactory` | Encapsulate object creation |
| **Observer** | `Observable`, `LibraryObserver`, `UserNotificationObserver`, `MaintenanceStaffObserver` | Auto-notify users and staff on events |
| **Strategy** | `BookSearchStrategy` (4 impls), `FineCalculationStrategy` (2 impls) | Swap algorithms at runtime |
| **Repository** | All `*Repository` classes | Abstract data access from business logic |
| **MVC** | `model/`, `view/`, `controller/` | Separate UI from business logic |

---

## 🖥️ How to Run

### Prerequisites
- Java 17 or later (`java -version`)

### Run from JAR
```bash
java -jar LibraryManagementSystem.jar
```

### Compile & Run from Source
```bash
# Compile
find src -name "*.java" > sources.txt
javac --release 17 -d out @sources.txt

# Package
jar cfm LibraryManagementSystem.jar manifest.txt -C out .

# Run
java -jar LibraryManagementSystem.jar
```

---

## 🎮 Using the Application

### Pre-loaded Demo Data
On startup the system seeds:
- **10 books** across Technology, Psychology, History, Fiction, Philosophy, Self-Help
- **5 registered members** (USR-001 to USR-005)
- **2 librarians** (use their Librarian IDs when issuing/returning)
- **3 maintenance staff** (Cleaner, Security, Repair)
- **1 admin** — username: `admin` / password: `admin123`

### Typical Workflows

#### Issue a Book
1. Click **Issue Book** in sidebar
2. Enter Member ID (e.g. `USR-001`), Book ID (e.g. `BK-003`), Librarian ID (check Librarians panel)
3. Click **Issue Book** → Transaction ID is shown

#### Return a Book
1. Click **Return Book** in sidebar
2. Enter Transaction ID from the issue step
3. Click **Process Return** → shows fine details if overdue

#### Pay a Fine
1. Either use the **Fines** panel → enter Fine ID → **Pay Fine**
2. Or note the Fine ID from the Return result and pay immediately

#### Search Books
- In **Books** panel, choose search type (Title/Author/Category/ISBN) and enter query

#### Mark Book as Damaged
- Select a book in the Books table → click **Mark Damaged**
- The Observer pattern fires a maintenance alert automatically

#### Admin Reports
- Click **Reports** to see full transaction history, overdue list, fine collection, and notifications across 4 tabs

---

## 📊 Key Business Rules

| Rule | Implementation |
|------|---------------|
| Loan period | 14 days from issue date |
| Standard fine | ₹5 per day late (configurable via Strategy) |
| Progressive fine | ₹5/day (1-7) → ₹10/day (8-14) → ₹20/day (15+) |
| Block on fine | User with unpaid fine cannot borrow new books |
| Book availability | Automatically updated on issue and return |
| Notifications | Auto-generated via Observer pattern for every event |
| Damage alert | Maintenance staff notified automatically via Observer |
