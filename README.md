# RandFacts

<p align ="center">
    <img width="475" height="155" alt="Image" src="src/main/resources/com/randfacts/images/project-logo-asset.png" />
    </p>


1st year Computer Programming II finals project an AI based random facts generator built on java 


## Tech Stack


[![Java 17 FRONTEND](https://img.shields.io/badge/Java_17_LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/17/)
[![JavaFX Controls FRONTEND](https://img.shields.io/badge/JavaFX_Controls-FF8000?style=for-the-badge&logo=java&logoColor=white)](https://openjfx.io/)
[![JavaFX FXML FRONTEND](https://img.shields.io/badge/JavaFX_FXML-FF8000?style=for-the-badge&logo=java&logoColor=white)](https://openjfx.io/)
[![JavaFX Media FRONTEND](https://img.shields.io/badge/JavaFX_Media-FF8000?style=for-the-badge&logo=java&logoColor=white)](https://openjfx.io/)
[![CSS3 FRONTEND](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white)](https://developer.mozilla.org/en-US/docs/Web/CSS)

[![API: Gemini BACKEND](https://img.shields.io/badge/Google_Gemini-8E75B2?style=for-the-badge&logo=googlegemini&logoColor=white)](https://ai.google.dev/)
[![Lib: Gson BACKEND](https://img.shields.io/badge/Gson-000000?style=for-the-badge&logo=json&logoColor=white)](https://github.com/google/gson)
[![Lib: dotenv-java BACKEND](https://img.shields.io/badge/dotenv--java-ECD53F?style=for-the-badge&logo=dotenv&logoColor=black)](https://github.com/cdimascio/dotenv-java)

[![DB: SQLite DATABASE](https://img.shields.io/badge/SQLite_3-003B57?style=for-the-badge&logo=sqlite&logoColor=white)](https://www.sqlite.org/)
[![Driver: sqlite-jdbc DATABASE](https://img.shields.io/badge/sqlite--jdbc-003B57?style=for-the-badge&logo=java&logoColor=white)](https://github.com/xerial/sqlite-jdbc)

[![Build: Maven PROJECT BUILDER](https://img.shields.io/badge/Apache_Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)

[![Git VERSION CONTROL](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)](https://git-scm.com/)
[![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/)
[![Figma](https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white)](https://www.figma.com/)

[![Neovim](https://img.shields.io/badge/Neovim-57A143?style=for-the-badge&logo=neovim&logoColor=white)](https://neovim.io/)

## Project Structure

```
RandFacts/
├── pom.xml                                          # Maven project descriptor
├── .env                                             # Runtime API key (gitignored)
├── .env.example                                     # Template for .env setup for potential users
├── .gitignore                                       # VCS exclusion rules
├── database/
│   └── randfacts.db                                 # SQLite database (auto created during compile) 
└── src/
    └── main/
        ├── java/
        │   ├── module-info.java                     # JPMS module descriptor
        │   └── com/randfacts/
        │       ├── MainApp.java                     # Application entry point, font loader, host services
        │       ├── MainController.java              # Navigation shell, background video, page transitions
        │       ├── HomepageController.java           # Fact generation UI, category selection, save action
        │       ├── HistoryController.java            # History list view, hover preview, delete with confirmation
        │       ├── SavedFactsController.java         # Saved facts list view, hover preview, delete with confirmation
        │       ├── DashboardController.java          # BarChart analytics, contextual slicing toggle
        │       ├── AboutUsController.java            # Hyperlink handler for external browser navigation
        │       ├── FactDetailController.java         # Interface contract for extended detail views
        │       ├── ExtendedHistoryPageController.java        # Read-only detail view for history items
        │       ├── ExtendedSavedFactsPageController.java     # Editable detail view with unsaved changes guard
        │       ├── FactService.java                  # Singleton service: AI chain, JDBC, in-memory state
        │       ├── Fact.java                         # Data model (id, title, content, date)
        │       └── AITest.java                       # Standalone Gemini API integration test
        └── resources/
            └── com/randfacts/
                ├── main.css                          # Global stylesheet (glassmorphism, custom scrollbars)
                ├── MainView.fxml                     # Root shell: video background, nav dock, content area
                ├── Homepage.fxml                     # Generation page layout
                ├── HistoryPage.fxml                  # History list with delete overlay
                ├── SavedFactsPage.fxml               # Saved facts list with delete overlay
                ├── DashboardPage.fxml                # BarChart analytics layout
                ├── AboutUsPage.fxml                  # Repository links and contributor credits
                ├── ExtendedHistoryPage.fxml           # Read-only fact detail layout
                ├── ExtendedSavedFactsPage.fxml        # Editable fact detail with exit guard overlay
                ├── fonts/
                │   ├── PixelifySans-Regular.ttf
                │   ├── PixelifySans-SemiBold.ttf
                │   ├── PixelifySans-Bold.ttf
                │   ├── PixelifySans-Medium.ttf
                │   ├── PixelifySans-VariableFont_wght.ttf
                │   ├── FiraCode-Regular.ttf
                │   ├── FiraCode-Bold.ttf
                │   ├── FiraCode-SemiBold.ttf
                │   ├── FiraCode-Medium.ttf
                │   ├── FiraCode-Light.ttf
                │   └── FiraCode-VariableFont_wght.ttf
                └── images/
                    ├── project-logo.png
                    ├── background-image.png
                    ├── background-lean.mp4
                    ├── bin-open.png
                    ├── bin-close.png
                    └── github.png
```

## Installing Tools and Dependencies 

to build and run this project, you need **Git**, **Java 17 (or newer LTS)**, and **Apache Maven**

### Windows 10/11

recommended approach for this OS is to use a package manager for a clean installation

#### Option A: Winget (Built-in on modern Windows)
install tools and dependencies on CMD or powershell

```powershell
winget install Git.Git -e --accept-source-agreements
winget install Microsoft.OpenJDK.17
winget install Apache.Maven
```

#### Option B: Scoop

install scoop first since its not pre-installed on windows OS
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser  

irm https://get.scoop.sh | iex   
```
install tools and dependencies on CMD or powershell
```powershell
scoop install git
scoop bucket add java
scoop install temurin17-jdk
scoop install maven
```

#### Option C: Chocolatey

open powerShell as Administrator and run this command to install chocolatey on your system:

```powershell
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))   
```
install tools and dependencies
```powershell
choco install git -y
choco install openjdk17 -y
choco install maven -y
```


#### Arch-based Distros

```bash
sudo pacman -S git jdk17-openjdk maven
```

#### Debian/Ubuntu-based Distros

```bash
sudo apt update && sudo apt install git openjdk-17-jdk maven -y
```

##### Fedora based distros

```bash
sudo dnf install git java-17-openjdk java-17-openjdk-devel maven -y
```

## Configuration

##### prerequisites:
- you must have a gemini api key if you haven't gotten one yet get them from **[here](https://aistudio.google.com/api-keys)**  
  

> [!IMPORTANT]  
>never share to anyone the copied API key

copy the environment template or rename it into `.env` and populate it with your Gemini API key:

```bash
cp .env.example .env
```

Edit `.env` with your key:

```
GEMINI_API_KEY=paste-your-gemini-api-key-here
```

## Build and Run

Compile the project:

```bash
mvn compile
```

Run via the JavaFX Maven plugin:

```bash
mvn javafx:run
```


---

>[!NOTE]
>for windows users the recommended display resolution is ***100%*** 
>to avoid squeezed and distorted application interface

# Project Preview


<p align="center">
    <img width="490" height="276" alt="Image" src="src/main/resources/com/randfacts/images/randfacts-project-preview.gif" />
    </p>

