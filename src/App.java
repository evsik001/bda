
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class App {
    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("appsettings.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String connectionString = properties.getProperty("SourceDatabase");

        SalesApplication salesApp = new SalesApplication(connectionString);
        salesApp.run();
    }
}
