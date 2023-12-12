package org.jembi.jempi.libshared.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LibMPIInteractionCount(@JsonProperty("total") Integer total) {
}
