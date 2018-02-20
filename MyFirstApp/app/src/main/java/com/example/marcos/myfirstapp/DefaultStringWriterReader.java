package com.example.marcos.myfirstapp;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by marcos on 3/20/16.
 */
//en este proyecto voy a usar el archivo tamali.txt
public class DefaultStringWriterReader {
    public void write(Context context,String s,String file){
        Log.i("informacion", "escribo en " + context.getFilesDir().getPath() + File.separator + file);
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(new
                    File(context.getFilesDir()+File.separator+file)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            //si no existia archivo lo crea "automaticamente"
            bufferedWriter.write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String read(Context context, String file){

        Log.i("informacion", "leo en " + context.getFilesDir().getPath() + File.separator + file);
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(new
                    File(context.getFilesDir()+File.separator+file)));
        } catch (FileNotFoundException e) {
            Log.i("informacion","no se encontro archivo a leer");
            return null;
        }
        String read;
        StringBuilder builder = new StringBuilder("");

        try {
            while((read = bufferedReader.readLine()) != null){
                builder.append(read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String str=builder.toString();
        Log.i("informacion","el archivo contiene: "+str);

        return str;
    }
}
