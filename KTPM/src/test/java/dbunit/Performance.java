package dbunit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.dbunit.DBTestCase;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.dbunit.Assertion;
import org.junit.jupiter.api.DynamicTest;

public class Performance extends DBTestCase {
    //clear product.xml before test
    private  Connection conn = null;
    private  Statement stmt = null;
    public Performance(String name) throws SQLException {
        super(name);
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "com.mysql.jdbc.Driver");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, "jdbc:mysql://localhost:3306/shop");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, "root");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, "");
        System.setProperty( PropertiesBasedJdbcDatabaseTester.DBUNIT_SCHEMA, "shop" );
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/shop", "root", "");
    }
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(new File("product.xml"));
    }

    protected IDataSet getExpectSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(new File("expect.xml"));
    }

    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.REFRESH;
    }

    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.DELETE_ALL;
    }
    public ArrayList<Product>  createListProduct() {
        ArrayList<Product> products = new ArrayList<Product>();
        Product product = null;
        for(int i=1;i<=100;i++){
            product = new Product(i,"John",888,2,"Hello");
            products.add(product);
        }
        return products;
    }
    public ArrayList<String>  createListTable() {
        ArrayList<String> tables = new ArrayList<String>();
        String table = null;
        for(int i=1;i<=20;i++){
            table = "table" + Integer.toString(i);
            tables.add(table);
        }
        return tables;
    }
    public void insertMultipleTable() throws SQLException {
        //Tạo mảng 100 record product
        ArrayList<String> listTable = createListTable();
        //Thêm các phần tử vào database
        String queryCreateTable;
        Statement statement = conn.createStatement();
        queryCreateTable = "CREATE TABLE {0}" +
                "(ID INT not NULL ," +
                "NAME VARCHAR(40)," +
                "PRIMARY KEY ( ID ))";
        for (String table : listTable) {
            statement.execute(MessageFormat.format(queryCreateTable, table));
        }
    }
    private void deleteMultipleTable() throws SQLException {
        ArrayList<String> listTable = createListTable();
        String sql = "DROP TABLE {0}";
        Statement statement = conn.createStatement();
        for (String table : listTable) {
            statement.execute(MessageFormat.format(sql, table));
        }
    }
    @Test
    public void testInsertRecord() throws Exception {


        //Tạo liên kết database với dbunit
        IDatabaseConnection dbUnitConnection= new DatabaseConnection(conn);


        //Tạo mảng 100 record product
        ArrayList<Product> listProduct = createListProduct();


        //Thêm các phần tử vào database
        String sql = "INSERT INTO PRODUCT(id, name, price, amount,description)  VALUES( ?,?,?,?,?)";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        for (Product product : listProduct) {
            pstmt.setString(1, Integer.toString(product.getId()));
            pstmt.setString(2, product.getName());
            pstmt.setString(3, Float.toString(product.getPrice()));
            pstmt.setString(4, Integer.toString(product.getAmount()));
            pstmt.setString(5, product.getDescription());
            pstmt.addBatch();
        }

        //Bắt đầu tính thời gian thực hiện
        long startTime = System.currentTimeMillis();

        //Bắt đầu chạy các query
        pstmt.executeBatch();

        //Kết thúc thời gian thực hiện
        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time(insert 100 records): " + (endTime-startTime) + "ms");

        //Nhận dữ liệu từ database về
        IDataSet databaseDataSet = dbUnitConnection.createDataSet();
        ITable actualTable = databaseDataSet.getTable("PRODUCT");

        //Nhập các exspect database để kiểm tra
        IDataSet expectedDataSet = getExpectSet();
        ITable expectedTable  = expectedDataSet.getTable("PRODUCT");
//        DatabaseOperation.CLEAN_INSERT.execute(dbUnitConnection, expectedDataSet);
//
//        //Kiểm tra dữ liệu ước lượng và dữ liệu thật có giống nhau
//        Assertion.assertEquals(expectedTable, actualTable);
//        //Xóa toàn bộ dữ liệu đã chỉnh sửa trong database
//        getTearDownOperation().execute(dbUnitConnection, expectedDataSet);
    }

    @Test
    public void testInsertAndDropTable() throws Exception {
        //Tạo liên kết database với dbunit
        IDatabaseConnection dbUnitConnection= new DatabaseConnection(conn);

        //Bắt đầu tính thời gian thực hiện
        long startTime = System.currentTimeMillis();
        insertMultipleTable();
        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time(insert 20 tables): " + (endTime-startTime) + "ms");
        long startTime2 = System.currentTimeMillis();
        deleteMultipleTable();
        long endTime2 = System.currentTimeMillis();
        System.out.println("Total execution time(drop 20 tables): " + (endTime2-startTime2) + "ms");
        //Kết thúc thời gian thực hiện

        //Nhận dữ liệu từ database về
        IDataSet databaseDataSet = dbUnitConnection.createDataSet();
        getTearDownOperation().execute(dbUnitConnection, databaseDataSet);
    }


}
