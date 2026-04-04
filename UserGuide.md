# Smart Waste Sorting and Pickup Request System

## User Guide

### About the System

The Smart Waste Sorting and Pickup Request System is a
desktop application that allows apartment caretakers to
submit waste pickup requests and city waste management
admins to schedule and track pickups.

**SDG Alignment:**

- SDG 11: Sustainable Cities and Communities
- SDG 12: Responsible Consumption and Production

---

## Getting Started

### System Requirements

- Java JDK 21 or higher
- JavaFX SDK 21
- MySQL 8.0 or higher
- Windows Operating System

### Installation

#### For the Project Owner

1. Ensure MySQL is running
2. Update database credentials in `util/DBConnection.java`
3. Run the application using `run.bat`

#### For Collaborators (Cloning the Repository)

1. Clone the repository: git clone https://github.com/labanshiks/WastePickup.git
2. Download JavaFX SDK 21 from gluonhq.com/products/javafx
   - Version: 21.0.10
   - OS: Windows
   - Type: SDK
3. Extract JavaFX to `C:\javafx-sdk-21`
4. Update the JavaFX path in `.vscode/settings.json`
   to match your installation path
5. Download MySQL JDBC Driver from
   dev.mysql.com/downloads/connector/j/
   - Place the `.jar` file in the `lib/` folder
6. Set up MySQL database:
   - Open MySQL terminal
   - Run: `CREATE DATABASE wastepickup;`
   - Create all tables using the schema below
7. Update database credentials in `util/DBConnection.java`:

```java
   private static final String URL =
       "jdbc:mysql://localhost:3306/wastepickup";
   private static final String USERNAME = "root";
   private static final String PASSWORD = "<your_root_password>";
```

8. Run the application using `run.bat`

#### Database Schema

Run these SQL commands in order after creating the database:

```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('CARETAKER', 'ADMIN') NOT NULL
);

CREATE TABLE caretakers (
    user_id INT PRIMARY KEY,
    phone VARCHAR(15) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE
);

CREATE TABLE admins (
    user_id INT PRIMARY KEY,
    department VARCHAR(100) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE
);

CREATE TABLE apartments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    estate VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    number_of_units INT NOT NULL,
    caretaker_user_id INT,
    FOREIGN KEY (caretaker_user_id)
    REFERENCES caretakers(user_id)
    ON DELETE SET NULL
);

CREATE TABLE vehicles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    plate VARCHAR(20) NOT NULL UNIQUE,
    capacity_kg DOUBLE NOT NULL,
    status ENUM('AVAILABLE','ON_ROUTE','MAINTENANCE')
    DEFAULT 'AVAILABLE'
);

CREATE TABLE pickup_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    apartment_id INT NOT NULL,
    category ENUM('PLASTIC','ORGANIC','GLASS',
                  'PAPER','METAL','E_WASTE','HAZARDOUS')
                  NOT NULL,
    estimated_weight_kg DOUBLE NOT NULL,
    preferred_from DATE NOT NULL,
    preferred_to DATE NOT NULL,
    status ENUM('PENDING','APPROVED','SCHEDULED',
                'COMPLETED','MISSED','CANCELLED')
                DEFAULT 'PENDING',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (apartment_id) REFERENCES apartments(id)
    ON DELETE CASCADE
);

CREATE TABLE schedules (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pickup_date DATE NOT NULL,
    time_slot VARCHAR(50) NOT NULL,
    route_name VARCHAR(100) NOT NULL,
    vehicle_id INT NOT NULL,
    status ENUM('PENDING','ONGOING',
                'COMPLETED','CANCELLED')
                DEFAULT 'PENDING',
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
    ON DELETE CASCADE
);

CREATE TABLE schedule_requests (
    schedule_id INT NOT NULL,
    request_id INT NOT NULL,
    PRIMARY KEY (schedule_id, request_id),
    FOREIGN KEY (schedule_id) REFERENCES schedules(id)
    ON DELETE CASCADE,
    FOREIGN KEY (request_id)
    REFERENCES pickup_requests(id)
    ON DELETE CASCADE
);

### Default Login Credentials

| Username   | Password | Role      |
| ---------- | -------- | --------- |
| admin1     | admin123 | Admin     |
| admin2     | admin123 | Admin     |
| caretaker1 | care123  | Caretaker |
| caretaker2 | care123  | Caretaker |
| caretaker3 | care123  | Caretaker |

---

## Caretaker Guide

### Logging In

1. Open the application
2. Enter your username and password
3. Click **Login**
4. You will be directed to your dashboard

### Viewing Your Dashboard

The dashboard shows:

- **Pending Requests** — requests awaiting scheduling
- **Scheduled** — requests assigned to a pickup route
- **Completed** — successfully collected requests
- **My Requests table** — full request history with
  pickup dates and assigned vehicles

### Submitting a Pickup Request

1. Click **New Pickup Request** in the sidebar
2. Select a **Waste Category** from the dropdown
3. Enter the **Estimated Weight** in kg
4. Select your **Preferred Pickup From** date
5. Select your **Preferred Pickup To** date
6. Click **Submit Request**

**Rules:**

- Weight must be greater than 0 kg
- Weight cannot exceed 1000 kg
- Pickup window cannot exceed 7 days
- Cannot submit duplicate requests for
  the same category if one is already
  PENDING or SCHEDULED

### Cancelling a Request

1. Click on a PENDING request in the table
2. Click **✗ Cancel Request**
3. Confirm cancellation in the dialog
4. Only PENDING requests can be cancelled

### Logging Out

Click the **Logout** button in the top right corner

---

## Admin Guide

### Logging In

1. Open the application
2. Enter your admin username and password
3. Click **Login**
4. You will be directed to the admin dashboard

### Viewing the Admin Dashboard

The dashboard shows:

- **Pending Requests** — all unscheduled requests
- **Scheduled** — requests assigned to routes
- **Available Vehicles** — vehicles ready for assignment
- **Pending Requests table** — all requests from
  all apartments

### Scheduling a Pickup

1. Click **Schedule Pickup** in the sidebar
2. Select a **Pickup Date** (must be today or future)
3. Select a **Time Slot**
4. Enter a **Route Name** e.g "Kilimani Morning Route"
5. Select an **Available Vehicle**
6. Check the requests to include in this schedule
7. Monitor **Selected Weight** vs **Vehicle Capacity**
8. Click **Create Schedule**

**Rules:**

- Pickup date cannot be in the past
- Total weight of selected requests cannot
  exceed vehicle capacity
- At least one request must be selected

### Managing Schedules

1. Click **Manage Schedules** in the sidebar
2. Select a schedule from the table
3. Click **✓ Mark Completed** if pickup was done
4. Click **✗ Mark Missed** if pickup was not done

**What happens on completion:**

- Schedule status → COMPLETED
- Vehicle status → AVAILABLE
- Linked requests status → COMPLETED

**What happens on missed:**

- Schedule status → CANCELLED
- Vehicle status → AVAILABLE
- Linked requests status → MISSED

### Viewing Reports

1. Click **Reports** in the sidebar
2. View four report sections:
   - Requests by Status
   - Requests by Category
   - Most Active Apartments
   - Most Active Vehicles
3. Click **Export to CSV** to download the report

---

## Waste Categories

| Category  | Description                       |
| --------- | --------------------------------- |
| PLASTIC   | Plastic bottles, bags, containers |
| ORGANIC   | Food waste, garden waste          |
| GLASS     | Glass bottles and containers      |
| PAPER     | Newspapers, cardboard, books      |
| METAL     | Cans, metal containers            |
| E_WASTE   | Electronics, batteries, cables    |
| HAZARDOUS | Chemicals, paint, medical waste   |

---

## Troubleshooting

| Problem                  | Solution                     |
| ------------------------ | ---------------------------- |
| Cannot login             | Check username and password  |
| Database error           | Ensure MySQL is running      |
| Application not starting | Check Java JDK version       |
| Cannot submit request    | Check for duplicate requests |
| No vehicles showing      | All vehicles may be ON_ROUTE |

---

_Developed by: Brian Laban Shikuku and Janet Ndanu_
_Institution: Strathmore University_
_Course: Object Oriented Programming_
_Year: 2026_
```
