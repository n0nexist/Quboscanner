package me.replydev.utils;

import me.replydev.qubo.CLI;
import me.replydev.qubo.GoodBye;
import me.replydev.qubo.Info;

public class Statuz {

    public static void status(){
        try{
            System.out.print("\033[36m[\033[31mTHREADS \033[32m> \033[0m"+CLI.getQuboInstance().getThreads()+"\033[36m |\033[0m "+CLI.getQuboInstance().getCurrent()+"\033[36m |\033[31m FOUND \033[32m> \033[0m"+Info.currentFound+"\033[36m]\033[0m      \r");
        }catch(Exception a){a.printStackTrace();GoodBye.quit(7);}
    }

}
