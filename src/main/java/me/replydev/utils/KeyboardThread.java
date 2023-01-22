package me.replydev.utils;

import me.replydev.qubo.CLI;

public class KeyboardThread implements Runnable {

    @Override
    public void run() {
        while(true){
            try{
                System.out.print("\033[31m[\033[0mTHREADS: \033[33m"+CLI.getQuboInstance().getThreads()+"\033[31m]-[\033[0mCHECKING: \033[33m"+CLI.getQuboInstance().getCurrent()+"\033[31m]\r");
            }catch(Exception a){}
        }
    }
}
