package com.nimblefix;

import com.nimblefix.ControlMessages.AuthenticationMessage;
import com.sun.org.apache.regexp.internal.RE;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    String SERVER_ADDRESS;
    int PORT=2180;

    Stage currentShowingStage=null;

    Socket clientSocket=null;
    ObjectInputStream READER=null;
    ObjectOutputStream WRITER=null;

    String clientID,password;

    Client(String IP_ADDRESS, String clientID, String password, int PORT,Stage currentShowingStage) {
        this.currentShowingStage = currentShowingStage;
        this.clientID = clientID;
        this.password = password;
        try {
            this.SERVER_ADDRESS = IP_ADDRESS;
            this.PORT = PORT;
            clientSocket = new Socket(IP_ADDRESS, PORT);
            WRITER = new ObjectOutputStream(clientSocket.getOutputStream());
            READER = new ObjectInputStream(clientSocket.getInputStream());

            Object o = readNext();
            if(o instanceof com.nimblefix.ControlMessages.AuthenticationMessage){
                handleAuthentication((AuthenticationMessage) o);
            }
        }catch (Exception e){ e.printStackTrace(); }
    }

    Client(String IP_ADDRESS, String clientID, String password, Stage currentShowingStage) {
        this.currentShowingStage = currentShowingStage;
        this.clientID = clientID;
        this.password = password;

        try {
            this.SERVER_ADDRESS = IP_ADDRESS;
            clientSocket = new Socket(IP_ADDRESS, PORT);
            WRITER = new ObjectOutputStream(clientSocket.getOutputStream());
            READER = new ObjectInputStream(clientSocket.getInputStream());

            Object o = readNext();
            if(o instanceof com.nimblefix.ControlMessages.AuthenticationMessage){
                handleAuthentication((AuthenticationMessage) o);
            }
        }catch(Exception e){ e.printStackTrace(); }
    }

    public Stage getCurrentShowingStage() {
        return currentShowingStage;
    }

    public void setCurrentShowingStage(Stage currentShowingStage) {
        this.currentShowingStage = currentShowingStage;
    }

    public Object readNext() {
        Object receivedObject=null;
        try {
            receivedObject = READER.readUnshared();
        } catch (Exception e) { e.printStackTrace(); }
        return receivedObject;
    }

    private void handleAuthentication(AuthenticationMessage authmsg) {
        if(authmsg.getSource()==AuthenticationMessage.Server&&authmsg.getMessageType()==AuthenticationMessage.Challenge){
            authmsg = new AuthenticationMessage(AuthenticationMessage.Staff,AuthenticationMessage.Response,clientID,password);
            try{WRITER.writeUnshared(authmsg); authmsg = (AuthenticationMessage) READER.readUnshared(); }catch (Exception e){ }
            if(authmsg.getMESSAGEBODY().equals("SUCCESS"))
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            openDashboard();
                        }catch (Exception e){ }
                    }
                });
            else if(authmsg.getMESSAGEBODY().equals("FAILURE"))
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        informAndClose();
                    }
                });
        }
    }

    private void informAndClose() {
        Alert a = new Alert(Alert.AlertType.ERROR ,null, ButtonType.OK);
        a.setHeaderText("Invalid Credential ! Please try again.");
        a.setTitle("Authentication Failure");
        a.showAndWait();
        try {
            this.finalize();
        }catch (Throwable e){}
    }

    private void openDashboard() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DashboardUI.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Dashboard");
        stage.setResizable(false);
        stage.setScene(new Scene(root, 800, 640));
        ((Dashboard)loader.getController()).curr_stg=stage;

        StringBuilder sb = new StringBuilder();
        for(char i : clientSocket.getRemoteSocketAddress().toString().substring(1).toCharArray())
            if(i==':')break;
            else
                sb.append(i);

        ((Dashboard)loader.getController()).setAddressandUserandClient(sb.toString(),clientID,this);

        currentShowingStage.hide();
        currentShowingStage=stage;

        stage.show();
    }

}