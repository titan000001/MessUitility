# Smart Hostel & Mess Management System

A robust, Java-based Desktop application designed to completely automate and simplify the complexities of managing a shared hostel or mess. Developed using modern Object-Oriented Principles, this application features dynamic month-end settlement calculations, multi-tenancy workspace isolation, and a clean modern UI.

## Key Features

*   **Multi-Tenant Workspaces (Mess/Month Isolation):** Create completely separate, isolated databases for different months (e.g., `Jan_2026`, `Feb_2026`) or different hostels. Data from one workspace will never mix with another.
*   **Role-Based Access Control (RBAC):**
    *   **Manager Dashboard:** Full control to add residents, log daily meals, input grocery/utility expenses, and generate final bills.
    *   **Resident Dashboard:** A personalized view where residents can log in with their generated ID to see their specific dues and meal history.
*   **Smart Settlement Engine ("The Brain"):**
    *   Automatically calculates the **Total Grocery Cost** and **Total Meals Eaten** across all residents.
    *   Dynamically generates the **Cost Per Meal**.
    *   Splits fixed **Utility Bills** (like Wi-Fi, Electricity) evenly among all active residents.
    *   Automatically credits any resident who paid for a shared expense out of their own pocket.
*   **Persistent SQLite Database:** Zero configuration required. The app automatically creates and manages local `.db` files using JDBC.
*   **Modern Desktop UI:** Built using `FlatLaf` (Flat Dark Look and Feel) for a sleek, responsive, and native Windows experience.
*   **Native Windows Executable:** Bundled via `jpackage` into a standalone `.exe` so end-users don't need to touch the terminal or install Java.

## Prerequisites for Development
- **Java JDK 14+** (Currently using JDK 21)
- Windows PowerShell

## How to Build & Run
1. Run the `setup_libs.ps1` script to download necessary dependencies (FlatLaf, SQLite JDBC, SLF4J).
2. Run the `build.ps1` script to compile the Java code, package it into a JAR, and generate the native Windows executable.
3. Open `out/MessUtility/MessUtility.exe` to launch the application!

## Default Credentials
When a new Workspace is created, the system automatically generates a default manager account:
- **ID:** `admin`
- **Password:** `admin123`
