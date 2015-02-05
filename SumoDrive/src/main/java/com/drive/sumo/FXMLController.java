package com.drive.sumo;

import it.polito.appeal.traci.SumoTraciConnection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;

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
            stc = new SumoTraciConnection(InetAddress.getLocalHost(), 1234);

            LaneMapper laneMapper = new LaneMapper(ImmutableMap.<String, Long>builder()
                    .put("1714", 1L)
                    .put("1632", 2L)
                    .put("1630", 3L)
                    .put("1608", 4L)
                    .put("996", 5L)
                    .put("612", 6L)
                    .put("608", 7L)
                    .put("532", 8L)
                    .put("220#0", 9L)
                    .put("220#1", 10L)
                    .put("220#2", 11L)
                    .put("222#0", 12L)
                    .put("222#1", 13L)
                    .put("224#0", 14L)
                    .put("224#1", 15L)
                    .put("226#0", 16L)
                    .put("226#1", 17L)
                    .put("226#2", 18L)
                    .put("228#0", 19L)
                    .put("228#1", 20L)
                    .put("230#0", 21L)
                    .put("230#1", 22L)
                    .put("232#0", 23L)
                    .put("232#1", 24L)
                    .put("232#2", 25L)
                    .put("234#0", 26L)
                    .put("234#1", 27L)
                    .put("236#0", 28L)
                    .put("236#1", 29L)
                    .put("238#0", 30L)
                    .put("238#1", 31L)
                    .put("238#2", 32L)
                    .put("256", 33L)
                    .put("258", 34L)
                    .put("260", 35L)
                    .put("262", 36L)
                    .put("276#0", 37L)
                    .put("276#1", 38L)
                    .put("288#0", 39L)
                    .put("288#1", 40L)
                    .put("1984", 41L)
                    .put("1986", 42L)
                    .put("1990", 43L)
                    .put("1992", 44L)
                    .put("1995#0", 45L)
                    .put("1995#1", 46L)
                    .put("1995#2", 47L)
                    .put("2017", 48L)
                    .put("2019#0", 49L)
                    .put("2019#1", 50L)
                    .put("2019#2", 51L)
                    .put("2019#3", 52L)
                    .put("2019#4", 53L)
                    .put("2021#0", 54L)
                    .put("2021#1", 55L)
                    .put("2021#2", 56L)
                    .put("2029#0", 57L)
                    .put("2029#1", 58L)
                    .put("2029#2", 59L)
                    .put("2031#0", 60L)
                    .put("2031#1", 61L)
                    .put("2031#2", 62L)
                    .put("2031#3", 63L)
                    .put("2031#4", 64L)
                    .put("2033", 65L)
                    .put("2035#0", 66L)
                    .put("2035#1", 67L)
                    .put("2035#2", 68L)
                    .put("2037", 69L)
                    .put("2039", 70L)
                    .put("2041", 71L)
                    .put("2043", 72L)
                    .put("2047", 73L)
                    .put("2049", 74L)
                    .put("2054", 75L)
                    .put("2056", 76L)
                    .put("2061", 77L)
                    .put("2063#0", 78L)
                    .put("2063#1", 79L)
                    .put("2063#2", 80L)
                    .put("2074#0", 81L)
                    .put("2074#1", 82L)
                    .put("2074#2", 83L)
                    .put("2074#3", 84L)
                    .put("2074#4", 85L)
                    .put("2076#0", 86L)
                    .put("2076#1", 87L)
                    .put("2076#2", 88L)
                    .put("2080#0", 89L)
                    .put("2080#1", 90L)
                    .put("2080#2", 91L)
                    .put("2082#0", 92L)
                    .put("2082#1", 93L)
                    .put("2082#2", 94L)
                    .put("2082#3", 95L)
                    .put("2082#4", 96L)
                    .put("2087#0", 97L)
                    .put("2087#1", 98L)
                    .put("2087#2", 99L)
                    .put("2089", 100L)
                    .put("2091", 101L)
                    .put("2093", 102L)
                    .put("2095", 103L)
                    .put("2097", 104L)
                    .build());
            MysqlDriver driver = new MysqlDriver();
            List<MeasurePoint> measurePoints = laneMapper.buildMeasurePoints(stc.getInductionLoopRepository(), driver.dataConsumer(stc));

            int cycleStart = currentTimeMs();
            while (currentTimeMs() < 1_000_000) {
                stc.nextSimStep(.1);
                for (MeasurePoint m : measurePoints) {
                    m.step();
                }
                if (currentTimeMs() - cycleStart >= 60_000) {
                	for (MeasurePoint m : measurePoints)
                		m.endCycle((currentTimeMs() - cycleStart) / 1_000);
                	invokeAlgo();
                	driver.loadSpeeds(laneMapper, stc);
                	cycleStart = currentTimeMs();
                }
            }

        } catch (IOException|InterruptedException|SQLException ex) {
        	ex.printStackTrace();
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
		}
    }

    private void invokeAlgo() throws IOException, InterruptedException {
    	Process p =
	    	Runtime.getRuntime().exec(new String[] {
	    			"/home/didier_m/eipw/server_core/build/algo_v2", "mysql://dbname=algo_demo2 user=root password=gangstaniggashit", "true", "--timestamp", convertTime(currentTimeMs()) + ""
	    			});
    	ByteStreams.copy(p.getInputStream(), System.out);
    	ByteStreams.copy(p.getErrorStream(), System.err);
    	if (p.waitFor() != 0)
    		throw new RuntimeException("ALGO FAILED");
    }

    private int currentTimeMs() throws IOException {
    	return stc.getSimulationData().queryCurrentSimTime().get();
    }

    public static int convertTime(int ms) {
    	return ms / 1000 + 10_000_000;
    }

}
