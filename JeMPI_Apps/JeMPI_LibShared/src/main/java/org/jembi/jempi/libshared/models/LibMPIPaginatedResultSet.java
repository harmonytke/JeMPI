package org.jembi.jempi.libshared.models;

import java.util.List;

public record LibMPIPaginatedResultSet<T>(
      List<T> data,
      LibMPIPagination pagination) {
}
