/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.lasalle.connection;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author sebastian_hdz_gtz
 */
public class ConnectionMysql {
    Connection conn;
    
    // Método para leer variables de entorno en la nube o usar las locales por defecto
    private static String env(String key, String def) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            value = System.getProperty(key);
        }
        return (value == null || value.isBlank()) ? def : value;
    }

    public Connection open(){
        // Aquí declaramos las variables. Si no existen en la nube, usa las de tu Mac.
        String host     = env("DB_HOST", "127.0.0.1");
        String port     = env("DB_PORT", "3306");
        String bdName   = env("DB_NAME", "healthBlood");
        String user     = env("DB_USER", "root");
        String password = env("DB_PASSWORD", "Sebastianoso206");
        String sslMode  = env("DB_SSL_MODE", "DISABLED");
        
        String url = env("DB_URL", null);
        if (url == null) {
            url = "jdbc:mysql://" + host + ":" + port + "/" + bdName
                + "?useUnicode=true&characterEncoding=utf-8&sslMode=" + sslMode;
            if ("DISABLED".equalsIgnoreCase(sslMode)) {
                url += "&allowPublicKeyRetrieval=true&useSSL=false";
            }
        }

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
            return conn;
        } catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public void close(){
        if(conn != null){
            try{
                conn.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}