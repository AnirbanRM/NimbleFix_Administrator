package com.nimblefix.ControlMessages;

import java.awt.*;
import java.io.Serializable;
import java.net.Socket;

public class AuthenticationMessage implements Serializable {

    //Source Types
        public final static int Server = -1;
        public final static int Admin = 0;
        public final static int User = 1;
        public final static int Staff = 2;

    //MessageType
        public final static int Challenge = 3;
        public final static int Response = 4;

    int Source,MessageType;
    String Username,Password;
    String MESSAGEBODY;

    public AuthenticationMessage(int Source, int MessageType, String Username, String Password){
        this.Source = Source;
        this.MessageType = MessageType;
        this.Username = Username;
        this.Password = Password;
    }

    public void setMESSAGEBODY(String msgB){
        this.MESSAGEBODY=msgB;
    }

    public String getMESSAGEBODY(){
        return MESSAGEBODY;
    }

    public int getSource(){
        return Source;
    }

    public int getMessageType(){
        return MessageType;
    }

    public String getUser(){
        return Username;
    }

    public String getPassword(){
        return Password;
    }
}
