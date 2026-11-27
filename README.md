# SparkX – Decentralized Energy Trading System

SparkX is a Java-based desktop application that simulates a decentralized peer-to-peer energy trading marketplace. It allows energy sellers to publish available energy offers and buyers to browse the marketplace, purchase energy, and track their transactions.

The application also includes an optional AI-powered advisor using the Groq API to help buyers and sellers make informed trading decisions.

## Features

### Seller
- Register and log in as a seller.
- Manage seller profile.
- Record generated and available energy.
- Publish energy offers with custom quantities and prices.
- View published offers and transaction history.
- Get AI-powered recommendations for energy quantity and pricing.

### Buyer
- Register and log in as a buyer.
- Manage buyer profile and purchasing preferences.
- Browse available energy offers.
- Purchase energy from sellers.
- View transaction history.
- Get AI-powered purchasing recommendations.

### AI Energy Advisor
- Provides seller recommendations for offer quantity and pricing.
- Helps buyers identify suitable energy offers.
- Validates recommendations before presenting calculated results.
- Uses Groq API and requires an individual API key.
- AI functionality is optional; the main application can run without it.

## Technologies Used

- **Java 25** – Core application development.
- **Java Swing** – Graphical user interface.
- **Groq API** – AI-powered recommendations.
- **File Handling** – Local application data persistence.
- **Eclipse IDE** – Development environment.
- **Git & GitHub** – Version control and project hosting.

## Requirements

- Java Development Kit (JDK) 25 or a compatible Java 25 runtime.
- Windows, Linux, or macOS.
- Internet connection for AI functionality only.

## Run the Application Without Eclipse

1. Open the repository on GitHub.
2. Navigate to the `dist` folder.
3. Download `SparkX.jar`.
4. Make sure Java 25 is installed.
5. Open a terminal in the folder containing the downloaded JAR.
6. Execute:

```bash
java -jar SparkX.jar
```

The application should launch without requiring Eclipse.

## Run the Project Through Eclipse

1. Clone or download this repository.
2. Open Eclipse IDE.
3. Select **File → Import → Existing Projects into Workspace**.
4. Select the downloaded project folder.
5. Finish the import.
6. Locate `SparkXApp.java` inside `src/sparkX`.
7. Right-click the file and select **Run As → Java Application**.

## Optional: Configure the Groq AI Advisor

The AI advisor requires your own Groq API key.

1. Obtain an API key from [Groq Console](https://console.groq.com/).
2. Locate the `.env.example` file in the project root.
3. Create a copy named `.env`.
4. Replace the placeholder with your API key:

```env
GROQ_API_KEY=your_groq_api_key_here
```

5. Save the file and launch the application.

**Important:**
- Never upload your `.env` file or API key to GitHub.
- Each user must configure their own API key to use the AI advisor.
- The application can still be used without configuring the AI advisor.

## Project Structure

```text
Final_Project/
│
├── src/
│   └── sparkX/
│       ├── ai/          # AI integration
│       ├── data/        # Data management
│       ├── gui/         # Application screens
│       ├── model/       # Data models
│       ├── service/     # Application services
│       └── SparkXApp.java
│
├── dist/
│   └── SparkX.jar       # Runnable application
│
├── .env.example         # Example API configuration
├── .gitignore
└── README.md
```

## Data Storage

SparkX stores application data locally in the `sparkx-data/` directory. User data is maintained locally rather than shared between different installations.

## Repository

**GitHub:** https://github.com/Mowahid-Safdar/SparkX-Energy-Trading-System

---

Developed as an academic project to demonstrate a peer-to-peer energy trading marketplace using Java.