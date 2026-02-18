import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    List<InvoiceTotal> findInvoiceTotals() {
        DBConnection dbConnection = new DBConnection();
        List<InvoiceTotal> list = new ArrayList<>();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("""
                     select invoice.customer_name, invoice_id, sum(unit_price * quantity) as total_amount
                     from invoice_line join invoice on invoice.id = invoice_line.invoice_id
                     group by invoice_id, customer_name;
                     """);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                InvoiceTotal invoiceTotal = new InvoiceTotal();
                invoiceTotal.setId(resultSet.getInt("invoice_id"));
                invoiceTotal.setCustomerName(resultSet.getString("customer_name"));
                invoiceTotal.setTotalAmount(resultSet.getDouble("total_amount"));
                list.add(invoiceTotal);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    List<InvoiceTotal> findConfirmedAndPaidInvoiceTotals() {
        DBConnection dbConnection = new DBConnection();
        List<InvoiceTotal> list = new ArrayList<>();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("""
                     select invoice.customer_name, invoice.status, invoice_id, sum(unit_price * quantity) as total_amount
                     from invoice_line join invoice on invoice.id = invoice_line.invoice_id
                     where status='PAID' or status='CONFIRMED'
                     group by invoice_id, customer_name, invoice.status;
                     """);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                InvoiceTotal invoiceTotal = new InvoiceTotal();
                invoiceTotal.setId(resultSet.getInt("invoice_id"));
                invoiceTotal.setCustomerName(resultSet.getString("customer_name"));
                invoiceTotal.setTotalAmount(resultSet.getDouble("total_amount"));
                invoiceTotal.setStatus(InvoiceStatus.valueOf(resultSet.getString("status")));
                list.add(invoiceTotal);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
