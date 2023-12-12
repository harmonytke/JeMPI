package org.jembi.jempi.libmpi.dgraph;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jembi.jempi.libconfig.dgraph.CustomDgraphInteraction;
import org.jembi.jempi.libshared.models.LibMPIPagination;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
record DgraphInteractions(
      @JsonProperty("all") List<CustomDgraphInteraction> all,
      @JsonProperty("pagination") List<LibMPIPagination> pagination) {
   DgraphInteractions(@JsonProperty("all") final List<CustomDgraphInteraction> all_) {
      this(all_, List.of(new LibMPIPagination(all_.size())));
   }
}
