import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    DBConnection dbConnection = new DBConnection();

    List<InvoiceTotal> findInvoiceTotals() {
        List<InvoiceTotal> list = new ArrayList<>();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     """
                             select invoice_id, customer_name, sum(quantity * unit_price) as total_amount from invoice_line
                                 join invoice i on i.id = invoice_line.invoice_id group by invoice_id, customer_name ;
                             """);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                InvoiceTotal invoiceTotal = new InvoiceTotal();
                invoiceTotal.setId(resultSet.getInt("invoice_id"));
                invoiceTotal.setCustomerName(resultSet.getString("customer_name"));
                invoiceTotal.setTotal(resultSet.getDouble("total_amount"));
                list.add(invoiceTotal);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    List<InvoiceTotal> findConfirmedAndPaidInvoiceTotals() {
        List<InvoiceTotal> list = new ArrayList<>();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     """
                                    select invoice_id, customer_name, sum(quantity * unit_price) as total_amount, status
                                    from invoice_line
                                        join invoice i on i.id = invoice_line.invoice_id
                                    where status = 'CONFIRMED' or status = 'PAID' group by invoice_id, customer_name, status;
                             """);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                InvoiceTotal invoiceTotal = new InvoiceTotal();
                invoiceTotal.setId(resultSet.getInt("invoice_id"));
                invoiceTotal.setCustomerName(resultSet.getString("customer_name"));
                invoiceTotal.setTotal(resultSet.getDouble("total_amount"));
                invoiceTotal.setStatus(InvoiceStatus.valueOf(resultSet.getString("status")));
                list.add(invoiceTotal);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
