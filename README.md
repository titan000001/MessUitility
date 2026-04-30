# MessUtility

A Java GUI application ready to be packaged as a native Windows executable (`.exe`).

## Prerequisites

- **Java JDK 14 or higher** is required. You currently have JDK 21 installed, which works perfectly.
- **jpackage** comes bundled with JDK 14+ and is used to package the app.

## Project Structure

- `src/com/messutility/Main.java`: The main Java GUI application (using Swing).
- `manifest.txt`: Contains the main class declaration for building the `.jar`.
- `build.ps1`: A PowerShell script to compile, build the JAR, and package it as an `.exe`.

## How to Build

Run the following command in PowerShell:

```powershell
.\build.ps1
```

If it succeeds, it will generate an `.exe` file inside `out/MessUtility/`.

## Running the Application

You can find the packaged Windows application at:
`out/MessUtility/MessUtility.exe`

Just double-click it to run your Java app natively without needing a terminal!
