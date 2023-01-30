package me.replydev.mcping.net;

import com.google.gson.JsonSyntaxException;
import me.replydev.mcping.MCPing;
import me.replydev.mcping.PingOptions;
import me.replydev.mcping.data.FinalResponse;
import me.replydev.qubo.Filez;
import me.replydev.qubo.Info;
import me.replydev.qubo.QuboInstance;
import me.replydev.utils.Log;
import me.replydev.utils.Statuz;

import java.io.IOException;

public class Check implements Runnable{

    private final String hostname;
    private final int port;
    private final int timeout;
    private final int count;
    private final QuboInstance quboInstance;
    private final String filterVersion;
    private final String filterMotd;
    private final int minPlayer;

    public Check(String hostname, int port, int timeout, int count ,QuboInstance quboInstance,String filterVersion,String filterMotd,int minPlayer){
        this.hostname = hostname;
        this.port = port;
        this.timeout = timeout;
        this.count = count;
        this.quboInstance = quboInstance;
        this.filterVersion = filterVersion;
        this.filterMotd = filterMotd;
        this.minPlayer = minPlayer;
    }

    public void run(){
        check();
        this.quboInstance.currentThreads.decrementAndGet();
    }

    public static String purify(String s) {
        return s.replaceAll("\033\\[[\\d;]*[^\\d;]", "");
    }

    private void check()
    {
        if(hostname == null || filterVersion == null || filterMotd == null) return;
        
        for(int i = 0; i < count; i++)
        {
            try 
            {
                Statuz.status();
                try
                {
                    FinalResponse response = new MCPing().getPing(new PingOptions().setHostname(hostname).setPort(port).setTimeout(timeout));
                    String des = getGoodDescription(response.getDescription());
                    if(response != null){
                        Info.currentFound++;
                    if(response.getDescription().contains(filterMotd) && response.getVersion().getName().contains(filterVersion) && response.getPlayers().getOnline() > minPlayer)
                    {
                        new Thread(){
                            
                            String formattedline = "\033[31m(\033[0m" + hostname + ":" + port + "\033[31m)(\033[0m" + response.getPlayers().getOnline() + "/" + response.getPlayers().getMax() + "\033[31m)" + "(\033[0m" + response.getVersion().getName() + "\033[31m)" + "(\033[0m" + des + "\033[31m)\033[0m";
                            @Override
                            public void run(){
                                /* bot checker */
                                MinecraftServer minecraftServer = new MinecraftServer(
                                    hostname,
                                    port,
                                    Protocolz.getProtocol(response.getVersion().getName())
                                );
                                String result = "";
                                try {
                                    minecraftServer.connect();
                                    minecraftServer.sendHandshakePacket();
                                    minecraftServer.sendLoginStartPacket();
                                    result = minecraftServer.check();
                                    Thread.sleep(1000L);
                                    minecraftServer.disconnect();
                                } catch (Exception e) {
                                    result = "\033[31mFAILED \033[0m(\033[33mJAVA ERROR\033[0m)";
                                } 
                                formattedline+="\033[31m(\033[0m"+result+"\033[31m)\033[0m";
                                /* fine bot checker */
                                Info.serverFound++;
                                Info.serverNotFilteredFound++;
                                Log.logln(formattedline);
                                Filez.writeToFile(Filez.getCurrentPath(), purify(formattedline));
                            }
                        }.start();
                    }
                    
                    else Info.serverNotFilteredFound++;
                    return;
                    }
                }
                catch (JsonSyntaxException e)
                {
                    String notfound = "(" + hostname + ":" + port + ")(Json not readable)";
                    System.out.println(notfound);
                    Info.serverNotFilteredFound++;
                    Filez.writeToFile(Filez.getCurrentPath(), notfound);
                }
                catch (NullPointerException e)
                {
                    if(this.quboInstance.inputData.isDebugMode())
                        System.out.println("WARN: NullPointerException for: " + hostname + ":" + port);
                }
            } 	catch (IOException ignored) {}
        }
    }

    private static String getGoodDescription(String des){  //ritorna la stringa senza spazi multipli e senza §
        if(des == null) return "";
        des = des.replace("§0","");
        des = des.replace("§1","");
        des = des.replace("§2","");
        des = des.replace("§3","");
        des = des.replace("§4","");
        des = des.replace("§5","");
        des = des.replace("§6","");
        des = des.replace("§7","");
        des = des.replace("§8","");
        des = des.replace("§9","");
        des = des.replace("§a","");
        des = des.replace("§b","");
        des = des.replace("§c","");
        des = des.replace("§d","");
        des = des.replace("§e","");
        des = des.replace("§f","");
        des = des.replace("§l","");
        des = des.replace("§m","");
        des = des.replace("§n","");
        des = des.replace("§o","");
        des = des.replace("§r","");
        des= des.trim().replaceAll(" +", " "); //rimuove spazi multipli
        des = des.replace("\n","");
        return des;
    }
}