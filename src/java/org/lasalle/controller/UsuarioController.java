/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.lasalle.controller;

import java.util.List;
import java.util.ArrayList;
import org.lasalle.connection.ConnectionMysql;
import org.lasalle.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import org.lasalle.model.Medida;

/**
 *
 * @author sebastian_hdz_gtz
 */
public class UsuarioController {
    
    public List<Usuario> getUsuarios()throws SQLException{
        //Declaramos la estrucutra de datos
        List<Usuario> lista = new ArrayList<>();
        //Declaramos la sentencia a ejecutar
        String sql = "SELECT * FROM users;";
        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();
        PreparedStatement pstm =  conn.prepareStatement(sql);
        ResultSet rs = pstm.executeQuery();
        
        while(rs.next()){
            Usuario user = new Usuario();
            user.setIdUsuario(rs.getInt("id_usuario"));
            user.setNombre (rs.getString("nombre"));
            user.setPrimerApellido(rs.getString("primer_apellido"));
            user.setSegundoApellido(rs.getString("segundo_apellido"));
            user.setEmail(rs.getString("email"));
            user.setFechaNacimiento(rs.getString("fecha_nacimiento"));
            user.setTipoDiabetes(rs.getString("tipo_diabetes"));
            user.setFechaRegistro(rs.getString("fecha_registro"));
            lista.add(user);
        }
        rs.close();
        connMysql.close();
        conn.close();
        return lista;
    }
    
    public List<Medida> getMedidas() throws SQLException {
        List<Medida> lista = new ArrayList<>();
        String sql = "SELECT * FROM lectura_glucosa;";
        
        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();
        PreparedStatement pstm =  conn.prepareStatement(sql);
        ResultSet rs = pstm.executeQuery();
        
        while(rs.next()){
            Medida medida = new Medida();
            medida.setIdMedida(rs.getInt("id_lectura"));
            medida.setIdUsuario(rs.getInt("id_usuario"));
            medida.setGlucosa(rs.getInt("valor_mg_dl"));
            medida.setContexto(rs.getString("tipo_lectura"));
            medida.setNotas(rs.getString("notas"));
            medida.setFechaHora(rs.getString("fecha_registro")); 
            
            lista.add(medida);
        }
        
        rs.close();
        connMysql.close();
        conn.close();
        
        return lista;
    }

    public int insertarUsuario(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO users (nombre, primer_apellido, segundo_apellido, "
                + "fecha_nacimiento, tipo_diabetes, fecha_registro, email) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        ConnectionMysql connMysql = new ConnectionMysql();
        try (Connection conn = connMysql.open();
             PreparedStatement pstm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstm.setString(1, usuario.getNombre());
            pstm.setString(2, usuario.getPrimerApellido());
            pstm.setString(3, usuario.getSegundoApellido());
            pstm.setDate(4, toSqlDate(usuario.getFechaNacimiento()));
            pstm.setString(5, usuario.getTipoDiabetes());
            pstm.setDate(6, toSqlDate(usuario.getFechaRegistro()));
            pstm.setString(7, usuario.getEmail());

            pstm.executeUpdate();

            try (ResultSet rs = pstm.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return -1;
        } finally {
            connMysql.close();
        }
    }

    public int insertarMedida(Medida medida) throws SQLException {
        String sql = "INSERT INTO lectura_glucosa (id_usuario, valor_mg_dl, tipo_lectura, "
                + "notas, fecha_registro) VALUES (?, ?, ?, ?, ?)";

        ConnectionMysql connMysql = new ConnectionMysql();
        try (Connection conn = connMysql.open();
             PreparedStatement pstm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstm.setInt(1, medida.getIdUsuario());
            pstm.setInt(2, medida.getGlucosa());
            pstm.setString(3, medida.getContexto());
            pstm.setString(4, medida.getNotas());
            pstm.setDate(5, toSqlDate(medida.getFechaHora()));

            pstm.executeUpdate();

            try (ResultSet rs = pstm.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return -1;
        } finally {
            connMysql.close();
        }
    }

    private java.sql.Date toSqlDate(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            return null;
        }
        return java.sql.Date.valueOf(fecha.trim());
    }

}


