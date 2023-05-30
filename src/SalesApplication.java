import java.math.BigDecimal;
import java.sql.*;
import java.util.Scanner;

public class SalesApplication {
    private String connectionString;

    public SalesApplication(String connectionString) {
        this.connectionString = connectionString;
    }

    public void run() {
        try (Connection connection = DriverManager.getConnection(connectionString)) {
            createTable(connection, "Customers",
                    "CREATE TABLE Customers (Id INT PRIMARY KEY IDENTITY, Name VARCHAR(100))");
            createTable(connection, "Articles",
                    "CREATE TABLE Articles (Id INT PRIMARY KEY IDENTITY, Name VARCHAR(100), Price DECIMAL(10, 2))");
            createTable(connection, "Invoices",
                    "CREATE TABLE Invoices (Id INT PRIMARY KEY IDENTITY, CustomerId INT, FOREIGN KEY (CustomerId) REFERENCES Customers(Id))");

            Scanner scanner = new Scanner(System.in);
            boolean exit = false;
            while (!exit) {
                System.out.println("Sales Application Menu:");
                System.out.println("1. Create Customer");
                System.out.println("2. Create Article");
                System.out.println("3. Create Invoice");
                System.out.println("4. Exit");

                System.out.print("Enter your choice: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        createCustomer(connection);
                        break;
                    case "2":
                        createArticle(connection);
                        break;
                    case "3":
                        createInvoice(connection);
                        break;
                    case "4":
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTable(Connection connection, String tableName, String createQuery) throws SQLException {
        String checkTableExistsQuery = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ?";
        try (PreparedStatement statement = connection.prepareStatement(checkTableExistsQuery)) {
            statement.setString(1, tableName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int tableCount = resultSet.getInt(1);
                    if (tableCount == 0) {
                        try (Statement createStatement = connection.createStatement()) {
                            createStatement.executeUpdate(createQuery);
                            System.out.println(tableName + " table created.");
                        }
                    }
                }
            }
        }
    }

    private void createCustomer(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter customer name: ");
        String name = scanner.nextLine();

        String insertQuery = "INSERT INTO Customers (Name) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setString(1, name);
            statement.executeUpdate();
            System.out.println("Customer created successfully.");
        }
    }

    private void createArticle(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter article name: ");
        String name = scanner.nextLine();

        System.out.print("Enter article price: ");
        BigDecimal price = scanner.nextBigDecimal();

        String insertQuery = "INSERT INTO Articles (Name, Price) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setString(1, name);
            statement.setBigDecimal(2, price);
            statement.executeUpdate();
            System.out.println("Article created successfully.");
        }
    }

    private void createInvoice(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter customer ID: ");
        int customerId = scanner.nextInt();

        System.out.print("Enter article ID: ");
        int articleId = scanner.nextInt();

        String insertQuery = "INSERT INTO Invoices (CustomerId) VALUES (?); SELECT SCOPE_IDENTITY();";
        try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setInt(1, customerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int invoiceId = resultSet.getInt(1);

                    String insertInvoiceArticleQuery = "INSERT INTO InvoiceArticles (InvoiceId, ArticleId) VALUES (?, ?)";
                    try (PreparedStatement invoiceArticleStatement = connection.prepareStatement(insertInvoiceArticleQuery)) {
                        invoiceArticleStatement.setInt(1, invoiceId);
                        invoiceArticleStatement.setInt(2, articleId);
                        invoiceArticleStatement.executeUpdate();
                    }

                    System.out.println("Invoice created successfully.");
                }
            }
        }
    }
}
