/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drive.sumo;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import it.polito.appeal.traci.InductionLoop;
import it.polito.appeal.traci.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 *
 * @author sylva_000
 */
public class LaneMapper {

	private static final Pattern EDGE_ID_FROM_LANE_ID_REGEX =
			Pattern.compile("^-?(\\d+(#\\d+)?)(-Added(On|Off)RampEdge)?_\\d+$");
    private final ImmutableMap<String, Long> sumoToAlgoEdgeIdMap;

    public LaneMapper(ImmutableMap<String, Long> sumoToAlgoIdMap) {
        this.sumoToAlgoEdgeIdMap = sumoToAlgoIdMap;
    }

    public List<MeasurePoint>
            buildMeasurePoints(Repository<InductionLoop> repo,
                               Consumer<MeasurePoint.Data> dataConsumer,
                               int cycleLength)
            throws IOException {

    	Map<String, List<InductionLoop>> groups = repo.getAll().values().stream()
    		.collect(groupingBy(il -> {
    			String id = il.getID();
    			return id.substring(4, id.length() - 2);
    		}));

    	return groups.entrySet().stream()
    			.map(e -> new MeasurePoint(
	    				dataConsumer,
	    				ImmutableSet.copyOf(e.getValue()),
	    				cycleLength,
	    				sumoToAlgoEdgeIdMap.get(e.getKey()))
	    		).collect(toList());
    }

    public long edgeIdFrom(String laneId) {
    	Matcher m = EDGE_ID_FROM_LANE_ID_REGEX.matcher(laneId);
    	if (!m.matches())
    		return -1;
    	Long res = sumoToAlgoEdgeIdMap.get(m.group(1));
    	return (res != null) ? res : -1;
    }
}
