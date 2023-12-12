package org.jembi.jempi.libshared.models;

public record LinkInfo(
      String goldenUID,
      String interactionUID,
      String sourceUID,
      float score) {
}
