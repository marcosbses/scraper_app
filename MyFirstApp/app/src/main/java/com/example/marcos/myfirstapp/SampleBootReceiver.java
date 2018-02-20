package com.example.marcos.myfirstapp;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.GregorianCalendar;

/**
 * Created by marcos on 7/20/16.
 */
public class SampleBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("informacion", "se booteo");
        Intent intenti=new Intent(context,MyActivity.class);
        context.startActivity(intenti);
        //iniciarAlarma(context);
    }


    public void iniciarAlarma(Context context){


        GregorianCalendar gregorianCalendar=new GregorianCalendar(2015,1,1,0,0);

        Intent alertIntent=new Intent(context,AlertReceiver.class);

        AlarmManager alarmManager=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,gregorianCalendar.getTimeInMillis(),15*60*1000,PendingIntent.getBroadcast(context,1,alertIntent,PendingIntent.FLAG_UPDATE_CURRENT));

    }
}
