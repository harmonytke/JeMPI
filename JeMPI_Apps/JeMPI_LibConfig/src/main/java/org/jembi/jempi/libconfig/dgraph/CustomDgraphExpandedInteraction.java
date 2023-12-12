package org.jembi.jempi.libconfig.dgraph;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jembi.jempi.libconfig.shared.models.CustomDemographicData;
import org.jembi.jempi.libconfig.shared.models.CustomUniqueInteractionData;
import org.jembi.jempi.libconfig.shared.models.ExpandedInteraction;
import org.jembi.jempi.libconfig.shared.models.Interaction;

import java.util.List;

import static org.jembi.jempi.libconfig.dgraph.CustomDgraphConstants.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CustomDgraphExpandedInteraction(
      @JsonProperty("uid") String interactionId,
      @JsonProperty("Interaction.source_id") DgraphSourceId sourceId,
      @JsonProperty(PREDICATE_INTERACTION_AUX_DATE_CREATED) java.time.LocalDateTime auxDateCreated,
      @JsonProperty(PREDICATE_INTERACTION_AUX_ID) String auxId,
      @JsonProperty(PREDICATE_INTERACTION_AUX_CLINICAL_DATA) String auxClinicalData,
      @JsonProperty(PREDICATE_INTERACTION_GIVEN_NAME) String givenName,
      @JsonProperty(PREDICATE_INTERACTION_FAMILY_NAME) String familyName,
      @JsonProperty(PREDICATE_INTERACTION_GENDER) String gender,
      @JsonProperty(PREDICATE_INTERACTION_DOB) String dob,
      @JsonProperty(PREDICATE_INTERACTION_CITY) String city,
      @JsonProperty(PREDICATE_INTERACTION_PHONE_NUMBER) String phoneNumber,
      @JsonProperty(PREDICATE_INTERACTION_NATIONAL_ID) String nationalId,
      @JsonProperty("~GoldenRecord.interactions") List<CustomDgraphReverseGoldenRecord> dgraphGoldenRecordList) {

   Interaction toInteraction() {
      return new Interaction(this.interactionId(),
                             this.sourceId().toSourceId(),
                             new CustomUniqueInteractionData(this.auxDateCreated(),
                                                             this.auxId(),
                                                             this.auxClinicalData()),
                             new CustomDemographicData(new String[]{this.givenName(),
                                                                    this.familyName(),
                                                                    this.gender(),
                                                                    this.dob(),
                                                                    this.city(),
                                                                    this.phoneNumber(),
                                                                    this.nationalId()}));
   }

   public ExpandedInteraction toExpandedInteraction() {
      return new ExpandedInteraction(this.toInteraction(),
                                     this.dgraphGoldenRecordList()
                                         .stream()
                                         .map(CustomDgraphReverseGoldenRecord::toGoldenRecordWithScore)
                                         .toList());
   }

}

