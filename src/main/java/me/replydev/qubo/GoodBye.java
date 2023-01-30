package me.replydev.qubo;

public class GoodBye {

    public static void quit(int code){
        /* comunica all'utente che stiamo uscendo */
        System.out.println("\033[3;31mEXITING WITH CODE \033[0;0m=>\033[33m "+code);
        System.out.println("\033[0;0m"); // resettiamo i colori e lo stile
        System.exit(code);
    }
    
}
