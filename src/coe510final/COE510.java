/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coe510final;

import java.awt.Event;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.swing.JTextArea;

/**
 *
 * @author Almuhallab Alsiyabi & Liam Iles 
 */
public class COE510 extends Application {
    static Connection connection = null;
    TextArea output = new TextArea();

    @Override
    public void start(Stage primaryStage) throws SQLException, FileNotFoundException {
        Stage mainStage = primaryStage;
        
        //Setting up GUI Interface
        
        StackPane stack = new StackPane();
        Button testConnnection = new Button("Connect to Database");
        Button dropTables = new Button("Drop Tables");
        Button populateTables = new Button("Populate Tables");
        Button createTableButton = new Button("Create Tables");
        Button searchQ = new Button("Search query");
        Button executeQuery = new Button("Execute Query");
        testConnnection.setMinWidth(130);
        dropTables.setMinWidth(130);
        populateTables.setMinWidth(130);
        createTableButton.setMinWidth(130);
        executeQuery.setMinWidth(130);
        searchQ.setMinWidth(130);
        GridPane pane = new GridPane();
        pane.add(testConnnection, 0, 0);
        pane.add(dropTables, 0, 1);
        pane.add(populateTables, 1, 0);
        pane.add(createTableButton, 1, 1);
        pane.add(searchQ, 1, 2);
        pane.add(executeQuery, 0, 2);
        pane.setHgap(10);
        pane.setVgap(10);
        output.setEditable(false);
        output.setStyle("-fx-font-family: monospace");
        output.setPrefSize(500, 400);
        TextField query = new TextField("Type query here");
        Button addButton = new Button("Add Customer");
        HBox top = new HBox(pane);
        VBox vPane = new VBox(top,query,output);
        vPane.setSpacing(20);
        Scene db = new Scene(vPane,800,380);
        
        
        //Creating actionable buttons
        
        populateTables.setOnAction(e -> {try {
            try {
                populateAllTables();
            } catch (InterruptedException ex) {
                Logger.getLogger(COE510.class.getName()).log(Level.SEVERE, null, ex);
            }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(COE510.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        });
        dropTables.setOnAction(e -> {dropAllTables();
            
        });
        executeQuery.setOnAction(e -> {executeQuery(query.getText());
            
        });
        createTableButton.setOnAction(e -> {createAllTables();
            
        });
        
        testConnnection.setOnAction(e -> {connect();
                                          System.out.println("Connected with the database!");
                                         }); 
        
        searchQ.setOnAction( e -> {try {output.clear();
            showQ(query.getText());
            } catch (SQLException ex) {
                Logger.getLogger(COE510.class.getName()).log(Level.SEVERE, null, ex);
            }
           });

        
        mainStage.setTitle("COE510");
        mainStage.setScene(db);
        mainStage.show();
        
        
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    //Creates tables in database
    
    private void createTables(String sql){

            try{
            Statement st = connection.createStatement();
            
            st.executeUpdate(sql);
            System.out.println("Table Created");
                }catch (SQLException e) {
                System.out.println("something went wrong");
                }
    }
    
    //Helper function to createTables() method
    
    private void createAllTables(){
        String createShop = "CREATE TABLE Shop(" +
                     "    StoreID NUMBER PRIMARY KEY, " +
                     "    StoreInformation VARCHAR2(50))" ;
        String createEmployee = "CREATE TABLE Employee(" +
                                "   EmployeeID NUMBER PRIMARY KEY, " +
                                "   FirstName VARCHAR2(20), " +
                                "   LastName VARCHAR2(20), " +
                                "   Position VARCHAR2(20) CHECK (Position IN ('Manager','Cashier','Womenizer')), " +
                                "   StoreID NUMBER REFERENCES Shop(StoreID))";
        String createCustomer = "CREATE TABLE Customer(" +
                                "   CustomerID NUMBER PRIMARY KEY, " +
                                "   FirstName VARCHAR2(20), " +
                                "   LastName VARCHAR2(20), " +
                                "   Balance FLOAT(32) DEFAULT 0.0)";
        String createProduct = "CREATE TABLE Product(" +
                                "    ProductID NUMBER PRIMARY KEY, " +
                                "    ProductCategory VARCHAR2(30), " +
                                "    ProductBrand VARCHAR2(20), " +
                                "    ProductName VARCHAR2(30), " +
                                "    Price DECIMAL)";
        String createProductSale =  "CREATE TABLE ProductSale(" +
                                    "    OrderID NUMBER PRIMARY KEY, " +
                                    "    DateOfTransaction DATE, " +
                                    "    RentalLength NUMBER DEFAULT 1, " +
                                    "    EmployeeID NUMBER REFERENCES Employee(EmployeeID), " +
                                    "    CustomerID NUMBER REFERENCES Customer(CustomerID), " +
                                    "    ProductID NUMBER REFERENCES Product(ProductID), " +
                                    "    StoreID NUMBER REFERENCES Shop(StoreID))";
        String createBuys = " CREATE TABLE Buys(" +
                            "    CustomerID NUMBER REFERENCES Customer(CustomerID), " +
                            "    ProductID NUMBER REFERENCES Product(ProductID), " +
                            "    Quantity NUMBER, " +
                            "    PRIMARY KEY (CustomerID,ProductID))";
        String createStocks =   "CREATE TABLE Stocks(" +
                                "    PRIMARY KEY (ProductID, StoreID), " +
                                "    Quantity NUMBER, " +
                                "    ProductID NUMBER, " +
                                "    StoreID NUMBER)";
        String createSalesHistoryView = "CREATE VIEW  SalesHistory AS " +
                                        "SELECT productsale.dateoftransaction,productsale.RentalLength,employee.FirstName AS \"Employee First Name\",employee.LastName AS \"Employee Last Name\",customer.FirstName AS \"Customer First Name\",customer.LastName AS \"Customer Last Name\",product.productname,shop.StoreInformation " +
                                        "FROM productsale,employee,customer,product,shop " +
                                        "WHERE productsale.employeeid = employee.employeeid " +
                                        "      AND productsale.customerid = customer.customerid " +
                                        "      AND productsale.productid = product.productid " +
                                        "      AND productsale.storeid = 1 " +
                                        "      AND shop.storeid = 1" +
                                        "UNION " +
                                        "SELECT productsale.dateoftransaction,productsale.RentalLength,employee.FirstName,employee.LastName AS \"Employee\",customer.FirstName,customer.LastName AS \"Customer\",product.productname,shop.StoreInformation " +
                                        "FROM productsale,employee,customer,product,shop " +
                                        "WHERE productsale.employeeid = employee.employeeid " +
                                        "      AND productsale.customerid = customer.customerid " +
                                        "      AND productsale.productid = product.productid " +
                                        "      AND productsale.storeid = 2 " +
                                        "      AND shop.storeid = 2" +
                                        "ORDER BY storeinformation";
        String createTrendingProductsView = "CREATE VIEW TrendingProducts AS " +
                                            "SELECT * " +
                                            "FROM product " +
                                            "WHERE EXISTS " +
                                            "(SELECT COUNT(*) FROM productsale " +
                                            "                 WHERE productsale.productid = product.productid " +
                                            "                 GROUP BY productsale.productid " +
                                            "                 HAVING COUNT(*) > 2)";
        String createStockQueryView =   "CREATE VIEW StockQuery AS " +
                                        "SELECT shop.storeid,product.productid,stocks.quantity,product.productcategory " +
                                        "FROM shop,product,stocks " +
                                        "WHERE stocks.storeid = shop.storeid AND product.productid = stocks.productid " +
                                        "ORDER BY productid";
        createTables(createShop);
        createTables(createEmployee);
        createTables(createCustomer);
        createTables(createProduct);
        createTables(createProductSale);
        createTables(createBuys);
        createTables(createStocks);
        createTables(createSalesHistoryView);
        createTables(createTrendingProductsView);
        createTables(createStockQueryView);
    }   
    
    
    //Drops tables in database
    
   private void dropTables(String sql) {

            try{
            Statement st = connection.createStatement();
            
            st.executeUpdate(sql);
            System.out.println("Tables dropped");
                }catch (SQLException e) {
                System.out.println("someething wrnt wrong");
                }
    }
   
   
   //Helper to dropTables() method'
    
    private void dropAllTables(){
        String dropProductSale = "DROP TABLE ProductSale ";
        String dropEmployee = "DROP TABLE Employee ";
        String dropBuys = "DROP TABLE Buys ";
        String dropShop = "DROP TABLE Shop ";   
        String dropStocks = "DROP TABLE Stocks ";
        String dropCustomer = "DROP TABLE Customer ";
        String dropProduct = "DROP TABLE Product " ;
        String dropSalesHistoryView = "DROP VIEW SalesHistory";
        String dropTrendingProductsView = "DROP VIEW TrendingProducts";
        String dropStockQueryView = "DROP VIEW StockQuery";
        dropTables(dropProductSale);
        dropTables(dropEmployee);
        dropTables(dropBuys);
        dropTables(dropShop);
        dropTables(dropStocks);
        dropTables(dropCustomer);
        dropTables(dropProduct);
        dropTables(dropSalesHistoryView);
        dropTables(dropTrendingProductsView);
        dropTables(dropStockQueryView);
    }
   
   //Connects the application to the database
   
    private void connect(){
        System.out.println("Connecting");
        String dbURL = "jdbc:oracle:thin:homeuser/password2@localhost:1521:xe";
        try {
            connection = DriverManager.getConnection(dbURL);
        } catch (SQLException ex) {
            System.out.println("connection error");
            Logger.getLogger(COE510.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (connection == null) {
            System.out.println("Error connecting to the server");
    }}
    
    //Displays the query results in the GUI
    
    private void showQ(String sql) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        ResultSetMetaData rsD = rs.getMetaData();
        int columns = rsD.getColumnCount();
        for (int i = 1; i <= columns ; i++) {
            System.out.format("%-40s", rsD.getColumnName(i));
            output.appendText(String.format("%30s", rsD.getColumnName(i)));
        }
        System.out.println("");
        output.appendText("\n");
        while (rs.next()) {
                    for(int x = 1; x <= columns; x++){
                    System.out.format("%-40s", rs.getString(x));
                    String tempstring = rs.getString(x);
                    output.appendText(String.format("%30s", tempstring));

                }
                System.out.println("");
                output.appendText("\n");

            }
        
}      
    
    
    //Executes user input queries in the GUI
    
    private void executeQuery(String sql){
            try{
            Statement st = connection.createStatement();
                System.out.println(sql);
            st.executeQuery(sql);
            System.out.println("Query executed!");
                }catch (SQLException e) {
            System.out.println("something wrnt wrong");
            }
    }
    
    //Pulls dummy data from a file to populate tables
    
    private void populateTables(String sql) {
            try{
            PreparedStatement st = connection.prepareStatement(sql);
            st.executeUpdate();
            System.out.println("Insertion Complete!");
                }catch (SQLException e) {
                System.out.println("someething wrnt wrong");
            }
        
    }
    
    //Helper for populateTables() method
    
    private void populateAllTables() throws FileNotFoundException, InterruptedException{
        int i = 1;
        Scanner scanner = new Scanner(new File("C:\\Users\\almuh\\OneDrive\\Documents\\NetBeansProjects\\COE510Final\\populate_tables.txt"));
			while (scanner.hasNextLine()) {
                        System.out.println(i);
			populateTables(scanner.nextLine());
                        i++;
			}
        }

}
