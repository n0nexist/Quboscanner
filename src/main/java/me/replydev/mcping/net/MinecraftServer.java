package me.replydev.mcping.net;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;
import java.util.UUID;

public class MinecraftServer {
  private String ip;
  
  private int port;
    
  private int protocol;
  
  private Socket socket;
  
  private DataOutputStream out;
  
  private DataInputStream in;
  
  public MinecraftServer(String server_ip, int server_port, int server_protocol) {
    this.ip = server_ip;
    this.port = server_port;
    this.protocol = server_protocol;
  }

  public static String getUUID() {
    return UUID.randomUUID().toString().toLowerCase();
  }

  public static String getRandIP() {
    Random rand = new Random();
    StringBuilder sb = new StringBuilder();
    sb.append(rand.nextInt(100)).append(".");
    sb.append(rand.nextInt(100)).append(".");
    sb.append(rand.nextInt(100)).append(".");
    sb.append(rand.nextInt(100));
    return sb.toString();
  }

  public static String getRandName() {
    Random rand = new Random();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 5; i++) {
        char c = (char)(rand.nextInt(26) + 'a');
        if (rand.nextInt(2) == 0) {
            c = Character.toUpperCase(c);
        }
        sb.append(c);
    }
    return sb.toString();
}


  
  public void connect() throws IOException {
    this.socket = new Socket();
    this.socket.connect(new InetSocketAddress(this.ip, this.port), 15000);
    this.out = new DataOutputStream(this.socket.getOutputStream());
    this.in = new DataInputStream(this.socket.getInputStream());
  }
  
  public void sendHandshakePacket() throws IOException {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    DataOutputStream handshake = new DataOutputStream(bout);
    handshake.writeByte(0);
    writeVarInt(handshake, this.protocol);
    String bungeeHack = String.valueOf(String.valueOf(this.ip)) + "\000"+getRandIP()+"\000"+getUUID();
    writeVarInt(handshake, bungeeHack.length());
    handshake.writeBytes(bungeeHack);
    handshake.writeShort(this.port);
    writeVarInt(handshake, 2);
    writeVarInt(this.out, bout.size());
    this.out.write(bout.toByteArray());
  }
  
  public void sendLoginStartPacket() throws IOException {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    DataOutputStream loginStart = new DataOutputStream(bout);
    loginStart.writeByte(0);
    String name = getRandName();
    writeVarInt(loginStart, name.length());
    loginStart.writeBytes(name);
    writeVarInt(this.out, bout.size());
    this.out.write(bout.toByteArray());
  }

  public String parseDisconnect(String text){
    /* componi il messaggio di disconnect */
    text = text.substring(2, text.length() - 2);
    text = text.replaceAll("\":\"", "\033[33m => \033[36m")
    .replaceAll("\",\"", "\033[33m => \033[36m");
    return text;
  }
  
  public String check() throws IOException {
    readVarInt(this.in);
    int id = readVarInt(this.in);
    String result = "";
    String disconnect = "";
    if (id == 0) {
      byte[] b = new byte[readVarInt(this.in)];
      this.in.readFully(b);
      disconnect = new String(b);
    } 
    if (disconnect.contains("You have to join through the proxy."))
      return "\033[31mIPWhitelist\033[0m"; 
    else if (disconnect.contains("Unable to authenticate."))
      return "\033[31mBungeeGuard\033[0m"; 
    else if (disconnect.contains("Unknown data in login hostname, did you forget to enable BungeeCord in spigot.yml?"))
      return "\033[32mDisable IPFORWARD\033[0m"; 
    else if (disconnect.contains("You are not white-listed on this server!"))
      return "\033[33mWhiteList\033[0m"; 
    else if (disconnect.contains("You are not whitelisted on this server!"))
      return "\033[33mWhiteList\033[0m"; 
    else if (disconnect.toLowerCase().contains("bungeeguard"))
      return "\033[33mprobably BungeeGuard\033[0m";
    else if (disconnect.toLowerCase().contains("must connect with"))
      return "\033[33mprobably OnlyProxyJoin\033[0m";
    else if (disconnect.toLowerCase().contains("connect with velocity"))
      return "\033[33mprobably Velocity\033[0m";  
    else if (disconnect.contains("This server has mods that require FML/Forge to be installed on the client. Contact your server admin for more details."))
      return "\033[33mForge\033[0m";
    else if (
      disconnect.toLowerCase().contains("forge")||
      disconnect.toLowerCase().contains("fml")||
      disconnect.toLowerCase().contains("mod")||
      disconnect.toLowerCase().contains("magma")
    ) 
      return "\033[33mprobably Forge\033[0m";  
    else if (
      disconnect.toLowerCase().contains("uuid spoofing detected")
    ) 
      return "\033[32mexploitfixer, can bypass\033[0m"; 
    switch (id) {
      case -1:
        result = "\033[91mDISCONNECT \033[0m(\033[91mCONNECTION TERMINATED\033[0m)";
        return result;
      case 0:
        result = parseDisconnect(disconnect);
        return result;
      case 1:
        result = "\033[33mPROBABLY PREMIUM\033[0m";
        return result;
      case 2:
        result = "\033[32mCONNECTED\033[0m";
        return result;
      case 3:
        result = "\033[32mCONNECTED \033[36m=>\033[32m probably bungeehack\033[0m";
        return result;
    } 
    result = "\033[34mUnknown [probably velocity] (" + id + ")\033[0m";
    return result;
  }
  
  public void disconnect() throws IOException {
    if (this.socket.isClosed())
      return; 
    this.socket.close();
  }
  
  public String getIp() {
    return String.valueOf(String.valueOf(this.ip)) + ":" + this.port;
  }
  
  
  public void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
    while (true) {
      if ((paramInt & 0xFFFFFF80) == 0) {
        out.writeByte(paramInt);
        return;
      } 
      out.writeByte(paramInt & 0x7F | 0x80);
      paramInt >>>= 7;
    } 
  }
  
  public int readVarInt(DataInputStream in) throws IOException {
    int i = 0;
    int j = 0;
    while (true) {
      byte k = in.readByte();
      i |= (k & Byte.MAX_VALUE) << j++ * 7;
      if (j <= 5) {
        if ((k & 0x80) != 128)
          return i; 
        continue;
      } 
      throw new RuntimeException("VarInt too big");
    } 
  }
}