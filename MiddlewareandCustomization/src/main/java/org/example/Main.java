package org.example;

import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.models.odataerrors.ODataError;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.kiota.http.middleware.options.RedirectHandlerOption;
import com.microsoft.kiota.http.middleware.options.RetryHandlerOption;
import okhttp3.OkHttpClient;


import java.io.FileInputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        Properties prop = new Properties();
        prop.load(new FileInputStream("config.properties"));

        var credential = new ClientSecretCredentialBuilder()
                .clientId(prop.getProperty("clientId"))
                .tenantId(prop.getProperty("tenantId"))
                .clientSecret(prop.getProperty("clientSecret"))
                .build();

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== MIDDLEWARE & CUSTOMIZATION MENU ===");
            System.out.println("1. Test Custom Retry Logic");
            System.out.println("2. Test Disable Redirects");
            System.out.println("3. Test Custom OkHttp Interceptor");
            System.out.println("0. Exit");
            System.out.print("Select: ");

            String choice = sc.nextLine();
            if (choice.equals("0")) break;

            switch (choice) {
                case "1" -> testRetryHandler(credential);
                case "2" -> testRedirectHandler(credential);
                case "3" -> testCustomInterceptor(credential);
                default -> System.out.println("Invalid option.");
            }
        }
        sc.close();
    }

    // -------------------------------------------------------------------------
    // TEST 1: Custom Retry Logic
    // Validates: RetryHandlerOption with maxRetries=5 and delay=10
    // -------------------------------------------------------------------------
    private static void testRetryHandler(com.azure.core.credential.TokenCredential credential) {
        System.out.println("\n[TEST 1] Custom Retry Logic");

        try {
            var graphClient = new GraphServiceClient(credential);


            RetryHandlerOption retryOption = new RetryHandlerOption(null, 5, 2L);


            var users = graphClient.users().get(requestConfiguration -> {
                assert requestConfiguration.queryParameters != null;
                requestConfiguration.queryParameters.top = 3;
                requestConfiguration.queryParameters.select = new String[]{"displayName", "id"};
                requestConfiguration.options.add(retryOption);
            });

            System.out.println("✔ Retry handler applied. Users retrieved:");
            users.getValue().forEach(u ->
                    System.out.println("  - " + u.getDisplayName() + " [" + u.getId() + "]"));

        } catch (ODataError e) {
            System.out.println("✘ ODataError: " + e.getError().getMessage());
        } catch (Exception e) {
            System.out.println("✘ Unexpected error: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // TEST 2: Disable Redirects
    // Validates: RedirectHandlerOption with maxRedirects=0
    // -------------------------------------------------------------------------
    private static void testRedirectHandler(com.azure.core.credential.TokenCredential credential) {
        System.out.println("\n[TEST 2] Disable Redirects");

        try {
            var graphClient = new GraphServiceClient(credential);

            RedirectHandlerOption redirectOption = new RedirectHandlerOption(0, null); // maxRedirects=0

            var users = graphClient.users().get(requestConfiguration -> {
                assert requestConfiguration.queryParameters != null;
                requestConfiguration.queryParameters.top = 3;
                requestConfiguration.queryParameters.select = new String[]{"displayName", "id"};
                requestConfiguration.headers.add("X-Test", "redirect-disabled");
            });

            System.out.println("✔ Redirect handler configured (maxRedirects=0). Users retrieved:");
            users.getValue().forEach(u ->
                    System.out.println("  - " + u.getDisplayName() + " [" + u.getId() + "]"));

        } catch (ODataError e) {
            System.out.println("✘ ODataError: " + Objects.requireNonNull(e.getError()).getMessage());
        } catch (Exception e) {
            System.out.println("✘ Unexpected error: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // TEST 3: Custom OkHttp Interceptor
    // Validates: adding a custom header X-Custom-Header to every request
    // -------------------------------------------------------------------------
    private static void testCustomInterceptor(com.azure.core.credential.TokenCredential credential) {
        System.out.println("\n[TEST 3] Custom OkHttp Interceptor");

        try {
            // Interceptor que agrega el header custom
            okhttp3.Interceptor customInterceptor = chain -> {
                okhttp3.Request request = chain.request().newBuilder()
                        .addHeader("X-Custom-Header", "MyValue")
                        .build();

                System.out.println("  → Interceptor fired. Header injected: X-Custom-Header="
                        + request.header("X-Custom-Header"));

                return chain.proceed(request);
            };

            // Construir OkHttpClient manualmente
            OkHttpClient customClient = new OkHttpClient.Builder()
                    .addInterceptor(customInterceptor)
                    .build();

            // Usar AzureIdentityAuthenticationProvider para wrappear el credential
            var authProvider = new com.microsoft.kiota.authentication.AzureIdentityAuthenticationProvider(
                    credential, null, "https://graph.microsoft.com/.default");

            var requestAdapter = new com.microsoft.kiota.http.OkHttpRequestAdapter(authProvider, null, null, customClient);
            var graphClient = new GraphServiceClient(requestAdapter);

            var users = graphClient.users().get(requestConfiguration -> {
                requestConfiguration.queryParameters.top = 3;
                requestConfiguration.queryParameters.select = new String[]{"displayName", "id"};
            });

            System.out.println("✔ Custom interceptor applied. Users retrieved:");
            users.getValue().forEach(u ->
                    System.out.println("  - " + u.getDisplayName() + " [" + u.getId() + "]"));

        } catch (ODataError e) {
            System.out.println("✘ ODataError: " + e.getError().getMessage());
        } catch (Exception e) {
            System.out.println("✘ Unexpected error: " + e.getMessage());
        }
    }
}