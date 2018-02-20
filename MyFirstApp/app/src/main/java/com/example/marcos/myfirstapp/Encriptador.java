package com.example.marcos.myfirstapp;

/**
 * Created by marcos on 3/20/16.
 */
public class Encriptador {
    private int[] key;

    public Encriptador(int[] key){
        this.key=key;
    }

    public String encrypt(String texto){
        char[] chars=texto.toCharArray();

        for (int i=0;i<key.length;i++){
            try{
                chars[i]=(char) (chars[i]+key[i]);
            }catch(IndexOutOfBoundsException e){
            }
        }
        return new String(chars);
    }

    public String decrypt(String encriptado){
        char[] chars=encriptado.toCharArray();

        for (int i=0;i<key.length;i++){
            try{
                chars[i]=(char) (chars[i]-key[i]);
            }catch(IndexOutOfBoundsException e){
            }
        }
        return new String(chars);

    }
}
