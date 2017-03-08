package com.linecorp.example.springelephant.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.json.JSONArray;
import com.linecorp.example.springelephant.Person;

public class PostgresHelper {
    
    private Connection conn;
    private String dbUrl;
    
    //we don't like this constructor
    protected PostgresHelper() {}
    
    public PostgresHelper(String dbUrl) {
        this.dbUrl = dbUrl;
    }
    
    public boolean connect() throws SQLException, ClassNotFoundException {
        if (dbUrl.isEmpty()) {
            throw new SQLException("Database credentials missing");
        }
        
        Class.forName("org.postgresql.Driver");
        this.conn = DriverManager.getConnection(this.dbUrl);
        return true;
    }
    
    public ResultSet execQuery(String query) throws SQLException {
        return this.conn.createStatement().executeQuery(query);
    }
    
    public int insert(String table, String aName, String aPhoneNumber) throws SQLException {
        
        String query = String.format("INSERT INTO %s (name, phone_number) VALUES (%s, %s)", table,
                                     aName, aPhoneNumber);
        System.out.println("SQL: " + query);
        
        return this.conn.createStatement().executeUpdate(query);
    }
    
    public Person getPerson(String table, String aName) throws SQLException {
        
        String query = String.format("SELECT * FROM %s WHERE name = '%s')", table, aName);
        System.out.println("SQL: " + query);
        ResultSet rs = this.conn.createStatement().executeQuery(query);
        rs.next();
        String existName = rs.getString(1);
        String existPhoneNumber = rs.getString(2);
        Person existData = null;
        existData.name = existName;
        existData.phoneNumber = existPhoneNumber;
        
        return existData;
    }
}
