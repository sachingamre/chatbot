/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sachin.core.api.db;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.sachin.app.App;
import com.sachin.core.utils.Utils;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sachin
 * Class implements Singleton Pattern and since it is final, no one can extend it
 */
final public class DbManager {

    // Static variable which keeps the single instance of DbManager
    public static HashMap<String, DbManager> dbSingletonInstancePool = new HashMap() ;
    public static HashMap<String, HashMap> dbCredentialPool = new HashMap() ;
    private ResultSet resultSet ;
    private PreparedStatement statement ;

    private Connection connection ;
    // private String sql ;

    // Set Db Params
    //public static String DB_HOST = null ;
    //public static String DB_NAME = null ;
    //public static String DB_USER = null ;
    //public static String DB_PASS = null ;
    //public static String DB_DRIVER = null ;

    public static String init(String dbHost, String dbName, String dbUser, String dbPass, String dbDriver) {
        HashMap credentials = new HashMap() ;
        credentials.put("dbHost", dbHost) ;
        credentials.put("dbName", dbName) ;
        credentials.put("dbUser", dbUser) ;
        credentials.put("dbPass", dbPass) ;
        credentials.put("dbDriver", dbDriver) ;

        dbCredentialPool.put(dbHost + "_" + dbName, credentials) ;
        return dbHost + "_" + dbName ;
    }
    
    /*
     * Create singleton Instance of DbManager
     * Static function takes no parameter
     * @access: public
     */
    public static DbManager getInstance(String dbKey)
    {
        if(!dbSingletonInstancePool.containsKey(dbKey)) {
            if(dbCredentialPool.containsKey(dbKey)) {
                HashMap credentials = dbCredentialPool.get(dbKey) ;
                dbSingletonInstancePool.put(dbKey, new DbManager((String) credentials.get("dbHost"), (String) credentials.get("dbUser"), (String) credentials.get("dbPass"), (String) credentials.get("dbName"), (String) credentials.get("dbDriver"))) ;
            }
            else {
                App.logger.error("Credentials for " + dbKey + " and not present, cannot instantiate database instance");
            }
        }
        return dbSingletonInstancePool.get(dbKey) ;
    }

    /*
     * Private constuctor which avoids other classes to instantiate the class with new keyword
     * @access: private
     * @param1: String  host
     * @param2: String  dbuser
     * @param3: String  dbpass
     * @param4: String  dbname
     * @param5: String  dbdriver
     */
    private DbManager(String dbHost, String dbUser, String dbPass, String dbName, String dbDriver)
    {
        String dsn = "jdbc:" + dbDriver + "://" + dbHost + ":3306/" + dbName ;
        String driver = "com." + dbDriver + ".jdbc.Driver";
        try {
            Class.forName(driver).newInstance();
            connection = (Connection) DriverManager.getConnection(dsn, dbUser, dbPass);
        }
        catch (Exception ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getPrimaryKey(String tableName)
    {
        String pKey = null ;

        try {
            if(!tableName.trim().equals("")) {
                DatabaseMetaData metadata = connection.getMetaData();
                resultSet = metadata.getPrimaryKeys(null, null, tableName);
                while (resultSet.next()) {
                    pKey = resultSet.getString("COLUMN_NAME") ;
                    break ;
                }
                resultSet.close();
            }
        }
        catch (SQLException ex) {
             Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pKey ;
    }

    public ArrayList getColumns(String tableName)
    {
        ArrayList columns = new ArrayList() ;
        try {
            if(!tableName.trim().equals("")) {
                DatabaseMetaData metadata = connection.getMetaData();
                resultSet = metadata.getColumns(null, null, tableName, null);
                while (resultSet.next()) {
                    columns.add(resultSet.getString("COLUMN_NAME")) ;
                }
                resultSet.close();
            }
        }
        catch (SQLException ex) {
             Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return columns ;
    }

    public int delete(String sql, List vals)
    {
        int rs = 0 ;
        try {
            statement = (PreparedStatement ) connection.prepareStatement(sql) ;
            statement = _setParams(statement, vals) ;
            rs = statement.executeUpdate();
        }
        catch (SQLException ex) {
             Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs ;
    }

    public int update(String sql, List vals)
    {
        int rs = 0 ;
        try {
            statement = (PreparedStatement) connection.prepareStatement(sql);
            statement = _setParams(statement, vals) ;
            rs = statement.executeUpdate();
        }
        catch (SQLException ex) {
             Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs ;
    }

    public int insert(String sql, List vals)
    {
        int rs = 0 ;
        try {
            statement = (PreparedStatement) connection.prepareStatement(sql);
            statement = _setParams(statement, vals) ;
            rs = statement.executeUpdate(sql);
        }
        catch (SQLException ex) {
             Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs ;
    }

    public ResultSet select(String sql, List vals)
    {
        try {
            statement = (PreparedStatement) connection.prepareStatement(sql);
            statement = _setParams(statement, vals) ;
            resultSet = statement.executeQuery();
        }
        catch (SQLException ex) {
             Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultSet ;
    }

    public String selectOne(String sql, List vals)
    {
        String val = null ;

        try {
            statement = (PreparedStatement) connection.prepareStatement(sql) ;
            statement = _setParams(statement, vals) ;
            resultSet = statement.executeQuery() ;
            if(resultSet.first()) {
                val = resultSet.getString(1) ;
            }
        }
        catch (SQLException ex) {
             Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return val ;
    }

    public HashMap selectRow(String sql, List vals)
    {
        HashMap resultant = new HashMap() ;

        try {
            statement = (PreparedStatement) connection.prepareStatement(sql);
            statement = _setParams(statement, vals) ;
            resultSet = statement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData() ;

            if(resultSet.next()) {
                for(int i = 1; i <= rsmd.getColumnCount(); i++) {
                    resultant.put(rsmd.getColumnName(i), resultSet.getString(rsmd.getColumnName(i))) ;
                }
            }
        }
        catch (SQLException ex) {
             Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultant ;
    }

    /**
     * Produces a string representation of a complete result set
     * @param rs The ResultSet to be displayed
     * @return The string representation
     * @throws SQLException to indicate a problem with the ResultSet
     */
    public static String dumpResultSet( ResultSet rs ) throws SQLException {
        StringBuilder buf = new StringBuilder();

        if( rs != null ) {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount() + 1;
            while( rs.next() ) {
                buf.append("\n").append(rs.getRow()).append(". ") ;
                for( int i = 1; i < columnCount; i++ ) {
                    buf.append(rsmd.getColumnLabel(i)).append(":").append( rs.getObject(i));
                    buf.append( " " );
                }
                buf.append( "\n\n" );
            }
        }
        return buf.toString();
    }

    public void close()
    {
        try {
            connection.close();
        } catch (SQLException ex) {
             Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private PreparedStatement _setParams(PreparedStatement statement, List list) {
        for(int i = 0; i < list.size(); i++) {
            HashMap hMap = (HashMap) list.get(i) ;
            Utils.printHash(hMap, "Db params === ") ;
            String type = (String) hMap.get("type") ;
            int index = i+1 ;
            try {
                System.out.println("Passed value --" + hMap.get("value").toString()) ;
                if(type.equals("int") && hMap.get("value") != null) {
                    int val = Integer.parseInt(hMap.get("value").toString()) ;
                    System.out.println("Val is " + val);
                    statement.setInt(index, val);
                }
                else if(type.equals("string") && hMap.get("value") != null) {
                    String val = hMap.get("value").toString() ;
                    statement.setString(index, val);
                }
                else if(type.equals("float") && hMap.get("value") != null) {
                    float val = Integer.parseInt(hMap.get("value").toString()) ;
                    System.out.println("Val is " + val);
                    statement.setFloat(index, val);
                }
                else if(type.equals("date") && hMap.get("value") != null) {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = df.parse(hMap.get("value").toString());
                    statement.setDate(index, (java.sql.Date) date);
                }
            }
            catch (SQLException ex) {
                Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (Exception ex) {
                    Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
                }
        }

        return statement ;
    }

}
