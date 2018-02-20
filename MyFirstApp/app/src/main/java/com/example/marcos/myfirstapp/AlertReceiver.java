package com.example.marcos.myfirstapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

/**
 * Created by marcos on 3/4/16.
 */
public class AlertReceiver extends BroadcastReceiver {
    NotificationCompat.Builder notification;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("informacion","onReceive");
        DefaultStringWriterReader writerReader=new DefaultStringWriterReader();
        String encryptedDuo=writerReader.read(context, "tamali.txt");
        int[] key={2,4,0,3,-2,4,1,0,2,-1};
        Encriptador encriptador=new Encriptador(key);
        String[] encryptedDuoArray=encryptedDuo.split(",");
        String documento=encryptedDuoArray[0];
        String clave=encriptador.decrypt(encryptedDuoArray[1]);
        DownloadWebpageTask downloadWebpageTask=new DownloadWebpageTask();
        downloadWebpageTask.setContext(context);
        downloadWebpageTask.execute(documento, clave);
        Log.i("informacion","onReceiveEnd");
    }

    private class NotificarMovimiento extends AsyncTask<String, Void, String> {
        private BrouScraper brouscraper;

        public NotificarMovimiento(BrouScraper brouscraper){
            this.brouscraper=brouscraper;
        }


        private Context context;
        public void setContext(Context context){
            this.context=context;
        }
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            return brouscraper.obtenerMovimientos(urls[0], urls[1]);
            //return urls[0]+","+urls[1];
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.i("informacion","creando notificacion");
            if(result==null){
               Log.i("informacion","No se pudo acceder a los datos");
            }else{
                String[] movimientos=result.split(";");
                if(movimientos.length<2){
                    Log.i("informacion",result);
                }else {
                    String ultimoMovimiento = movimientos[1] + " " + movimientos[2];
                    createNotification(context, ultimoMovimiento);
                }
            }
        }
    }


    private void createNotification(Context context, String texto) {
        Log.i("informacion", context.getClass().getName());
        notification=new NotificationCompat.Builder(context);
        notification.setAutoCancel(true);
        notification.setSmallIcon(R.drawable.ic_vpn_key_24dp);
        notification.setTicker("");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("Su saldo ha cambiado");
        notification.setContentText(texto);
        notification.setDefaults(Notification.DEFAULT_SOUND);


        Intent intent=new Intent(context,MyActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        NotificationManager notificationManager=(NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(45613, notification.build());
    }

    private void createNotificacionActualizar(Context context) {
        Log.i("informacion", context.getClass().getName());
        notification=new NotificationCompat.Builder(context);
        notification.setAutoCancel(true);
        notification.setSmallIcon(R.drawable.ic_vpn_key_24dp);
        notification.setTicker("");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("se pide actualizacion");
        notification.setContentText("por favor actualice sus datos en ebrou");
        notification.setDefaults(Notification.DEFAULT_SOUND);


        Intent intent=new Intent(context,MyActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        NotificationManager notificationManager=(NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(45613, notification.build());
    }




    public void accienesDeAlerta(Context context,String saldoWeb){
        Log.i("informacion","acciendesDeAlerta");
        DefaultStringWriterReader writerReader=new DefaultStringWriterReader();

        Log.i("informacion","se tenia en archivo un saldo de "+writerReader.read(context,"saldo.txt"));
        Log.i("informacion","el saldo web es"+saldoWeb);

        String saldoArchivo=writerReader.read(context, "saldo.txt");
        if(saldoWeb!=null) {
            if(saldoWeb.equals("se pide actualizacion de datos")){createNotificacionActualizar(context);}
            if(esNumero(saldoWeb)) {
                if (saldoArchivo.equals(saldoWeb)) {
                    Log.i("informacion", "el saldo no ha cambiado que es de " + saldoArchivo);
                } else {//cambio el saldo
                    Log.i("informacion","cambio el saldo");
                    writerReader.write(context, saldoWeb, "saldo.txt");
                    NotificarMovimiento notificarMovimiento=new NotificarMovimiento(new BrouScraper());
                    notificarMovimiento.setContext(context);
                    String encryptedDuo=writerReader.read(context, "tamali.txt");
                    int[] key={2,4,0,3,-2,4,1,0,2,-1};
                    Encriptador encriptador=new Encriptador(key);
                    String[] encryptedDuoArray=encryptedDuo.split(",");
                    String documento=encryptedDuoArray[0];
                    String clave=encriptador.decrypt(encryptedDuoArray[1]);
                    notificarMovimiento.execute(documento,clave);
                }
            }
        }

    }

    public boolean esNumero(String str){
        str=str.replace(".","").replace(",","");
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    public void accienesDePrimeraAlerta(Context context,String saldoWeb){
        Log.i("informacion","accionesDePrimeraAlerta");
        Log.i("informacion", "comenzamos con un saldo web de " + saldoWeb);
        DefaultStringWriterReader writerReader=new DefaultStringWriterReader();

        if(saldoWeb!=null&&esNumero(saldoWeb)) {
            writerReader.write(context, saldoWeb, "saldo.txt");
        }else{
            writerReader.write(context, "-1", "saldo.txt");
            Log.i("informacion","no se pudo escrapear el  saldo");
        }
        Log.i("informacion","Se ha escrito el saldo: "+writerReader.read(context,"saldo.txt"));


    }
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        private Context context;
        public void setContext(Context contexto){
            context=contexto;
        }
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            Log.i("informacion","urls: "+urls[0]+","+urls[1]);
            return BrouScraper.obtenerSaldo(urls[0],urls[1]);
            //return urls[0]+","+urls[1];
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.i("informacion","el resultado es "+result);
            File file=new File(context.getFilesDir() + File.separator +"saldo.txt");
            DefaultStringWriterReader defaultStringWriterReader=new DefaultStringWriterReader();

            if(file.exists()){
                String saldo=defaultStringWriterReader.read(context, "saldo.txt");
                if(!saldo.equals("-1")) {
                    accienesDeAlerta(context, result);
                }else{//saldo =-1
                    accienesDePrimeraAlerta(context,result);
                }
            }else{//no existe archivo saldo
                accienesDePrimeraAlerta(context,result);
            }
        }
    }
}
