package me.replydev.utils;

import me.replydev.qubo.CLI;
import me.replydev.qubo.GoodBye;

public class Statuz {

    public static void status(){
        try{
            System.out.print("\033[31m[\033[0mTHREADS: \033[33m"+CLI.getQuboInstance().getThreads()+"\033[31m]-[\033[0mCURRENT: \033[33m"+CLI.getQuboInstance().getCurrent()+"\033[31m]     \r");
        }catch(Exception a){a.printStackTrace();GoodBye.quit(7);}
    }

}
