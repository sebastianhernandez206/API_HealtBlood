/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.lasalle.rest;

import com.google.gson.Gson;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.ArrayList;
import jakarta.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.lasalle.controller.UsuarioController;
import org.lasalle.model.Usuario;
import org.lasalle.model.Medida;




/**
 *
 * @author sebastian_hdz_gtz
 */
@Path("glucosa")
public class RestGlucosa {
    
    @Path("saludar")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response saludar(){
        String saludo = """
                      {"Saludo":"Hola desde java"}
                      """;
        return Response.ok(saludo).build();
    }
    
    @Path("SaludarCumpleanios")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response saludarCumpleanios(
            @QueryParam("nombre") String nombre
    ){
        String out = """
                     {"Response" : "Feliz cumple %s"}
                     """;
        out = String.format(out, nombre);
        return Response.ok(out).build();
    }
    
    @Path("getAllUsuarios")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsuarios(){
        UsuarioController uc = new UsuarioController();
        List<Usuario> lista = new ArrayList<>();
        Gson gson = new Gson();
        String out = "";
        try {
            lista = uc.getUsuarios();
            out = gson.toJson(lista);
        }catch (Exception e) {
            e.printStackTrace();
            out = """
                  {"result:"Empty table"}
                  """;
        }
        return Response.ok(out).build();
    }
    
    @Path("getAllMedidas")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMedidas(){
        UsuarioController uc = new UsuarioController();
        List<Medida> lista = new ArrayList<>();
        Gson gson = new Gson();
        String out = "";
        try {
            lista = uc.getMedidas();
            out = gson.toJson(lista);
        } catch (Exception e) {
            e.printStackTrace();
            out = """
                  {"result":"Empty table"}
                  """;
        }
        return Response.ok(out).build();
    }

    @Path("registrarUsuario")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarUsuario(
            @FormParam("nombre") String nombre,
            @FormParam("primer_apellido") String primerApellido,
            @FormParam("segundo_apellido") String segundoApellido,
            @FormParam("fecha_nacimiento") String fechaNacimiento,
            @FormParam("tipo_diabetes") String tipoDiabetes,
            @FormParam("fecha_registro") String fechaRegistro,
            @FormParam("email") String email
    ) {
        Gson gson = new Gson();
        Map<String, Object> respuesta = new LinkedHashMap<>();

        if (nombre == null || nombre.isBlank()
                || primerApellido == null || primerApellido.isBlank()) {
            respuesta.put("status", "error");
            respuesta.put("mensaje", "Los campos nombre y primer_apellido son obligatorios");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(gson.toJson(respuesta)).build();
        }

        try {
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre.trim());
            usuario.setPrimerApellido(primerApellido.trim());
            usuario.setSegundoApellido(segundoApellido);
            usuario.setFechaNacimiento(fechaNacimiento);
            usuario.setTipoDiabetes(tipoDiabetes);
            usuario.setFechaRegistro(
                    (fechaRegistro == null || fechaRegistro.isBlank())
                            ? LocalDate.now().toString()
                            : fechaRegistro.trim()
            );
            usuario.setEmail(email);

            int idUsuario = new UsuarioController().insertarUsuario(usuario);

            respuesta.put("status", "ok");
            respuesta.put("mensaje", "Usuario registrado correctamente");
            respuesta.put("id_usuario", idUsuario);
            return Response.status(Response.Status.CREATED)
                    .entity(gson.toJson(respuesta)).build();
        } catch (IllegalArgumentException e) {
            respuesta.put("status", "error");
            respuesta.put("mensaje", "Formato de fecha invalido, use yyyy-MM-dd");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(gson.toJson(respuesta)).build();
        } catch (Exception e) {
            e.printStackTrace();
            respuesta.put("status", "error");
            respuesta.put("mensaje", "Error al registrar el usuario: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(gson.toJson(respuesta)).build();
        }
    }

    @Path("registrarMedida")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarMedida(
            @FormParam("id_usuario") String idUsuario,
            @FormParam("valor_mg_dl") String valorMgDl,
            @FormParam("tipo_lectura") String tipoLectura,
            @FormParam("notas") String notas,
            @FormParam("fecha_registro") String fechaRegistro
    ) {
        Gson gson = new Gson();
        Map<String, Object> respuesta = new LinkedHashMap<>();

        if (idUsuario == null || idUsuario.isBlank()
                || valorMgDl == null || valorMgDl.isBlank()) {
            respuesta.put("status", "error");
            respuesta.put("mensaje", "Los campos id_usuario y valor_mg_dl son obligatorios");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(gson.toJson(respuesta)).build();
        }

        int idUsuarioInt;
        int valorMgDlInt;
        try {
            idUsuarioInt = Integer.parseInt(idUsuario.trim());
            valorMgDlInt = Integer.parseInt(valorMgDl.trim());
        } catch (NumberFormatException e) {
            respuesta.put("status", "error");
            respuesta.put("mensaje", "id_usuario y valor_mg_dl deben ser numeros enteros");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(gson.toJson(respuesta)).build();
        }

        if (valorMgDlInt < 0 || valorMgDlInt > 65535) {
            respuesta.put("status", "error");
            respuesta.put("mensaje", "valor_mg_dl debe estar entre 0 y 65535");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(gson.toJson(respuesta)).build();
        }

        try {
            Medida medida = new Medida();
            medida.setIdUsuario(idUsuarioInt);
            medida.setGlucosa(valorMgDlInt);
            medida.setContexto(tipoLectura);
            medida.setNotas(notas);
            medida.setFechaHora(
                    (fechaRegistro == null || fechaRegistro.isBlank())
                            ? LocalDate.now().toString()
                            : fechaRegistro.trim()
            );

            int idLectura = new UsuarioController().insertarMedida(medida);

            respuesta.put("status", "ok");
            respuesta.put("mensaje", "Medida registrada correctamente");
            respuesta.put("id_lectura", idLectura);
            return Response.status(Response.Status.CREATED)
                    .entity(gson.toJson(respuesta)).build();
        } catch (SQLIntegrityConstraintViolationException e) {
            respuesta.put("status", "error");
            respuesta.put("mensaje", "El id_usuario no existe");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(gson.toJson(respuesta)).build();
        } catch (IllegalArgumentException e) {
            respuesta.put("status", "error");
            respuesta.put("mensaje", "Formato de fecha invalido, use yyyy-MM-dd");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(gson.toJson(respuesta)).build();
        } catch (Exception e) {
            e.printStackTrace();
            respuesta.put("status", "error");
            respuesta.put("mensaje", "Error al registrar la medida: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(gson.toJson(respuesta)).build();
        }
    }

}
