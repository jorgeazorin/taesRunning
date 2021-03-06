package taes.running;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import kotlin.Pair;

public class Usuario implements Serializable {
    private transient   SweetAlertDialog pDialog;
    private transient Context c;
    private String email;
    public String getEmail(){
        return  email;
    }
    public void setEmail(String email){
        this.email=email;
    }

    private String  nombre;
    public String getNombre(){
        return  nombre;
    }
    public void setNombre(String email){
        this.nombre=email;
    }


      private String id;
    public String getId(){
        return  id;
    }
    public void setId(String id){
        this.id=id;
    }
    private String provincia;
    public String getprovincia(){
        return  provincia;
    }
    public void setprovincia(String provincia){
        this.provincia=provincia;
    }

    private String nivel;
    public String getNivel(){
        return  nivel;
    }
    public void setNivel(String email){
        this.nivel=email;
    }


    private String genero;
    public String getGenero(){
        return  genero;
    }
    public void setGenero(String email){
        this.genero=email;
    }

    private Date nacimiento;

    public Date getNacimiento(){
        return nacimiento;
    }

    public void setNacimiento(Date nacimiento){
        this.nacimiento=nacimiento;
    }

    private int calorias;
    public int getCalorias(){
        return  calorias;
    }
    public void setCalorias(int email){
        this.calorias=email;
    }

    private float distancia;
    public void setDistancia(float distancia){
        this.distancia=distancia;
    }
    private String foto;
    public  void setFoto(String foto){
        this.foto=foto;
    }
    public String getFoto(){
        return foto;
    }

    public boolean enviarAlServidor(final Context c){
        this.c=c;
        System.out.println("kkkk esto no va");
        pDialog = new SweetAlertDialog(c, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        IniciarSesion();
        return true;
    }

    private void ObtenerRanking(){
        System.out.println("kkk Otbeniedo ranking");
        Fuel.get(Principal.servidor+"/users/").responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                pDialog.setTitleText("Error!").setContentText("No se ha obtenido el ranking").setConfirmText("OK").showCancelButton(false).setConfirmClickListener(null).changeAlertType(SweetAlertDialog.ERROR_TYPE);
             //   GetRutas();
            }
            @Override
            public void success(Request request,Response response, String data) {
                GetRutas(data);
                System.out.println("kkk dataa es"+data);
            }
        });
    }

    private void IniciarSesion(){
        final Usuario usuario=this;
        JSONObject json = new JSONObject();
        try {
            json.put("name", nombre);
            json.put("password",id);
            json.put("email",email);
            json.put("level",nivel);
            json.put("city","Alicante");
            json.put("rol","USER");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Fuel.post(Principal.servidor+"/users").header(new Pair<>("Content-Type", "application/json")).body(json.toString(), Charset.defaultCharset()).responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                ObtenerRanking();
            }
            @Override
            public void success(Request request,Response response, String data) {
                ObtenerRanking();
            }
        });
    }

    private void GetRutas(final String ranking){
        Fuel.get(Principal.servidor+"/routes/").responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                pDialog.setTitleText("Error!").setContentText("No se han obtenido rutas").setConfirmText("OK").showCancelButton(false).setConfirmClickListener(null).changeAlertType(SweetAlertDialog.ERROR_TYPE);
                System.out.println("kkk Error Obteniendo Rutas");
                GetUsuario("",ranking);
            }

            @Override
            public void success(Request request,Response response, String data) {
                GetUsuario(data, ranking);
            }
        });
    }

    private void GetUsuario(final String rutas, final String Ranking){
        final Usuario usuario=this;
        Fuel.get(Principal.servidor+"/users/email/" + email).responseString(new Handler<String>()
        {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                pDialog.setTitleText("Error!").setContentText("No se ha obtenido el usaurio del email").setConfirmText("OK").showCancelButton(false).setConfirmClickListener(null).changeAlertType(SweetAlertDialog.ERROR_TYPE);
            }
            @Override
            public void success(Request request,Response response, String data) {
                try {
                    JSONArray jsonArray = new JSONArray(data);
                    JSONObject jsonbject = jsonArray.getJSONObject(0);
                    usuario.setId(jsonbject.getString("id"));
                    usuario.setNivel(jsonbject.getString("level"));
                    usuario.setprovincia(jsonbject.getString("city"));
                    usuario.setGenero(jsonbject.getString("rol"));
                    System.out.println("kkkk Id Obtenido del usuario" + getId());
                    Intent intent = new Intent(c, Principal.class);
                    intent.putExtra("Usuario", usuario);
                    intent.putExtra("Rutas",rutas);
                    System.out.println("kkk intent es "+Ranking);
                    intent.putExtra("Ranking",Ranking);
                    c.startActivity(intent);
                    pDialog.dismiss();
                } catch (JSONException e) {
                    pDialog.setTitleText("Error json id usuario!").setContentText(data).setConfirmText("OK").showCancelButton(false).setConfirmClickListener(null).changeAlertType(SweetAlertDialog.ERROR_TYPE);
                }
             //   pDialog.dismiss();
            }
        });
    }
}
