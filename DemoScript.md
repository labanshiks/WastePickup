# Smart Waste Sorting and Pickup Request System

## Presentation Demo Script

---

## Introduction (2 minutes)

"Good [morning/afternoon]. Our project is the Smart
Waste Sorting and Pickup Request System.

In Nairobi, most urban residents live in apartments
managed by caretakers. Waste collection is currently
uncoordinated — caretakers have no way to formally
request pickups and city teams have no visibility
into demand.

Our system solves this by providing:

- A digital channel for caretakers to submit
  waste pickup requests
- A scheduling tool for admins to plan routes
  and assign vehicles
- Real time status tracking for both parties

This addresses SDG 11 — Sustainable Cities and
SDG 12 — Responsible Consumption."

---

## System Architecture (2 minutes)

"Our system is built using Object Oriented Programming
principles with three main layers:

1. **Model Layer** — 11 Java classes representing
   real world entities like User, Apartment,
   PickupRequest and Schedule

2. **DAO Layer** — 5 Data Access Objects that handle
   all database operations using JDBC and
   PreparedStatements

3. **UI Layer** — 7 JavaFX screens connected to
   controllers via FXML

Key OOP concepts demonstrated:

- Inheritance: Caretaker and Admin extend User
- Encapsulation: Private attributes with getters/setters
- Aggregation: Apartment has a Caretaker
- Composition: PickupRequest has a WasteCategory
- Enums: WasteCategory, RequestStatus, VehicleStatus
- Interfaces: DaoInterface implemented by all DAOs
- Collections: ArrayList in Schedule"

---

## Live Demo (8 minutes)

### Scene 1 — Login Screen (30 seconds)

- Open application
- Show login screen
- Demonstrate validation:
  - Click Login with empty fields
  - Show: "Please enter your username and password!"
- Say: "Our login screen validates input and routes
  users to role specific dashboards"

### Scene 2 — Caretaker Flow (3 minutes)

**Login as Caretaker:**

- Username: `caretaker1`
- Password: `care123`
- Click Login

**Dashboard:**

- Point out: "Welcome caretaker1 in the top right"
- Point out: "Pending, Scheduled and Completed counters"
- Point out: "My Requests table — currently empty"
- Say: "Each caretaker only sees their own
  apartment's requests"

**Submit Request 1:**

- Click New Pickup Request
- Show apartment auto-filled: "Greenpark Apartments"
- Select category: `PLASTIC`
- Enter weight: `50`
- Select from date: tomorrow
- Select to date: day after tomorrow
- Click Submit Request
- Show success popup
- Return to dashboard — pending count shows 1

**Submit Request 2:**

- Click New Pickup Request again
- Select category: `ORGANIC`
- Enter weight: `80`
- Select dates
- Click Submit Request
- Dashboard pending count shows 2

**Demonstrate duplicate prevention:**

- Click New Pickup Request
- Select category: `PLASTIC` again
- Enter weight: `30`
- Select dates
- Click Submit Request
- Show error: "A PENDING or SCHEDULED PLASTIC
  request already exists!"
- Say: "Our system prevents duplicate requests
  for the same waste category"

**Demonstrate cancel:**

- Go back to dashboard
- Submit a new `METAL` request, weight `40`
- Select it in the table
- Click Cancel Request
- Confirm cancellation
- Show it disappears from table
- Say: "Caretakers can cancel requests before
  they are scheduled"

**Logout**

### Scene 3 — Second Caretaker (1 minute)

**Login as caretaker2:**

- Username: `caretaker2`
- Password: `care123`

**Submit requests:**

- Submit `GLASS` request, weight `30`, future dates
- Submit `PAPER` request, weight `45`, future dates
- Show dashboard with 2 pending requests
- Say: "Each caretaker manages their own apartment
  independently"

**Logout**

### Scene 4 — Admin Flow (4 minutes)

**Login as Admin:**

- Username: `admin1`
- Password: `admin123`

**Admin Dashboard:**

- Point out blue theme vs green caretaker theme
- Point out: "Pending Requests shows 4 — requests
  from ALL apartments"
- Point out: "Available Vehicles shows 3"
- Say: "Admins have visibility across all apartments"

**Schedule Pickup:**

- Click Schedule Pickup
- Show all 4 pending requests from different apartments
- Fill in:
  - Pickup Date: tomorrow's date
  - Time Slot: `8AM - 10AM`
  - Route Name: `Kilimani Morning Route`
  - Vehicle: `KCA 123A (500.0kg)`
- Check requests 1 and 2 (PLASTIC and ORGANIC)
- Show Selected Weight updating: `130.0 kg`
- Say: "The system tracks total weight against
  vehicle capacity in real time"
- Click Create Schedule
- Show success popup
- Return to dashboard — pending drops to 2

**Demonstrate past date validation:**

- Click Schedule Pickup again
- Select a past date
- Click Create Schedule
- Show: "Pickup date cannot be in the past!"
- Go back

**Manage Schedules:**

- Click Manage Schedules
- Show the schedule we just created
- Point out vehicle plate, route, date
- Select the schedule
- Click Mark Completed
- Show success message
- Say: "When marked complete, the vehicle
  automatically becomes available again
  and all linked requests update to COMPLETED"

**Reports:**

- Click Reports
- Point out four report sections:
  - Requests by Status — shows COMPLETED and PENDING
  - Requests by Category — shows distribution
  - Most Active Apartments — Greenpark leads
  - Most Active Vehicles — KCA 123A
- Click Export to CSV
- Show success popup
- Say: "Reports can be exported for further
  analysis in Excel"

**Logout**

### Scene 5 — Caretaker Confirms (30 seconds)

- Login as `caretaker1`
- Show dashboard:
  - Completed count shows 2
  - Pending count shows 0
- Show My Requests table:
  - PLASTIC and ORGANIC show as COMPLETED
  - Pickup Date and Vehicle visible
- Say: "Caretakers can see exactly when their
  waste was collected and which vehicle did it"
- Logout

---

## OOP Concepts Summary (2 minutes)

"Let me highlight the key OOP concepts we demonstrated:

**Encapsulation:**
All model attributes are private with controlled
access through getters and setters. For example
RequestStatus and createdAt are set automatically
in the constructor — no external class can
interfere with these values.

**Inheritance:**
Caretaker and Admin both extend User, inheriting
common attributes like id, username and password
while adding their own unique attributes.

**Interfaces:**
DaoInterface defines a contract that all five DAO
classes must implement, ensuring consistency across
the data access layer.

**Enums:**
WasteCategory, RequestStatus and VehicleStatus
prevent invalid data entry at the Java level,
complementing the MySQL ENUM constraints at
the database level.

**Collections:**
Schedule uses an ArrayList of PickupRequests,
mirrored in the database by the schedule_requests
join table.

**Design Patterns:**
DBConnection implements the Singleton pattern —
only one database connection exists throughout
the application lifecycle."

---

## Known Improvements (1 minute)

"If we were to continue developing this system:

1. **Service Layer** — Business logic would move
   from controllers into dedicated service classes
   for cleaner separation of concerns

2. **Password Hashing** — Passwords would be
   stored as bcrypt hashes instead of plain text

3. **GPS Routing** — Vehicle routes would use
   real coordinates for optimized pickup paths

4. **Tenant Portal** — A read only view for
   apartment tenants to track pickup schedules

5. **Push Notifications** — SMS alerts to
   caretakers when pickups are scheduled"

---

## Closing (30 seconds)

"Our system demonstrates how OOP principles can
solve a real urban challenge in the Kenyan context.
By modeling real world entities as Java classes
and applying inheritance, encapsulation and
interfaces, we built a maintainable and extensible
application.

Thank you. We are happy to take questions."

---

## Anticipated Questions and Answers

**Q: Why did you use JavaFX instead of a web interface?**
A: "The project requirements specified JavaFX.
However the layered architecture we built would
make it straightforward to replace the UI layer
with a web frontend without changing the model
or DAO layers."

**Q: How did you prevent SQL injection?**
A: "We used PreparedStatements throughout all DAO
classes. The ? placeholders ensure user input is
always treated as data, never as SQL code."

**Q: What is the Singleton pattern?**
A: "DBConnection uses Singleton to ensure only one
database connection exists at a time. The private
constructor prevents instantiation and the static
getConnection() method returns the same instance
every time."

**Q: Why use enums instead of strings for status?**
A: "Enums restrict values to a predefined set,
preventing invalid states. For example RequestStatus
ensures a request can only be PENDING, APPROVED,
SCHEDULED, COMPLETED, MISSED or CANCELLED —
never an arbitrary string like 'done' or 'finished'."

**Q: How does the join table work?**
A: "The schedule_requests table mirrors our Java
ArrayList relationship. One schedule can have many
requests and the join table stores each relationship
as a row with schedule_id and request_id. This is
the database equivalent of ArrayList in Schedule.java."
