<div align="center">


# <img src="https://flagcdn.com/32x24/ps.png" alt="Palestine Flag" width="28" valign="middle"/> GHADS Pro — Gaza Aid Tracker

![Java](https://img.shields.io/badge/Java-8-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-GUI-0056A6?style=for-the-badge&logo=java&logoColor=white)
![NetBeans](https://img.shields.io/badge/NetBeans-IDE-1B6AC6?style=for-the-badge&logo=apache-netbeansIDE&logoColor=white)
![OOP](https://img.shields.io/badge/Style-OOP-6A5ACD?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Academic%20Project-2EA043?style=for-the-badge)

A desktop application for managing and tracking humanitarian aid distribution operations for families in the Gaza Strip.

</div>

The project was developed using (Java 8) and (JavaFX) in (NetBeans IDE), applying (Object-Oriented Programming (OOP)) principles and storing data locally using (UTF-8) encoding.

## 📑 Table of Contents

- Interface Type
- Team Members
- AI Usage Declaration
- Project Links
- Project Overview
- Key Features
- Distribution Statuses
- Eligibility Verification
- Inventory Management
- User Roles and Permissions
- Technologies Used
- Project Structure
- Class Descriptions
- Object-Oriented Programming Concepts
- Priority Sorting
- Data Storage
- Input Validation
- Dashboard Calculations
- Running the Project in NetBeans
- How to Use the Application
- Current Academic Scope
- Future Improvements
- Academic Objective
- Notice

---

## 🖥️ Interface Type

This project uses a **JavaFX GUI** (desktop application), not a console interface.

## 👥 Team Members

| Name | ID | Role |
|---|---|---|
| Noura Mahmoud Taysir Srour | 2549011082 |  Team Leader |
| Afnan Mohamed Abdel Qader Zaqout | 2549011044 | Member |
| Huda Abdallah Suleiman Abu Tailkh | 2549011077 | Member |
| Shahd Hassan Khalil Abu Al-Aish | 2549011055 | Member |

## 🤖 AI Usage Declaration

The core project idea, class architecture, business logic, and the complete implementation were developed entirely by the project team. We used AI tools (in a limited capacity as a learning and guidance assistant during the JavaFX GUI design phase, specifically by describing the required functional mechanisms for the interface and receiving general design guidance. The design of core classes, business logic, and the integration of all system components were achieved and thoroughly understood by the team members, who reviewed, tested, and adapted any AI-related design suggestions before including them in the final project.

## 🔗 Project Links

- 🎥 Video Presentation: [https://youtu.be/mCyRo4I89ss](https://youtu.be/mCyRo4I89ss)

## 📖 Project Overview

The GHADS Pro system helps humanitarian organizations and field volunteers record aid distribution operations, track beneficiary family data, manage available inventory, verify family eligibility, and arrange cases according to priority.

> [!NOTE]
> **Priority is based on the following rules:**
> 1. Displaced families are prioritized first.
> 2. When displacement status is the same, priority is given to the family with the larger number of members.

## ✨ Key Features

- A modern Arabic user interface with right-to-left layout support.
- A dashboard that displays:
  - The number of unique registered family IDs
  - The initial inventory
  - The total number of units actually delivered
  - The remaining inventory balance
- Add new distribution records.
- Edit existing records.
- Delete records using the distribution receipt number.
- Prevent duplicate distribution receipt numbers.
- Automatically generate a new family ID.
- Automatically save records in the aid_data.txt file.
- Restore saved data when the application starts.
- Store Arabic text correctly using UTF-8 encoding.
- Verify that the family member count and distributed quantity are greater than zero.
- Prevent the delivered quantity from exceeding the available inventory.
- Prevent the inventory balance from becoming negative.
- Exclude waiting-list record quantities from the delivered total.
- Preserve the original distribution status when reloading data.
- Automatically record the current date when aid is actually delivered.
- Verify that at least 30 days have passed since the previous aid delivery.
- Sort families by priority using Comparable and Collections.sort().
- Hash passwords using SHA-256.

## 📦 Distribution Statuses

The system uses two aid distribution statuses:

| Status | Meaning |
|---|---|
| 🟢 **Delivered** | This means that the family is eligible and the delivery operation has been recorded successfully. |
| 🟡 **On Waiting List** (Less than 30 days) | This means that the family received aid less than 30 days ago and has therefore been placed on the waiting list. |

> [!IMPORTANT]
> Quantities associated with waiting-list records are not deducted from the inventory.

## ✅ Eligibility Verification

A family is considered eligible to receive aid when:

- It has not received aid previously.
- Or at least 30 days have passed since the previous aid delivery.

The following entries can be used to indicate that the family has never received aid:

- `Never`
- `لم يتلقَ`
- `لا يوجد`

Any other date must be entered using the following format: `yyyy-MM-dd` -> Example: `2026-07-14`

When aid is actually delivered, the system automatically records the current date using: `LocalDate.now()`

## 📊 Inventory Management

The initial inventory defined in the application is: **5000 units**

Only records with the following status are included in the delivered quantity: **Delivered**

The remaining balance is calculated as follows: `Remaining balance = Initial inventory - Total units actually delivered`

> [!IMPORTANT]
> When adding a new distribution operation or editing the quantity of a delivered record, the application verifies that the entered quantity does not exceed the available inventory.

## 🔐 User Roles and Permissions

### 🙋 Volunteer

The volunteer can:
- ✅ View distribution records.
- ✅ Add new distribution records.
- ✅ Apply priority-based sorting.
- ❌ Cannot edit existing records.
- ❌ Cannot delete records.

### 🏢 UNICEF Organization

The organization user can:
- ✅ View distribution records.
- ✅ Add new distribution records.
- ✅ Edit existing records.
- ✅ Delete records.
- ✅ Apply priority-based sorting.
- ✅ Manage distribution operations within the assigned coverage area.

> [!NOTE]
> When switching between roles, the application displays a password verification dialog.
> The current demonstration password is: `123`
> The password is verified using: `checkPassword()`
> Passwords are stored inside user objects as hashed values using SHA-256 instead of being stored as plain text.

> [!WARNING]
> The current accounts and password are intended for academic demonstration purposes only.

## 🛠️ Technologies Used

![Java](https://img.shields.io/badge/-Java%208-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/-JavaFX-0056A6?style=flat-square)
![NetBeans](https://img.shields.io/badge/-NetBeans%20IDE-1B6AC6?style=flat-square)
![OOP](https://img.shields.io/badge/-OOP-6A5ACD?style=flat-square)
![Collections](https://img.shields.io/badge/-Java%20Collections-4B8BBE?style=flat-square)
![SHA256](https://img.shields.io/badge/-SHA--256-C1272D?style=flat-square)
![UTF8](https://img.shields.io/badge/-UTF--8%20Encoding-2EA043?style=flat-square)

- Java 8
- JavaFX
- NetBeans IDE
- Object-Oriented Programming (OOP)
- Inheritance
- Abstraction
- Encapsulation
- Polymorphism
- Java Collections Framework
- Comparable
- Collections.sort()
- File Handling
- UTF-8 Encoding
- SHA-256 Password Hashing
- Java Time API

## 📁 Project Structure

```
Gaza_Aid_Tracker/
├── src/
│   └── gaza/
│       └── aid/
│           └── tracker/
│               ├── GHADSApp.java
│               ├── AidDistribution.java
│               ├── DatabaseManager.java
│               ├── Family.java
│               ├── Organization.java
│               ├── User.java
│               └── Volunteer.java
├── nbproject/
├── aid_data.txt
├── build.xml
├── manifest.mf
└── README.md
```

## 🧩 Class Descriptions

| Class | Responsibility |
|---|---|
| `GHADSApp` | The main class of the JavaFX application. It manages the user interface, dashboard, user roles, add, edit, and delete operations, input validation, inventory calculations, and priority sorting. |
| `AidDistribution` | Represents a single aid distribution operation and connects the family, organization, quantity, and distribution status. |
| `DatabaseManager` | Saves and reloads distribution records from the aid_data.txt file using UTF-8 encoding. |
| `Family` | Represents beneficiary family data and contains the logic for processing the last aid date and verifying eligibility. |
| `User` | An abstract parent class containing shared user data, password hashing and verification, and the abstract permissions method. |
| `Organization` | Represents the humanitarian organization and inherits from the User class. |
| `Volunteer` | Represents the field volunteer and inherits from the User class. |

## 🧠 Object-Oriented Programming Concepts

### 1️⃣ Abstraction

The User class is defined as an abstract class: `public abstract class User`
It contains the following abstract method: `public abstract String getRolePermissions();`

### 2️⃣ Inheritance

The Organization and Volunteer classes inherit from the User class:
- `public class Organization extends User`
- `public class Volunteer extends User`

### 3️⃣ Encapsulation

Class attributes are declared using private, and they are accessed and modified through getter and setter methods.

### 4️⃣ Polymorphism

The Organization and Volunteer classes provide different implementations of the following method: `getRolePermissions()`

### 5️⃣ Comparison and Sorting

The AidDistribution class implements the following interface: `Comparable<AidDistribution>`
The application then sorts the distribution list using: `Collections.sort(distributionList)`

## 🔢 Priority Sorting

The `compareTo()` method applies the following rules:

- A displaced family is placed before a non-displaced family.
- If both families have the same displacement status, the family with the larger number of members is placed first.

**Example:**
1. Displaced family with 8 members
2. Displaced family with 5 members
3. Non-displaced family with 7 members
4. Non-displaced family with 3 members

## 💾 Data Storage

The application stores data in the following file: `aid_data.txt`

Each record is stored on a separate line, and the fields are separated using a semicolon: `;`

The fields are stored in the following order:
1. Distribution ID
2. Distributed quantity
3. Family name
4. Family ID
5. Number of family members
6. Contact number
7. Displacement status
8. Last aid date
9. Distribution status

**Example:**
```
REC-01;50;Ahmed Family;100;6;0590000000;true;2026-07-14;Delivered
```

> [!NOTE]
> When reloading the data, the application uses a dedicated constructor in AidDistribution to preserve the saved status and date without re-executing the eligibility logic.
> Before saving text values, semicolons and line breaks are removed to prevent damage to the file structure.

## 🧪 Input Validation

The application verifies the following:
- The distribution receipt number is not empty.
- The family name is not empty.
- The contact number is not empty.
- The number of family members is greater than zero.
- The distributed quantity is greater than zero.
- The distribution receipt number is not duplicated.
- The last aid date is entered in the correct format.
- The delivered quantity does not exceed the available inventory.
- The updated quantity of a delivered record does not exceed the available inventory after recalculation.

## 📈 Dashboard Calculations

### 1. Number of Registered Families

The dashboard calculates unique family IDs using: `HashSet<Integer>`

> [!NOTE]
> The application prevents duplicate family IDs from being counted in the dashboard, but it does not currently verify whether two records belong to the same family based on the family name or contact number.

### 2. Total Delivered Units

Only records with the Delivered status are included in the calculation.

### 3. Remaining Balance

`Remaining balance = 5000 - Total delivered units`

## ▶️ Running the Project in NetBeans

1. Extract the project folder.
2. Open NetBeans IDE.
3. Select: File → Open Project
4. Choose the project folder.
5. Right-click the project name.
6. Select: Run

If NetBeans asks you to select the main class, choose: `gaza.aid.tracker.GHADSApp`
The application can also be run directly from the following file: `GHADSApp.java`

## 📝 How to Use the Application

1. Run the project.
2. The application starts with the Volunteer role by default.
3. Use the role selection menu to switch between:
   - Volunteer
   - Organization
4. Enter the demonstration password when the verification dialog appears.
5. Use the available operations:
   - Add a new distribution record.
   - Edit a record when using the organization role.
   - Delete a record when using the organization role.
   - Apply priority-based sorting.
6. All new changes are saved in aid_data.txt.

## 🎓 Current Academic Scope

The project was designed as an academic desktop application. The current version uses:
- A text file instead of a database.
- One demonstration account for the organization.
- One demonstration account for the volunteer.
- The same demonstration password for both accounts.
- A new family ID for each new distribution record.
- No separate system administrator role.
- No current verification of duplicate families using the family name and phone number.

## 🚀 Future Improvements

- Use MySQL or SQLite instead of a text file.
- Add a separate login screen.
- Create multiple user accounts.
- Assign a different password to each account.
- Add a system administrator role.
- Detect duplicate families using a national ID, a permanent family ID, or a verified phone number.
- Add search and filtering features.
- Add reports and charts.
- Export records to Excel or PDF.
- Add an operation audit log.
- Add backup and restore features.
- Add tests using JUnit.
- Separate the user interface from the business logic using MVC.
- Support multiple organizations and warehouses.

## 🎯 Academic Objective

The project demonstrates the application of the following concepts:
- Designing classes and defining relationships between them.
- Inheritance and abstraction.
- Encapsulation and polymorphism.
- Developing a user interface using JavaFX.
- Managing collections and priority-based sorting.
- Input validation.
- File handling.
- Password protection.
- Applying date-based eligibility rules.
- Inventory management.

## ⚠️ Notice

> [!WARNING]
> All accounts, passwords, quantities, and sample data included in this project are intended for testing and academic demonstration purposes only.
