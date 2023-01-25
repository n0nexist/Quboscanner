package me.replydev.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import me.replydev.qubo.Info;

public class Log {
    public static void logln(String s){
        log(s + "\n");
    }
    static void log(String s){
        if(Info.gui) return;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.ITALIAN);
        LocalTime time = LocalTime.now();
        String f = formatter.format(time);
        System.out.print("\u001b[33m[\u001b[0m" + f + "\u001b[33m]\u001b[0m  - " + s);
    }

}
