/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drive.sumo;

import it.polito.appeal.traci.InductionLoop;
import it.polito.appeal.traci.SumoTraciConnection;
import it.polito.appeal.traci.Vehicle;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableSet;

/**
 *
 * @author sylva_000
 */
public class MeasurePoint {

    private final Consumer<Data> dataConsumer;
    private final ImmutableSet<InductionLoop> loops;
    private final int cycleLength;
    private final long algoEdgeId;
    private final Set<Vehicle> vehiclesSeenLast = new HashSet<>();
    private int currentCycleStepCount;
    private double occupancyAccum;
    private int vehicleCountAccum;
    private double cycleStartTime;
    private double currentTime;

    public static class Data {

        public final double interval, occupancy;
        public final int vehicleCount;
        public final long algoEdgeId;

        public Data(double interval,
                    double occupancy,
                    int vehicleCount,
                    long algoEdgeId) {
            this.interval = interval;
            this.occupancy = occupancy;
            this.vehicleCount = vehicleCount;
            this.algoEdgeId = algoEdgeId;
        }
    }

    public MeasurePoint(Consumer<Data> dataConsumer,
                        ImmutableSet<InductionLoop> loops,
                        int cycleLength,
                        long algoEdgeId) {
        this.dataConsumer = dataConsumer;
        this.loops = loops;
        this.cycleLength = cycleLength;
        this.algoEdgeId = algoEdgeId;
        resetCycle();
    }

    public void step(SumoTraciConnection stc) throws IOException {
    	currentTime = stc.getSimulationData().queryCurrentSimTime().get() / 1000;
        for (InductionLoop loop : loops) {
            if (loop.getOccupancy() > 0) {
                occupancyAccum += loop.getOccupancy();
            }
            for (Vehicle veh : loop.getLastStepVehicles()) {
                if (!vehiclesSeenLast.contains(veh)) {
                    vehicleCountAccum++;
                }
                vehiclesSeenLast.add(veh);
            }
        }

        vehiclesSeenLast.clear();
        for (InductionLoop loop : loops) {
            vehiclesSeenLast.addAll(loop.getLastStepVehicles());
        }
        ++currentCycleStepCount;

        if (currentTime - cycleStartTime >= cycleLength) {
            dataConsumer.accept(new Data(
                    currentTime - cycleStartTime,
                    occupancyAccum / (currentCycleStepCount * loops.size()),
                    vehicleCountAccum,
                    algoEdgeId
            ));
            resetCycle();
        }
    }

    private void resetCycle() {
        currentCycleStepCount = 0;
        occupancyAccum = 0;
        vehicleCountAccum = 0;
        cycleStartTime = currentTime;
    }

}
