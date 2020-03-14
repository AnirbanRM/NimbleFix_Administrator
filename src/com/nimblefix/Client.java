package com.nimblefix;

import com.nimblefix.ControlMessages.AuthenticationMessage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    String SERVER_ADDRESS;
    int PORT=2180;

    Socket clientSocket=null;
    ObjectInputStream READER=null;
    ObjectOutputStream WRITER=null;

    String clientID,password;

    Client(String IP_ADDRESS, String clientID, String password, int PORT) {
        this.clientID = clientID;
        this.password = password;
        try {
            this.SERVER_ADDRESS = IP_ADDRESS;
            this.PORT = PORT;
            clientSocket = new Socket(IP_ADDRESS, PORT);
            WRITER = new ObjectOutputStream(clientSocket.getOutputStream());
            READER = new ObjectInputStream(clientSocket.getInputStream());

            startReading();
        }catch (Exception e){ e.printStackTrace(); }
    }

    Client(String IP_ADDRESS, String clientID, String password) {
        this.clientID = clientID;
        this.password = password;

        try {
            this.SERVER_ADDRESS = IP_ADDRESS;
            clientSocket = new Socket(IP_ADDRESS, PORT);
            WRITER = new ObjectOutputStream(clientSocket.getOutputStream());
            READER = new ObjectInputStream(clientSocket.getInputStream());

            startReading();
        }catch(Exception e){ e.printStackTrace(); }
    }

    private void startReading() {
        while(true) {
            Object receivedObject=null;

            try {
                receivedObject = READER.readObject();
            } catch (Exception e) { }

            if(receivedObject instanceof com.nimblefix.ControlMessages.AuthenticationMessage){
                handleAuthentication((AuthenticationMessage) receivedObject);
            }
        }
    }

    private void handleAuthentication(AuthenticationMessage authmsg) {
        if(authmsg.getSource()==AuthenticationMessage.Server&&authmsg.getMessageType()==AuthenticationMessage.Challenge){
            authmsg = new AuthenticationMessage(AuthenticationMessage.Staff,AuthenticationMessage.Response,clientID,password);
            try{WRITER.writeObject(authmsg); authmsg = (AuthenticationMessage) READER.readObject(); }catch (Exception e){ }
            System.out.println(authmsg.getMESSAGEBODY());
        }
    }




}

