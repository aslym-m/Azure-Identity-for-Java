# Azure Identity for Java - Training Lab

This repository is a hands-on lab environment designed to practice and master **Microsoft Entra ID** and **Microsoft Graph API** authentication flows using Java.

## 🚀 Purpose
The goal is to implement and test OAuth 2.0 authentication flows safely, focusing on enterprise-level integration best practices.

## 🛠️ Authentication Flows Included

### On-Behalf-Of (OBO) Flow:
Perfect for backend services acting on behalf of a user.

**Requirements:**
- Two App Registrations: One for the Frontend (Client) and one for the Backend (API).

- Backend App must Expose an API and define a specific scope.

- Backend App must explicitly pre-authorize the Frontend App client ID in the Authorized client applications section.

- Java app requires an incoming User Access Token (the assertion) to exchange for a new, valid Graph-scoped token.


### Client Credentials Flow:
Used for daemon services, background tasks, or server-to-server communication where no user presence is required or available.

**Requirements:**

- App Registration must be configured with Application Permissions (not Delegated permissions).

- Administrator consent must be granted in the Azure portal for the requested API permissions.

- A valid Client Secret or Certificate must be configured in the App Registration.

- Java app uses the ClientSecretCredential, which authenticates the application itself rather than a user.



### Device Code Flow:

Used for input-constrained devices, CLI tools, or terminals where the user cannot log in via a browser directly inside the application.

**Requirements:**

- App Registration configured as a Public Client/Native application.

- Allow public client flows enabled in the Authentication settings of the portal.

- Java app requires a challenge consumer to capture and print the authentication URL and user code so the user can sign in on a separate device.

## ⚠️ Security Note
This project **DOES NOT** contain real credentials. Never upload files containing secrets (e.g., `.env`, `config.properties`, `token.txt`) to GitHub. I have configured a `.gitignore` file to ensure these remain local.

## ⚙️ Local Configuration
1. Clone the repository: `git clone <your-repo-url>`
2. Create a `config.properties` file in the root directory:

   ```properties
   clientId=YOUR_CLIENT_ID
   tenantId=YOUR_TENANT_ID
   clientSecret=YOUR_CLIENT_SECRET 
   ```
3. Ensure config.properties is included in your .gitignore file.

## 📚 Resources

[Microsoft Graph Java SDK Documentation](https://learn.microsoft.com/en-us/graph/sdks/create-requests?tabs=java)

[Azure Identity Library for Java](https://learn.microsoft.com/en-us/java/api/overview/azure/identity-readme?view=azure-java-stable)


