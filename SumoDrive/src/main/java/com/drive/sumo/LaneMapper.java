/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drive.sumo;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import it.polito.appeal.traci.InductionLoop;
import it.polito.appeal.traci.Repository;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 *
 * @author sylva_000
 */
public class LaneMapper {

    private final ImmutableMap<String, Long> sumoToAlgoEdgeIdMap;

    public LaneMapper(ImmutableMap<String, Long> sumoToAlgoIdMap) {
        this.sumoToAlgoEdgeIdMap = sumoToAlgoIdMap;
    }

    public List<MeasurePoint>
            buildMeasurePoints(Repository<InductionLoop> repo,
                               Consumer<MeasurePoint.Data> dataConsumer,
                               int cycleLength)
            throws IOException {

        SetMultimap<Long, InductionLoop> edgesIndLoops
                = MultimapBuilder.hashKeys().hashSetValues().build();
        repo.getAll().forEach((id, loop) -> {
            String edgeId = id.substring(0, id.length() - 2);
            edgesIndLoops.put(sumoToAlgoEdgeIdMap.get(edgeId), loop);
        });
        return edgesIndLoops.asMap().entrySet()
                .stream().map(entry
                        -> new MeasurePoint(dataConsumer, cycleLength, entry))
                .collect(Collectors.toList());
    }
}
