package me.replydev.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Keyboard {

    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static String s(){
        try {
            return reader.readLine();
        } catch (IOException e) {
            
        }
        return null;
    }

    public static String s(String message){
        Log.log(message);
        return s();
    }
}
