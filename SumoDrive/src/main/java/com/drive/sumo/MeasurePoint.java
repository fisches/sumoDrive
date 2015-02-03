/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drive.sumo;

import it.polito.appeal.traci.InductionLoop;
import it.polito.appeal.traci.StepAdvanceListener;
import it.polito.appeal.traci.Vehicle;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableSet;

/**
 *
 * @author sylva_000
 */
public class MeasurePoint implements StepAdvanceListener {

    private final Consumer<Data> dataConsumer;
    private final ImmutableSet<InductionLoop> loops;
    private final int cycleLength;
    private final long osmId;
    private final Set<Vehicle> vehiclesSeenLast = new HashSet<>();
    private int currentCycleStepCount;
    private double occupancyAccum;
    private int vehicleCountAccum;
    private double durationAccum;

    public static class Data {

        public final double interval, occupancy;
        public final int vehicleCount;
        public final long osmId;

        public Data(double interval,
                    double occupancy,
                    int vehicleCount,
                    long osmId) {
            this.interval = interval;
            this.occupancy = occupancy;
            this.vehicleCount = vehicleCount;
            this.osmId = osmId;
        }
    }

    public MeasurePoint(Consumer<Data> dataConsumer,
                        ImmutableSet<InductionLoop> loops,
                        int cycleLength,
                        long osmId) {
        this.dataConsumer = dataConsumer;
        this.loops = loops;
        this.cycleLength = cycleLength;
        this.osmId = osmId;
        resetCycle();
    }

    public MeasurePoint(Consumer<Data> dataConsumer,
                        int cycleLength,
                        Map.Entry<Long, Collection<InductionLoop>> entry) {
        this(dataConsumer,
             ImmutableSet.copyOf(entry.getValue()),
             cycleLength,
             entry.getKey());
    }

    public void step() throws IOException {
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

        if (++currentCycleStepCount == cycleLength) {
        	System.out.println(durationAccum);
            dataConsumer.accept(new Data(
                    durationAccum,
                    occupancyAccum / (cycleLength * loops.size()),
                    vehicleCountAccum,
                    osmId
            ));
            resetCycle();
        }
    }

    @Override
    public void nextStep(double d) {
        durationAccum += d;
    }

    private void resetCycle() {
        currentCycleStepCount = 0;
        occupancyAccum = 0;
        vehicleCountAccum = 0;
        durationAccum = 0;
    }

}
