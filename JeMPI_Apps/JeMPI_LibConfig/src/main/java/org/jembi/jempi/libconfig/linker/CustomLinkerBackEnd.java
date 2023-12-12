package org.jembi.jempi.libconfig.linker;

import org.jembi.jempi.libconfig.shared.models.CustomDemographicData;
import org.jembi.jempi.libconfig.shared.models.Interaction;

public final class CustomLinkerBackEnd {

   private CustomLinkerBackEnd() {
   }

   public static Interaction applyAutoCreateFunctions(final Interaction interaction) {
      return new Interaction(interaction.interactionId(),
                             interaction.sourceId(),
                             interaction.uniqueInteractionData(),
                             new CustomDemographicData(interaction.demographicData().fields));
   }

}
