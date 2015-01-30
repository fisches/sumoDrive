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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sylva_000
 */
public class MysqlDriver implements Consumer<MeasurePoint.Data> {

    private Connection connect = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private SumoTraciConnection stc = null;

    public MysqlDriver(SumoTraciConnection stc) {
        try {
            this.stc = stc;
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.jdbc.Driver");
            // Setup the connection with the DB
            connect = DriverManager
                    .getConnection("jdbc:mysql://localhost/demo", "root", "root");
            // Statements allow to issue SQL queries to the database
            statement = (Statement) connect.createStatement();
            // Result set get the result of the SQL query
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MysqlDriver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MysqlDriver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void accept(MeasurePoint.Data t) {
        try {
            String query = String.format("INSERT INTO edge_state SET edge_id=%s, vehicle_flow=%s, vehicle_flow_period=%s,"
                    + "occupancy=%s, time=FROM_UNIXTIME(%s)", t.osmId, t.vehicleCount, t.interval, t.occupancy, stc.getSimulationData().queryCurrentSimTime().get());
            statement.execute(query);
        } catch (IOException ex) {
            Logger.getLogger(MysqlDriver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MysqlDriver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
