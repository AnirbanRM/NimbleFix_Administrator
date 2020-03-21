package com.nimblefix;

import com.nimblefix.ControlMessages.ComplaintMessage;
import com.nimblefix.core.Organization;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import javax.jws.Oneway;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class Spectator implements Initializable {

    public Stage curr_stg;
    public Client client;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void startComplaintListener() {

        curr_stg.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if(client.currentShowingStage.equals(curr_stg)) {
                    try {
                        client.clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread receiverthd = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    Object o = null;
                    o = client.readNext();
                    if(o!=null){
                        final Object temp = o;
                        if(o instanceof ComplaintMessage){
                            Thread complaint_thd = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    handle_complaint((ComplaintMessage)temp);
                                }
                            });
                            complaint_thd.start();
                        }
                    }
                    else
                        break;
                }
            }
        });
        receiverthd.start();
    }

    private void handle_complaint(ComplaintMessage complaint) {

        System.out.println(complaint);

    }

    public void load(Organization organization) {


    }
}
