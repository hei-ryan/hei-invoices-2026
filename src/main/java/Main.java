import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        DBConnection dbConnection = new DBConnection();
        try (Connection conn = dbConnection.getConnection()) {
            System.out.println(conn + " : connection established");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
