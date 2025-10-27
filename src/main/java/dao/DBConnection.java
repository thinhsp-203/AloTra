package dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private final String serverName = "DESKTOP-84O1O38";    
    private final String dbName     = "AloTra";
    private final String portNumber = "6316";
    private final String userID     = "webuser";
    private final String password   = "WebUser@123";

    public Connection getConnection() throws Exception {
        String url = "jdbc:sqlserver://" + serverName + ":" + portNumber
                   + ";databaseName=" + dbName
                   + ";encrypt=false;trustServerCertificate=true";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(url, userID, password);
    }
}