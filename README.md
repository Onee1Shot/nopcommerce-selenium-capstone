"# nopcommerce-selenium-capstone"

📋 Project Overview
End-to-end automation testing framework for the nopCommerce e-commerce application using Selenium WebDriver, Java, TestNG, and Cucumber BDD. Implements Page Object Model design pattern with CI/CD integration via Jenkins, GitHub version control, and JIRA for test management.

🛠️ Technology Stack
Programming Language: Java 17

Automation Tool: Selenium WebDriver 4.x

Testing Framework: TestNG

BDD Framework: Cucumber

Build Tool: Maven

Reporting: ExtentReports

CI/CD: Jenkins

Version Control: Git/GitHub

Issue Tracking: JIRA

Framework Architecture :

nopcommerce-selenium-framework/
├── src/main/java/
│   ├── pages/           # Page Object classes  
│   └── utils/           # Utilities (Driver, Excel, Config, Waits)  
├── src/test/java/
│   ├── testcases/       # Test scripts  
│   ├── runners/         # TestNG/Cucumber runners  
│   └── stepDefinitions/ # BDD step definitions  
├── testdata/            # Excel sheets, properties  
├── reports/             # Extent reports, logs, screenshots  
├── pom.xml              # Maven dependencies  
└── testng.xml           # TestNG suite configuration  

Key Features :

Page Object Model (POM) for maintainability

Data-driven testing using Excel/Properties files

Cross-browser execution (Chrome, Firefox)

Parallel test execution with TestNG

BDD scenarios with Cucumber Gherkin

Detailed reporting with ExtentReports and screenshots

Jenkins CI/CD integration for automated test runs

JIRA integration for requirement management and defect tracking


Test Scenarios :
User Registration and Login

Product Search and Filtering

Shopping Cart Management

Checkout Process

Order Management

User Profile Updates

Test Reports :
ExtentReports: HTML reports with step-by-step screenshots

TestNG Reports: Default TestNG HTML reports

Jenkins Integration: Automatically publish reports after each build
