package me.replydev.mcping.net;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

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
    String bungeeHack = String.valueOf(String.valueOf(this.ip)) + "\0005.12.45.7\000ea3f3fc9-70c4-30f3-8605-12589135dd0d";
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
    String name = "__n0nexist__";
    writeVarInt(loginStart, name.length());
    loginStart.writeBytes(name);
    writeVarInt(this.out, bout.size());
    this.out.write(bout.toByteArray());
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
      return "\033[0;91mIPWhitelist\033[0m"; 
    if (disconnect.contains("Unable to authenticate."))
      return "\033[0;91mBungeeGuard\033[0m"; 
    if (disconnect.contains("Unknown data in login hostname, did you forget to enable BungeeCord in spigot.yml?"))
      return "\033[0;32mDisable IPFORWARD\033[0m"; 
    if (disconnect.contains("You are not white-listed on this server!"))
      return "\033[0;32mWhiteList\033[0m"; 
    if (disconnect.contains("You are not whitelisted on this server!"))
      return "\033[0;32mWhiteList\033[0m"; 
    switch (id) {
      case -1:
        result = "\033[0;91mDISCONNECT(STREAM CLOSED)\033[0m";
        return result;
      case 0:
        result = "\033[0;91mDISCONNECT\033[0m";
        return result;
      case 1:
        result = "\033[0;33mPREMIUM\033[0m";
        return result;
      case 2:
        result = "\033[0;32mSUCCESS\033[0m";
        return result;
      case 3:
        result = "\033[0;32mSUCCESS(Compressed)\033[0m";
        return result;
    } 
    result = "\033[0;34mUnknown [probably velocity] (" + id + ")\033[0m";
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