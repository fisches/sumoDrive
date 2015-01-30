package com.drive.sumo;

import com.google.common.collect.ImmutableMap;
import it.polito.appeal.traci.Lane;
import it.polito.appeal.traci.SumoTraciConnection;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class FXMLController implements Initializable {

    @FXML
    private Label label;

    private SumoTraciConnection stc = null;
    private Lane laneTmp = null;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            stc = new SumoTraciConnection(InetAddress.getLocalHost(), 1234);

            Map<String, Lane> lanes = stc.getLaneRepository().getAll();
            lanes.forEach((k, p) -> {
                System.out.println("p :" + p);
            });
            stc.nextSimStep();
            stc.nextSimStep();
            LaneMapper laneMapper = new LaneMapper(ImmutableMap.<String, Long>builder()
                    .put("ind_260", 1L)
                    .put("ind_1986", 2L)
                    .put("ind_256", 3L)
                    .put("ind_2049", 4L)
                    .put("ind_262", 5L)
                    .put("ind_2041", 6L)
                    .put("ind_258", 7L)
                    .put("ind_2095", 8L)
                    .put("ind_1995#0", 9L)
                    .put("ind_1995#2", 10L)
                    .put("ind_2021#0", 11L)
                    .put("ind_2021#2", 12L)
                    .put("ind_2063#0", 13L)
                    .put("ind_2063#2", 14L)
                    .put("ind_2076#0", 15L)
                    .put("ind_2076#2", 16L)
                    .put("ind_2035#0", 17L)
                    .put("ind_2035#2", 18L)
                    .put("ind_2029#0", 19L)
                    .put("ind_2029#2", 20L)
                    .put("ind_2087#0", 21L)
                    .put("ind_2087#2", 22L)
                    .put("ind_2080#0", 23L)
                    .put("ind_2080#2", 24L)
                    .put("ind_2031#0", 25L)
                    .put("ind_2031#4", 26L)
                    .put("ind_2019#0", 27L)
                    .put("ind_2019#4", 28L)
                    .put("ind_2082#0", 29L)
                    .put("ind_2082#4", 30L)
                    .put("ind_2074#0", 31L)
                    .put("ind_2074#4", 32L)
                    .build());
            List<MeasurePoint> measurePoints = laneMapper.buildMeasurePoints(stc.getInductionLoopRepository(), new MysqlDriver(stc), 60);
            measurePoints.forEach(stc::addStepAdvanceListener);

            for (int i = 0; i < 1000; i++) {
                stc.nextSimStep();
                for (MeasurePoint m : measurePoints) {
                    m.step();
                }
            }

        } catch (UnknownHostException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
