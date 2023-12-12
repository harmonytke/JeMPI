package org.jembi.jempi.libconfig.dgraph;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jembi.jempi.libconfig.shared.models.CustomDemographicData;
import org.jembi.jempi.libconfig.shared.models.CustomUniqueGoldenRecordData;
import org.jembi.jempi.libconfig.shared.models.GoldenRecord;

import java.util.List;

import static org.jembi.jempi.libconfig.dgraph.CustomDgraphConstants.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CustomDgraphGoldenRecord(
      @JsonProperty("uid") String goldenId,
      @JsonProperty("GoldenRecord.source_id") List<DgraphSourceId> sourceId,
      @JsonProperty(PREDICATE_GOLDEN_RECORD_AUX_DATE_CREATED) java.time.LocalDateTime auxDateCreated,
      @JsonProperty(PREDICATE_GOLDEN_RECORD_AUX_AUTO_UPDATE_ENABLED) Boolean auxAutoUpdateEnabled,
      @JsonProperty(PREDICATE_GOLDEN_RECORD_AUX_ID) String auxId,
      @JsonProperty(PREDICATE_GOLDEN_RECORD_GIVEN_NAME) String givenName,
      @JsonProperty(PREDICATE_GOLDEN_RECORD_FAMILY_NAME) String familyName,
      @JsonProperty(PREDICATE_GOLDEN_RECORD_GENDER) String gender,
      @JsonProperty(PREDICATE_GOLDEN_RECORD_DOB) String dob,
      @JsonProperty(PREDICATE_GOLDEN_RECORD_CITY) String city,
      @JsonProperty(PREDICATE_GOLDEN_RECORD_PHONE_NUMBER) String phoneNumber,
      @JsonProperty(PREDICATE_GOLDEN_RECORD_NATIONAL_ID) String nationalId) {

   public GoldenRecord toGoldenRecord() {
      return new GoldenRecord(this.goldenId(),
                              this.sourceId() != null
                                    ? this.sourceId().stream().map(DgraphSourceId::toSourceId).toList()
                                    : List.of(),
                              new CustomUniqueGoldenRecordData(this.auxDateCreated(),
                                                               this.auxAutoUpdateEnabled(),
                                                               this.auxId()),
                              new CustomDemographicData(new String[]{this.givenName(),
                                                                     this.familyName(),
                                                                     this.gender(),
                                                                     this.dob(),
                                                                     this.city(),
                                                                     this.phoneNumber(),
                                                                     this.nationalId()}));
   }

}

