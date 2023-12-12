package org.jembi.jempi.libmpi.dgraph;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.jembi.jempi.libconfig.dgraph.DgraphSourceId;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
record DgraphSourceIds(@JsonProperty("all") List<DgraphSourceId> all) {
}
