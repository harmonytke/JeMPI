package org.jembi.jempi.libshared.models;

import org.jembi.jempi.libconfig.shared.models.GoldenRecord;

public record ExternalLinkCandidate(
      GoldenRecord goldenRecord,
      float score) {
}
