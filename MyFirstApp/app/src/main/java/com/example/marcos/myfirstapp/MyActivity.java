package com.example.marcos.myfirstapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.GregorianCalendar;

import java.io.IOException;

import static java.lang.String.*;


public class MyActivity extends AppCompatActivity {
    public static Integer num = 0;
    private String s=null;
    private TextView tv=null;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            num++;
            //Lo de abajo lo comente porque saque la TextView +@id/texto1
            //TextView tv2=(TextView) findViewById(R.id.texto1);
            //tv2.setText(Integer.toString(num));
        }
    };


    //NotificationCompat.Builder notification;
    private static final int uniqueID=45612;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        tv = (TextView) findViewById(R.id.text2);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        File tamali=new File(getApplicationContext().getFilesDir()+File.separator+"tamali.txt");

        if(tamali.exists()) {
            mostrarMovimientos(new TextView(this));
            stopAlarm(new TextView(this));
            iniciarAlarma(new TextView(this));
        }else{
            Intent intenti=new Intent(this,MySecondActivity.class);
            startActivity(intenti);
            finish();
        }

    }

    public void iniciarAlarma(View v){


        GregorianCalendar gregorianCalendar=new GregorianCalendar(2015,1,1,0,0);

        Intent alertIntent=new Intent(this,AlertReceiver.class);

        AlarmManager alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,gregorianCalendar.getTimeInMillis(),15*60*1000,PendingIntent.getBroadcast(this,1,alertIntent,PendingIntent.FLAG_UPDATE_CURRENT));
        tv.setText("se inicio la alarma");

    }

    public void stopAlarm(View v) {

        Intent alertIntent=new Intent(this,AlertReceiver.class);

        AlarmManager alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(this, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        tv.setText("se detuvo la alrma");
    }
    public void mostrarLoDelArchivo(View v){

        Log.i("informacion", "se presiono boton mostrar lo del archivo");
        DefaultStringWriterReader writerReader=new DefaultStringWriterReader();
        tv.setText(writerReader.read(this,"tamali.txt"));
    }


    public void mostrarSaldo(View v){
        tv.setText("obteniendo saldo ...");
        Log.i("informacion","obteniendo saldo");
        int[] key={2,4,0,3,-2,4,1,0,2,-1};
        Encriptador encriptador=new Encriptador(key);
        DefaultStringWriterReader writerReader=new DefaultStringWriterReader();
        String[] userNamePass = writerReader.read(this,"tamali.txt").split(",");
        String userName=userNamePass[0];
        String pass = encriptador.decrypt(userNamePass[1]);
        new DownloadWebpageTask().execute(userName, pass);

    }

    public void mostrarMovimientos(View v){
        tv.setText("obteniendo movimientos ...");
        int[] key={2,4,0,3,-2,4,1,0,2,-1};
        Encriptador encriptador=new Encriptador(key);
        DefaultStringWriterReader writerReader=new DefaultStringWriterReader();
        String[] userNamePass = writerReader.read(this,"tamali.txt").split(",");
        String userName=userNamePass[0];
        String pass = encriptador.decrypt(userNamePass[1]);
        new MostrarMoviemientos().execute(userName, pass);
    }








    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            return BrouScraper.obtenerSaldo(urls[0],urls[1]);
            //return urls[0]+","+urls[1];
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            if(result==null){
                tv.setText("No se pudo acceder a los datos");
            }else{
                tv.setText(result);
            }
        }
    }

    private class MostrarMoviemientos extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            return BrouScraper.obtenerMovimientos(urls[0], urls[1]);
            //return urls[0]+","+urls[1];
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            if(result==null){

            }else{
                String[] movimientos=result.split(";");
                if(movimientos.length<2){
                    tv.setText("No se pudo acceder a los datos");
                    return;
                }
                TableLayout tableLayout=(TableLayout) findViewById(R.id.tabla_movimientos);
                TableRow tableRow;
                TextView textView;

                for(int i=0;i<5;i++){
                    tableRow=(TableRow) tableLayout.getChildAt(i+1);
                    for(int j=0;j<3;j++) {
                        textView = (TextView) tableRow.getChildAt(j);
                        textView.setText(movimientos[(i * 3) + j]);
                    }
                }
            }
        }
    }

    public void archivoACero(View v){
        Log.i("informacion","se pone el saldo de archivo en cero");
        DefaultStringWriterReader defaultStringWriterReader=new DefaultStringWriterReader();
        defaultStringWriterReader.write(this, "0", "saldo.txt");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent segundaActividadIntent=new Intent(this,MySecondActivity.class);
            startActivity(segundaActividadIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "My Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.marcos.myfirstapp/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "My Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.marcos.myfirstapp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


}
