package org.jembi.jempi.libconfig.shared.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CustomUniqueGoldenRecordData(
      LocalDateTime auxDateCreated,
      Boolean auxAutoUpdateEnabled,
      String auxId) {

   public CustomUniqueGoldenRecordData(final CustomUniqueInteractionData uniqueInteractionData) {
      this(LocalDateTime.now(),
           true,
           uniqueInteractionData.auxId()
          );
   }

}
