package org.example;
import com.microsoft.graph.core.tasks.PageIterator;
import com.microsoft.graph.models.UserCollectionResponse;
import com.microsoft.graph.models.odataerrors.ODataError;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.models.PasswordProfile;
import com.microsoft.graph.models.User;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    private static GraphServiceClient graphClient;

    public static void main(String[] args) throws Exception {
        Properties prop = new Properties();
        prop.load(new FileInputStream("config.properties"));

        var credential = new ClientSecretCredentialBuilder()
                .clientId(prop.getProperty("clientId"))
                .tenantId(prop.getProperty("tenantId"))
                .clientSecret(prop.getProperty("clientSecret"))
                .build();

        graphClient = new GraphServiceClient(credential);
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== GRAPH CRUD LAB MENU ===");
            System.out.println("1. List Users");
            System.out.println("2. Create User");
            System.out.println("3. Update User Title");
            System.out.println("4. Delete User");
            System.out.println("0. Exit");
            System.out.print("Select: ");

            String choice = sc.nextLine();
            if (choice.equals("0")) break;

            switch (choice) {
                case "1" -> {
                    System.out.print("Filter by name prefix (leave empty for all): ");
                    String filter = sc.nextLine();
                    listUsers(filter.isEmpty() ? null : filter);
                }
                case "2" -> {
                    System.out.print("Name: "); String n = sc.nextLine();
                    System.out.print("UPN: "); String u = sc.nextLine();
                    createUser(u, n);
                }
                case "3" -> {
                    System.out.print("User ID: "); String id = sc.nextLine();
                    System.out.print("New Title: "); String t = sc.nextLine();
                    updateUser(id, t);
                }
                case "4" -> {
                    System.out.print("User ID to delete: "); String id = sc.nextLine();
                    deleteUser(id);
                }
                default -> System.out.println("Invalid option.");
            }
        }
        sc.close();
    }

    private static void listUsers(String nameFilter) {
        try {
            var users = graphClient.users().get(r -> {
                r.queryParameters.select = new String[]{"displayName", "id", "userPrincipalName"};
                r.queryParameters.top = 10;
                if (nameFilter != null) {
                    r.queryParameters.filter = "startsWith(displayName, '" + nameFilter + "')";
                }
            });

            // PageIterator
            PageIterator<User, UserCollectionResponse> pageIterator = new PageIterator
                    .Builder<User, UserCollectionResponse>()
                    .client(graphClient)
                    .collectionPage(users)
                    .collectionPageFactory(UserCollectionResponse::createFromDiscriminatorValue)
                    .processPageItemCallback(u -> {
                        System.out.println(u.getDisplayName() + " | " + u.getUserPrincipalName() + " [" + u.getId() + "]");
                        return true; // retorna false para detener la iteracion
                    })
                    .build();

            pageIterator.iterate();

        } catch (ODataError e) {
            System.out.println("Error listing users: " + e.getError().getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    private static void createUser(String upn, String name) {
        try {
            User u = new User();
            u.setDisplayName(name); u.setUserPrincipalName(upn); u.setMailNickname(name.split(" ")[0]);
            u.setAccountEnabled(true);
            PasswordProfile p = new PasswordProfile(); p.setPassword("P@ssw0rd123!"); u.setPasswordProfile(p);
            User created = graphClient.users().post(u);
            System.out.println("Created ID: " + created.getId());
        } catch (ODataError e) {
            System.out.println("Error creating user: " + e.getError().getMessage());
        }
    }

    private static void updateUser(String id, String title) {
        try {
            User u = new User(); u.setJobTitle(title);
            graphClient.users().byUserId(id).patch(u);
            System.out.println("Updated successfully.");
        } catch (ODataError e) {
            System.out.println("Error updating user: " + e.getError().getMessage());
        }
    }

    private static void deleteUser(String id) {
        try {
            graphClient.users().byUserId(id).delete();
            System.out.println("Deleted successfully.");
        } catch (ODataError e) {
            System.out.println("Error deleting user: " + e.getError().getMessage());
        }
    }
}