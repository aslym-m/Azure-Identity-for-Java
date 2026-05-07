# Microsoft Graph CRUD Lab - Java

A hands-on lab for practicing **Microsoft Graph API** user management operations using Java and the **Client Credentials Flow**.

## 🚀 Purpose
The goal is to implement and test basic CRUD operations against Microsoft Entra ID users via the Graph API, focusing on enterprise-level integration best practices.

## 🛠️ Features

### User Management via Microsoft Graph
A console-based menu application that performs full CRUD operations on Azure AD users.

**Operations included:**
- **List Users** — Retrieves the top 10 users with display name, ID, and UPN.
- **Create User** — Creates a new user with a default password and enabled account.
- **Update User Title** — Updates the job title of an existing user by ID.
- **Delete User** — Permanently deletes a user by ID.

### Authentication — Client Credentials Flow
Server-to-server communication with no user presence required.

**Requirements:**
- App Registration configured with **Application Permissions** (not Delegated).
- `User.Read.All`, `User.ReadWrite.All` permissions granted with **admin consent**.
- A valid **Client Secret** configured in the App Registration.
- Java app uses `ClientSecretCredential` to authenticate the application itself.

## ⚠️ Security Note
This project **DOES NOT** contain real credentials. Never upload files containing secrets (e.g., `config.properties`) to GitHub. A `.gitignore` is configured to ensure these remain local.

## ⚙️ Local Configuration
1. Clone the repository:
   ```bash
   git clone <your-repo-url>
   ```
2. Create a `config.properties` file inside the `stage-3-auth/` folder:
   ```properties
   clientId=YOUR_CLIENT_ID
   tenantId=YOUR_TENANT_ID
   clientSecret=YOUR_CLIENT_SECRET
   ```
3. Confirm `config.properties` is listed in your `.gitignore`.

## ▶️ Running the App
Run `Main.java` from IntelliJ. The working directory must be set to the `stage-3-auth/` folder so `config.properties` is resolved correctly.

```
=== GRAPH CRUD LAB MENU ===
1. List Users
2. Create User
3. Update User Title
4. Delete User
0. Exit
```

## 📚 Resources
- [Microsoft Graph Java SDK Documentation](https://learn.microsoft.com/en-us/graph/sdks/create-requests?tabs=java)
- [Azure Identity Library for Java](https://learn.microsoft.com/en-us/java/api/overview/azure/identity-readme?view=azure-java-stable)
- [Graph API User Operations](https://learn.microsoft.com/en-us/graph/api/resources/user?view=graph-rest-1.0)