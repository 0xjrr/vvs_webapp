package vvs_webapp;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.operation.Operation;

import webapp.DbSetupConfig;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbSetupTest {

    private static final String DB_URL = "jdbc:hsqldb:file:src/main/resources/data/hsqldb/cssdb";
    private static final String USER = "SA";
    private static final String PASSWORD = "";

    private Connection getConnection() throws Exception {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }

    @Test
    public void testAddCustomerWithExistingVat() throws Exception {
        Operation operation = Operations.sequenceOf(
            Operations.deleteAllFrom("SALEDELIVERY", "ADDRESS", "SALE", "CUSTOMER"),
            Operations.insertInto("CUSTOMER")
                .columns("DESIGNATION", "PHONENUMBER", "VATNUMBER")
                .values("JOSE FARIA", 914276732, 197672337)
                .build()
        );

        DbSetup dbSetup = new DbSetup(DbSetupConfig.getDestination(), operation);
        dbSetup.launch();

        try (Connection connection = getConnection()) {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("INSERT INTO CUSTOMER (DESIGNATION, PHONENUMBER, VATNUMBER) VALUES ('MARIA SILVA', 934567890, 197672337)");

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM CUSTOMER WHERE VATNUMBER = 197672337");
            rs.next();
            int count = rs.getInt(1);
            assertEquals(1, count);  // Ensure there is only one customer with the VAT number 197672337
        } catch (Exception e) {
        	System.out.println(e.getMessage().toLowerCase());
            assertEquals("integrity constraint violation: unique constraint or index violation; sys_ct_10095 table: customer", e.getMessage().toLowerCase());
        }
    }

    @Test
    public void testUpdateCustomerContact() throws Exception {
        Operation operation = Operations.sequenceOf(
            Operations.deleteAllFrom("SALEDELIVERY", "ADDRESS", "SALE", "CUSTOMER"),
            Operations.insertInto("CUSTOMER")
                .columns("DESIGNATION", "PHONENUMBER", "VATNUMBER")
                .values("JOSE FARIA", 914276732, 197672337)
                .build()
        );

        DbSetup dbSetup = new DbSetup(DbSetupConfig.getDestination(), operation);
        dbSetup.launch();

        try (Connection connection = getConnection()) {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("UPDATE CUSTOMER SET PHONENUMBER = 987654321 WHERE VATNUMBER = 197672337");

            ResultSet rs = stmt.executeQuery("SELECT PHONENUMBER FROM CUSTOMER WHERE VATNUMBER = 197672337");
            rs.next();
            int phoneNumber = rs.getInt(1);
            assertEquals(987654321, phoneNumber);
        }
    }

    @Test
    public void testDeleteAllCustomers() throws Exception {
        Operation operation = Operations.sequenceOf(
            Operations.deleteAllFrom("SALEDELIVERY", "ADDRESS", "SALE", "CUSTOMER"),
            Operations.insertInto("CUSTOMER")
                .columns("DESIGNATION", "PHONENUMBER", "VATNUMBER")
                .values("JOSE FARIA", 914276732, 197672337)
                .values("LUIS SANTOS", 964294317, 168027852)
                .build()
        );

        DbSetup dbSetup = new DbSetup(DbSetupConfig.getDestination(), operation);
        dbSetup.launch();

        try (Connection connection = getConnection()) {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("DELETE FROM CUSTOMER");
            
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM CUSTOMER");
            rs.next();
            int count = rs.getInt(1);
            assertEquals(0, count);
        }
    }

    @Test
    public void testDeleteAndAddCustomer() throws Exception {
        Operation operation = Operations.sequenceOf(
            Operations.deleteAllFrom("SALEDELIVERY", "ADDRESS", "SALE", "CUSTOMER"),
            Operations.insertInto("CUSTOMER")
                .columns("DESIGNATION", "PHONENUMBER", "VATNUMBER")
                .values("JOSE FARIA", 914276732, 197672337)
                .build()
        );

        DbSetup dbSetup = new DbSetup(DbSetupConfig.getDestination(), operation);
        dbSetup.launch();

        try (Connection connection = getConnection()) {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("DELETE FROM CUSTOMER WHERE VATNUMBER = 197672337");
            stmt.executeUpdate("INSERT INTO CUSTOMER (DESIGNATION, PHONENUMBER, VATNUMBER) VALUES ('JOSE FARIA', 914276732, 197672337)");

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM CUSTOMER WHERE VATNUMBER = 197672337");
            rs.next();
            int count = rs.getInt(1);
            assertEquals(1, count);
        }
    }

    @Test
    public void testDeleteCustomerSales() throws Exception {
        Operation operation = Operations.sequenceOf(
            Operations.deleteAllFrom("SALEDELIVERY", "ADDRESS", "SALE", "CUSTOMER"),
            Operations.insertInto("CUSTOMER")
                .columns("DESIGNATION", "PHONENUMBER", "VATNUMBER")
                .values("JOSE FARIA", 914276732, 197672337)
                .build(),
            Operations.insertInto("SALE")
                .columns("DATE", "TOTAL", "STATUS", "CUSTOMER_VAT")
                .values("2024-05-20", 100.0, "O", 197672337)
                .build()
        );

        DbSetup dbSetup = new DbSetup(DbSetupConfig.getDestination(), operation);
        dbSetup.launch();

        try (Connection connection = getConnection()) {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("DELETE FROM CUSTOMER WHERE VATNUMBER = 197672337");
            
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM SALE WHERE CUSTOMER_VAT = 197672337");
            rs.next();
            int count = rs.getInt(1);
            assertEquals(0, count);
        }
    }

    @Test
    public void testAddNewSale() throws Exception {
        Operation operation = Operations.sequenceOf(
            Operations.deleteAllFrom("SALEDELIVERY", "ADDRESS", "SALE", "CUSTOMER"),
            Operations.insertInto("CUSTOMER")
                .columns("DESIGNATION", "PHONENUMBER", "VATNUMBER")
                .values("JOSE FARIA", 914276732, 197672337)
                .build()
        );

        DbSetup dbSetup = new DbSetup(DbSetupConfig.getDestination(), operation);
        dbSetup.launch();

        try (Connection connection = getConnection()) {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("INSERT INTO SALE (DATE, TOTAL, STATUS, CUSTOMER_VAT) VALUES ('2024-05-20', 100.0, 'O', 197672337)");

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM SALE");
            rs.next();
            int count = rs.getInt(1);
            assertEquals(1, count);  // Ensure there is one sale in the table
        }
    }

    // Additional tests for sales and sale deliveries
    @Test
    public void testAddSaleDelivery() throws Exception {
        Operation operation = Operations.sequenceOf(
            Operations.deleteAllFrom("SALEDELIVERY", "ADDRESS", "SALE", "CUSTOMER"),
            Operations.insertInto("CUSTOMER")
                .columns("DESIGNATION", "PHONENUMBER", "VATNUMBER")
                .values("JOSE FARIA", 914276732, 197672337)
                .build(),
            Operations.insertInto("SALE")
                .columns("DATE", "TOTAL", "STATUS", "CUSTOMER_VAT")
                .values("2024-05-20", 100.0, "O", 197672337)
                .build(),
            Operations.insertInto("ADDRESS")
                .columns("ADDRESS", "CUSTOMER_VAT")
                .values("123 Street", 197672337)
                .build(),
            Operations.insertInto("SALEDELIVERY")
                .columns("SALE_ID", "CUSTOMER_VAT", "ADDRESS_ID")
                .values(1, 197672337, 1)
                .build()
        );

        DbSetup dbSetup = new DbSetup(DbSetupConfig.getDestination(), operation);
        dbSetup.launch();

        try (Connection connection = getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM SALEDELIVERY");
            rs.next();
            int count = rs.getInt(1);
            assertEquals(1, count);  // Ensure there is one sale delivery in the table
        }
    }

    @Test
    public void testDeleteSaleDelivery() throws Exception {
        Operation operation = Operations.sequenceOf(
            Operations.deleteAllFrom("SALEDELIVERY", "ADDRESS", "SALE", "CUSTOMER"),
            Operations.insertInto("CUSTOMER")
                .columns("DESIGNATION", "PHONENUMBER", "VATNUMBER")
                .values("JOSE FARIA", 914276732, 197672337)
                .build(),
            Operations.insertInto("SALE")
                .columns("DATE", "TOTAL", "STATUS", "CUSTOMER_VAT")
                .values("2024-05-20", 100.0, "O", 197672337)
                .build(),
            Operations.insertInto("ADDRESS")
                .columns("ADDRESS", "CUSTOMER_VAT")
                .values("123 Street", 197672337)
                .build(),
            Operations.insertInto("SALEDELIVERY")
                .columns("SALE_ID", "CUSTOMER_VAT", "ADDRESS_ID")
                .values(1, 197672337, 1)
                .build()
        );

        DbSetup dbSetup = new DbSetup(DbSetupConfig.getDestination(), operation);
        dbSetup.launch();

        try (Connection connection = getConnection()) {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("DELETE FROM SALEDELIVERY WHERE SALE_ID = 1");
            
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM SALEDELIVERY WHERE SALE_ID = 1");
            rs.next();
            int count = rs.getInt(1);
            assertEquals(0, count);  // Ensure the sale delivery was removed from the table
        }
    }
}
