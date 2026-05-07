# Middleware & Customization

This module explores how to customize the **Microsoft Graph Java SDK's internal pipeline** — the layer between your code and the actual HTTP call. All three examples validate that you can modify SDK behavior without touching its source code.

---

## 🔍 What's Covered

### 1. Custom Retry Logic
By default, the SDK retries failed requests (e.g., 429 Too Many Requests) up to 3 times. This test overrides that to 5 retries with a 2-second delay between attempts.

The retry never triggers under normal conditions — the point is confirming the configuration is applied correctly without breaking the call.

```java
RetryHandlerOption retryOption = new RetryHandlerOption(null, 5, 2L);

graphClient.users().get(requestConfiguration -> {
    requestConfiguration.options.add(retryOption);
});
```

---

### 2. Disable Redirects
When Graph returns a 302 redirect, the SDK follows it automatically. Setting `maxRedirects=0` disables that behavior — useful for security audits or when you want to handle redirects manually.

```java
RedirectHandlerOption redirectOption = new RedirectHandlerOption(0, null);

graphClient.users().get(requestConfiguration -> {
    requestConfiguration.options.add(redirectOption);
});
```

---

### 3. Custom OkHttp Interceptor
The SDK uses OkHttp under the hood for all HTTP calls. An interceptor runs on every request before it leaves the application — think of it as a global filter.

This example injects a custom header into every request automatically, without modifying individual calls. In production this pattern is used for correlation IDs, audit headers, or additional tokens.

```java
OkHttpClient customClient = new OkHttpClient.Builder()
    .addInterceptor(chain -> {
        Request request = chain.request().newBuilder()
            .addHeader("X-Custom-Header", "MyValue")
            .build();
        return chain.proceed(request);
    })
    .build();

var requestAdapter = new OkHttpRequestAdapter(authProvider, null, null, customClient);
var graphClient = new GraphServiceClient(requestAdapter);
```

---

## ⚙️ Running Locally

1. Add a `config.properties` file in this module's root folder:
   ```properties
   clientId=YOUR_CLIENT_ID
   tenantId=YOUR_TENANT_ID
   clientSecret=YOUR_CLIENT_SECRET
   ```

2. Set the **Working Directory** in IntelliJ to this module's folder:
   ```
   C:\Users\aslym\ProjectsJava\GraphFlows\MiddlewareandCustomization
   ```

3. Run `Main.java` and select an option from the menu:
   ```
   === MIDDLEWARE & CUSTOMIZATION MENU ===
   1. Test Custom Retry Logic
   2. Test Disable Redirects
   3. Test Custom OkHttp Interceptor
   0. Exit
   ```

---

## 🗒️ Notes

- **Per-Request vs Global config**: Using `requestConfiguration.options` affects only that specific call. Passing a custom `OkHttpClient` to the `GraphServiceClient` affects every call globally.
- **Interceptor order matters**: Authentication happens early in the pipeline. Custom interceptors run after the `AuthenticationHandler`, so the token is already attached when your interceptor fires.
- **Avoid blocking** `chain.proceed()`: If your interceptor never calls it, the SDK will hang indefinitely.

---

## 📚 References

- [Customize the Graph Client (Java)](https://learn.microsoft.com/en-us/graph/sdks/customize-client?tabs=java)
- [OkHttp Interceptors](https://square.github.io/okhttp/features/interceptors/)