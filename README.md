# Microsoft Graph Java SDK — Learning Lab 🚀

A personal deep-dive into the **Microsoft Graph Java SDK (v6)** and **Microsoft Entra ID** authentication flows, built through hands-on labs.

The goal was simple: go beyond reading docs and actually build something that covers the full spectrum — from getting a first authenticated call working, all the way to production-level patterns like batching, delta queries, and diagnostics.

---

## 🔍 What's Inside

### Authentication Flows
The part most tutorials skip over. Implemented and compared three real Entra ID auth flows:

- **Client Credentials** — service-to-service, no user involved.
- **Device Code** — for CLI tools where the user can't log in via browser directly.
- **On-Behalf-Of (OBO)** — a backend API acting on behalf of a signed-in user, requires two App Registrations and token exchange.

### CRUD & Queries
Console app that performs full user management via Graph:

- `GET` with `$select`, `$filter`, `$top` via `requestConfiguration`
- `POST` new users with `PasswordProfile`
- `PATCH` without overwriting unset fields
- `DELETE` by user ID
- `PageIterator` for automatic multi-page result handling
- `ODataError` try-catch to handle API errors gracefully without crashing

### Middleware & Customization
Under the hood of the SDK pipeline:

- How the **Handler Chain** processes every outgoing request.
- Customized retry behavior for transient failures.
- Global **OkHttp Interceptor** to modify request headers across all calls.

### Advanced Scenarios
Production patterns for scale and efficiency:

- **JSON Batching** — bundle up to 20 API calls in one HTTP request.
- **Delta Queries** — sync only changed resources since the last call.
- **Large File Upload** — chunked upload sessions for files over 4MB.

### Diagnostics & Troubleshooting
The part that actually makes you useful when something breaks:

- Capturing `request-id` and `timestamp` for incident investigation.
- Enabling **Wire Logging** to see raw HTTP traffic.
- Handling **429 Throttling** and **CAE (Continuous Access Evaluation)** errors properly.

---

## ⚙️ Running Locally

1. Clone the repo:
   ```bash
   git clone <your-repo-url>
   ```

2. Navigate to the module you want to run:
   ```bash
   cd crud-queries
   ```

3. Create a `config.properties` in the module folder:
   ```properties
   clientId=YOUR_CLIENT_ID
   tenantId=YOUR_TENANT_ID
   clientSecret=YOUR_CLIENT_SECRET
   ```

4. Run `Main.java` from IntelliJ with the working directory set to the module folder.

> `config.properties` is excluded via `.gitignore` — never committed.

---

## 🛠️ Tech Stack

- Java 21
- Microsoft Graph Java SDK v6
- Azure Identity Library for Java
- Kiota (HTTP + serialization layer)
- OkHttp
- Maven

---

## 📚 References

- [Microsoft Graph Java SDK Docs](https://learn.microsoft.com/en-us/graph/sdks/create-requests?tabs=java)
- [Azure Identity Library for Java](https://learn.microsoft.com/en-us/java/api/overview/azure/identity-readme?view=azure-java-stable)
- [Graph Explorer](https://developer.microsoft.com/en-us/graph/graph-explorer)
- [Graph REST API Reference](https://learn.microsoft.com/en-us/graph/api/overview?view=graph-rest-1.0)