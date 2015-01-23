package com.drive.sumo;

import it.polito.appeal.traci.Lane;
import it.polito.appeal.traci.SumoTraciConnection;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javax.sound.midi.SysexMessage;

public class FXMLController implements Initializable {
    
    @FXML
    private Label label;
    
    private SumoTraciConnection stc = null;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
        
       
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            stc = new SumoTraciConnection("/Users/florianoliver/projects/sumo/stg/stg.sumo.cfg", 12345);
            stc.runServer();
            stc.nextSimStep();
            Map<String, Lane> lanes = stc.getLaneRepository().getAll();
            lanes.forEach((k, p) -> {
                System.out.println("p :" + p);
            });
        } catch (UnknownHostException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
}
