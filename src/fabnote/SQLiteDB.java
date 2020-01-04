/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fabnote;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteDB {
    private static Connection conn = null;
    private Statement stmt = null;
    
    public SQLiteDB(String DBfullPathName){
        conn = getConnection(DBfullPathName);
    }
    
    private Connection getConnection(String DBfullPathName){
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + DBfullPathName);
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(ex.getClass().getName() + ": " + ex.getMessage());
            System.exit(0);
        } System.out.println("Connection to database successful !!");
        return conn;
    }
    
    public void addToDatabase(String time, String hashtags, String content) throws SQLException{
        String insert = "INSERT INTO notes(time, hashtags, content) ";
        String stringValues = "VALUES('" + time + "', '" + hashtags + "', '" + apostrophDeal(content) + "');";
        this.stmt = conn.createStatement();
        this.stmt.executeUpdate(insert + stringValues);
        this.stmt.close();
        System.out.println("Insertion was successful !!");
    }
    
    public void updateDB(int id, String time, String hashtags, String content) throws SQLException{
        String updateQuery = "UPDATE notes SET time = '" + time + "', hashtags = '" + hashtags
                + "', content = '" + apostrophDeal(content) + "' WHERE id = " + id + ";";
        this.stmt = conn.createStatement();
        this.stmt.execute(updateQuery);
        this.stmt.close();
        System.out.println("Update was successful !!");
    }
    
    
    public void deleteFromDatabase(int id) throws SQLException{
        String updateQuery = "DELETE FROM notes WHERE id = " + id;
        this.stmt = conn.createStatement();
        this.stmt.executeUpdate(updateQuery);
        this.stmt.close();
        System.out.println("Deletion was successful !!");
    }
    
    public void addAllNotes(java.util.List<Note> noteList) throws SQLException{
        
        this.stmt = conn.createStatement();
        try (ResultSet resultSet = this.stmt.executeQuery("SELECT * FROM notes")) {
            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String time = resultSet.getString("time") ;
                String hashtags = resultSet.getString("hashtags");
                String content = resultSet.getString("content"); 
                noteList.add(new Note(id, time, hashtags, content));
            }
        }
        this.stmt.close();
    }
    
    
    private String apostrophDeal(String str){
        return str.replaceAll("'", "''");
    }
    
    
}
