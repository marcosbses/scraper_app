package com.example.marcos.myfirstapp;

import android.text.TextUtils;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Map;

/**
 * Created by marcos on 8/12/16.
 */
public class BrouScraper {
    public static String obtenerSaldo(String documento,String pass) {

        Connection.Response res2 = null;
        try {
            res2 = Jsoup.connect("https://www.canales.brou.com.uy/eBanking")
                    .method(Connection.Method.GET)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            return "no se pudo conectar a eBanking";
        }
        Connection.Response res = null;
//ingreso a ebrou
        try {
            res = Jsoup.connect("https://www.canales.brou.com.uy/eBanking/seguridad/loginFlow.htm?execution=e1s1")
                    //.userAgent("User-Agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36")
                    .referrer("https://www.canales.brou.com.uy/eBanking/seguridad/loginFlow.htm?execution=e2s1")
                    .followRedirects(true)
                    .cookies(res2.cookies())
                    .header("Host", "www.canales.brou.com.uy")
                    .header("Origin", "https://www.canales.brou.com.uy")
                    .header("Connection", "keep-alive")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .data("tipoDocToLogin", "CI", "userName", documento, "password", pass, "_eventId_ok", "")
                    .method(Connection.Method.POST)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            return "no se pudo conectar a login";
        }

        try {
            res = Jsoup.connect("https://www.canales.brou.com.uy/eBanking/inicio/consolidada.htm")
                    .cookies(res.cookies())
                    .data("cargaGrupo","CUENTA")
                    .method(Connection.Method.POST)
                    .execute();
        } catch (IOException e) {
            //e.printStackTrace();
            return "no se pudo conectar a consolidado";
        }

        Document doc = null;
        try {
            doc = res.parse();
        } catch (IOException e) {
            e.printStackTrace();
            return "no se pudo parsear";
        }

        Element saldoElem=doc.select("td.textImporte").first();
        if(saldoElem==null){Log.i("informacion","no se encontro saldo");return null;}
        String saldo=saldoElem.text();
        saldo=saldo.replace(" ","");
        return saldo;

    }

    public static String obtenerMovimientos(String documento,String pass) {
        Log.i("informacion","obteniendo movimientos en brouscraper");
        Connection.Response res2 = null;
        try {
            res2 = Jsoup.connect("https://www.canales.brou.com.uy/eBanking")
                    .method(Connection.Method.GET)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            return "no se pudo conectar a eBanking";
        }
        Connection.Response res = null;
//ingreso a ebrou
        try {
            res = Jsoup.connect("https://www.canales.brou.com.uy/eBanking/seguridad/loginFlow.htm?execution=e1s1")
                    //.userAgent("User-Agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36")
                    .referrer("https://www.canales.brou.com.uy/eBanking/seguridad/loginFlow.htm?execution=e2s1")
                    .followRedirects(true)
                    .cookies(res2.cookies())
                    .header("Host", "www.canales.brou.com.uy")
                    .header("Origin", "https://www.canales.brou.com.uy")
                    .header("Connection", "keep-alive")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .data("tipoDocToLogin", "CI", "userName", documento, "password", pass, "_eventId_ok", "")
                    .method(Connection.Method.POST)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            return "no se pudo conectar a login";
        }
        Log.i("informacion","se conecto a ebrou");

        Map<String,String> cookies=res.cookies();

        try {
            res = Jsoup.connect("https://www.canales.brou.com.uy/eBanking/inicio/consolidada.htm")
                    .cookies(cookies)
                    .header("Connection", "keep-alive")
                    .data("cargaGrupo","CUENTA")
                    .method(Connection.Method.POST)
                    .execute();
        } catch (IOException e) {
            //e.printStackTrace();
            return "no se pudo conectar a consolidado";
        }


        //saco el link e ingreso a movimientos
        Document doc=null;
        try {
            doc = res.parse();
        } catch (IOException e) {
            Log.i("informacion","no se pudo parsear");
            return "no se pudo parsear";
        }
        Log.i("informacion","se pudo parsear");
        String movPath;
        try {
            movPath = doc.select("a.consultaMovs").first().attr("href");
        }catch (NullPointerException e){
            Log.i("informacion","no se pudo encontar a");
            return null;
        }

        String numPath=movPath.split("=")[1];
        Log.i("informacion","se obtuvo el link a movimientos");
        try {
            res = Jsoup.connect("https://www.canales.brou.com.uy/eBanking/cuentas/consultaMovimientosCuenta.htm?id_cuenta="+numPath)
                    .cookies(cookies)
                    .data("id_cuenta",numPath)
                    .method(Connection.Method.GET)
                    .execute();
        } catch (IOException e) {
            return "no se pudo conectar a movimientos";
        }

        try {
            doc = res.parse();
        } catch (IOException e) {
            return e.getMessage();
        }
        Elements importes=doc.select("td.textImporte");
        Elements filas=doc.select("table#table_movimientos_cuenta_completa").select("tr");
        Element ultimoImporte=importes.last();
        int ultimoIndiceImporte=doc.select("td.textImporte").indexOf(ultimoImporte);
        int ultimoIndiceTabla=filas.size()-1;
        String[] arrayMovimientos=new String[5];
        Elements fila=null;
        Log.i("informacion","se prepara para armar el array de movs");

        for(int i=0;i<5;i++){

            if(!importes.get(ultimoIndiceImporte-(i*2)).text().equals("")){

                fila=filas.get(ultimoIndiceTabla-i).select("td");
                arrayMovimientos[i]=fila.get(0).text()+";"+fila.get(1).text()+";"+importes.get(ultimoIndiceImporte-(i*2)).text().toString();

            }else{
                fila=filas.get(ultimoIndiceTabla-i).select("td");
                arrayMovimientos[i]=fila.get(0).text()+";"+fila.get(1).text()+";"+"-"+importes.get(ultimoIndiceImporte-(i*2)-1).text().toString();
            }
        }
        Log.i("informacion",TextUtils.join(";",arrayMovimientos));
        return TextUtils.join(";", arrayMovimientos);
    }

}
