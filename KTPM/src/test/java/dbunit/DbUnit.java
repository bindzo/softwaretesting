package dbunit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.FileInputStream;
import java.sql.*;

import org.dbunit.DBTestCase;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;


public class DbUnit extends DBTestCase {
    private  Connection conn = null;
    private  Statement stmt = null;
    public DbUnit(String name) throws SQLException {
        super(name);
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "com.mysql.jdbc.Driver");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, "jdbc:mysql://localhost:3306/shop");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, "root");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, "");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/shop", "root", "");
    }
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(new FileInputStream("product.xml"));
    }

    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.REFRESH;
    }

    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.DELETE_ALL;
    }
    //    Các trường hợp test
    @Test
    public void testByPrice() throws SQLException {
        int id = 1;
        int actual = 0;
        int expect = 1566000; // Giá giả định mong muốn
        stmt = conn.createStatement();
        String sql = "SELECT price FROM product where id="+ Integer.toString(id);
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next()){
            actual = rs.getInt("price");
        }
        rs.close();
        System.out.println("Expect : "+Integer.toString(expect)+" , Actual : "+ Integer.toString(actual));
        assertThat(actual, is(expect));
        System.out.println("Hoàn thành test price");
    }

    @Test
    public void testByAmount() throws SQLException {
        int id = 1;
        int actual = 0;
        int expect = 32; // Số lượng giả định mong muốn
        stmt = conn.createStatement();
        String sql = "SELECT amount FROM product where id="+ Integer.toString(id);
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next()){
            actual = rs.getInt("amount");
        }
        rs.close();
        System.out.println("Expect : "+Integer.toString(expect)+" , Actual : "+ Integer.toString(actual));
        assertThat(actual, is(expect));
        System.out.println("Hoàn thành test amount");
    }
    @Test
    public void testByName() throws SQLException {
        int id = 1;
        String actual = null;
        String expect = "laptop"; // tên giả định mong muốn
        stmt = conn.createStatement();
        String sql = "SELECT name FROM product where id="+ Integer.toString(id);
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next()){
            actual = rs.getString("name");
        }
        rs.close();

        System.out.println("Expect : "+expect+" , Actual : "+ actual);
        assertThat(actual, is(expect));
        System.out.println("Hoàn thành test name");
    }

    @Test
    public void testPerfomance() throws SQLException {
        int id = 1;
        String actual = null;
        String expect = "laptop"; // tên giả định mong muốn
        stmt = conn.createStatement();
        String sql = "SELECT name FROM product where id="+ Integer.toString(id);
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next()){
            actual = rs.getString("name");
        }
        rs.close();

        System.out.println("Expect : "+expect+" , Actual : "+ actual);
        assertThat(actual, is(expect));
        System.out.println("Hoàn thành test name");
    }


}
