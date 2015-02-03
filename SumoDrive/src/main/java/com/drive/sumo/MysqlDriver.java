/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drive.sumo;

import it.polito.appeal.traci.SumoTraciConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sylva_000
 */
public class MysqlDriver {
    private Connection connection = null;

    public MysqlDriver() {
        try {
        	Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://localhost/demo", "root", "root");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MysqlDriver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MysqlDriver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Consumer<MeasurePoint.Data> dataConsumer(SumoTraciConnection stc) throws SQLException {
    	PreparedStatement stmt = connection.prepareStatement(
    			"INSERT INTO edge_state"
    			+ " SET edge_id=?, vehicle_flow=?, vehicle_flow_period=?,"
    			+ "     occupancy=?, time=FROM_UNIXTIME(?)");
    	return t -> {
    		try {
    			stmt.setLong(0, t.osmId);
    			stmt.setInt(1, t.vehicleCount);
    			stmt.setInt(2, (int)t.interval);
				stmt.setInt(3, (int)t.occupancy);
				stmt.setInt(4, stc.getSimulationData().queryCurrentSimTime().get());
				stmt.execute();
			} catch (Exception e) {
				if (e instanceof RuntimeException)
					throw (RuntimeException)e;
				else
					throw new RuntimeException(e);
			}
    	};
    }

    public void loadSpeeds(LaneMapper mapper, SumoTraciConnection stc) throws IOException, SQLException {
    	PreparedStatement stmt = connection.prepareStatement(
    			"SELECT speed FROM edge_speed WHERE edge_id = ?"
    			+ " AND time = (SELECT max(time) FROM edge_speed WHERE edge_id = ?)"
    			);
    	Function<Long, Double> retrieveSpeed = edgeId -> {
    		try {
				stmt.setLong(0, edgeId);
				stmt.setLong(1, edgeId);
				return stmt.executeQuery().getDouble(0);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
    	};
    	
    	Map<Long, Double> knownSpeeds = new HashMap<>();
    	stc.getLaneRepository().getAll().forEach((laneId, lane) -> {
    		long edgeId = mapper.edgeIdFrom(laneId);
    		lane.queryChangeMaxSpeed().setValue(
    				knownSpeeds.computeIfAbsent(edgeId, retrieveSpeed));
    	});
    }
}
