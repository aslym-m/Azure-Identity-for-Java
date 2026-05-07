package org.example;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Properties;
import java.io.FileInputStream;

//Imports for Client Credentials
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;


// Imports for Device Code
import com.azure.identity.DeviceCodeCredential;
import com.azure.identity.DeviceCodeCredentialBuilder;


// Imports for OBO
import com.azure.identity.OnBehalfOfCredential;
import com.azure.identity.OnBehalfOfCredentialBuilder;

// Imports for Graph
import com.microsoft.graph.serviceclient.GraphServiceClient;

/* * NOTE: To switch authentication flows, comment out the active block
 * and uncomment the flow you wish to test.
 */

public class Main {
    public static void main(String[] args) throws IOException {

        // --- CREDENTIALS ---
        Properties prop = new Properties();
        prop.load(new FileInputStream("config.properties"));

        final String clientId = prop.getProperty("clientId");
        final String tenantId = prop.getProperty("tenantId");
        final String clientSecret = prop.getProperty("clientSecret");

        // =================================================================
        // ACTIVE: On-Behalf-Of (OBO) FLOW
        // =================================================================
        String tokenPath = "C:\\temp\\token.txt";
        String userAssertionToken = new String(Files.readAllBytes(Paths.get(tokenPath)));

        final OnBehalfOfCredential credential = new OnBehalfOfCredentialBuilder()
                .clientId(clientId)
                .tenantId(tenantId)
                .clientSecret(clientSecret)
                .userAssertion(userAssertionToken)
                .build();

        final GraphServiceClient graphClient = new GraphServiceClient(credential);

        var user = graphClient.me().get();
        System.out.println("Acting on behalf of: " + user.getDisplayName());

        // =================================================================
        // END OF ACTIVE BLOCK
        // =================================================================


        /* // =================================================================
        // BLOCK 2: DEVICE CODE FLOW (Uncomment to use)
        // =================================================================

        final DeviceCodeCredential devCredential = new DeviceCodeCredentialBuilder()
                .clientId(clientId)
                .tenantId(tenantId)
                .challengeConsumer(challenge -> System.out.println(challenge.getMessage()))
                .build();

        final GraphServiceClient graphClientDevice = new GraphServiceClient(devCredential);
        var userDevice = graphClientDevice.me().get();
        System.out.println("Authenticated as: " + userDevice.getDisplayName());
        // =================================================================
        */


        /*
        // =================================================================
        // BLOCK 3: CLIENT CREDENTIALS (Uncomment to use)
        // =================================================================

        final ClientSecretCredential ccCredential = new ClientSecretCredentialBuilder()
                .clientId(clientId)
                .tenantId(tenantId)
                .clientSecret(clientSecret)
                .build();

        final GraphServiceClient graphClientCC = new GraphServiceClient(ccCredential);

        try {
            var users = graphClientCC.users().get();
            System.out.println("Connection successful. First user: " + users.getValue().get(0).getDisplayName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // =================================================================
        */
    }
}