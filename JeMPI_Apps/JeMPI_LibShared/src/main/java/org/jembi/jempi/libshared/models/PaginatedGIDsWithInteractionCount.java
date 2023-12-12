package org.jembi.jempi.libshared.models;

import java.util.List;

public record PaginatedGIDsWithInteractionCount(
      List<String> data,
      LibMPIPagination pagination,
      LibMPIInteractionCount interactionCount) {
}
