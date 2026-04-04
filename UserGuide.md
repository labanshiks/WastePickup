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

1. Clone the repository from GitHub
2. Set up MySQL database:
   - Open MySQL terminal
   - Run: `CREATE DATABASE wastepickup;`
   - Import the database schema
3. Update database credentials in `util/DBConnection.java`
4. Run the application using `run.bat`

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

| Problem                 | Solution                     |
| ----------------------- | ---------------------------- |
| Cannot login            | Check username and password  |
| Database error          | Ensure MySQL is running      |
| Application won't start | Check Java JDK version       |
| Cannot submit request   | Check for duplicate requests |
| No vehicles showing     | All vehicles may be ON_ROUTE |

---

_Developed by: Brian Laban Shikuku and Janet Ndanu_
_Institution: Strathmore University_
_Course: Object Oriented Programming_
_Year: 2026_
