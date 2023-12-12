package org.jembi.jempi.libconfig.dgraph;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jembi.jempi.libconfig.shared.models.CustomDemographicData;
import org.jembi.jempi.libconfig.shared.models.CustomUniqueGoldenRecordData;
import org.jembi.jempi.libconfig.shared.models.GoldenRecord;
import org.jembi.jempi.libconfig.shared.models.GoldenRecordWithScore;

import java.util.List;

import static org.jembi.jempi.libconfig.dgraph.CustomDgraphConstants.*;
import static org.jembi.jempi.libconfig.shared.models.CustomDemographicData.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
record CustomDgraphReverseGoldenRecord(
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
      @JsonProperty(PREDICATE_GOLDEN_RECORD_NATIONAL_ID) String nationalId,
      @JsonProperty("~GoldenRecord.interactions|score") Float score) {

   GoldenRecord toGoldenRecord() {
      return new GoldenRecord(this.goldenId(),
                              this.sourceId() != null
                                    ? this.sourceId().stream().map(DgraphSourceId::toSourceId).toList()
                                    : List.of(),
                              new CustomUniqueGoldenRecordData(this.auxDateCreated(),
                                                               this.auxAutoUpdateEnabled(),
                                                               this.auxId()),
                              new CustomDemographicData(new Field[]{
                                    new Field(GIVEN_NAME_IDX, FIELD_NAMES[GIVEN_NAME_IDX], this.givenName()),
                                    new Field(FAMILY_NAME_IDX, FIELD_NAMES[FAMILY_NAME_IDX], this.familyName()),
                                    new Field(GENDER_IDX, FIELD_NAMES[GENDER_IDX], this.gender()),
                                    new Field(DOB_IDX, FIELD_NAMES[DOB_IDX], this.dob()),
                                    new Field(CITY_IDX, FIELD_NAMES[CITY_IDX], this.city()),
                                    new Field(PHONE_NUMBER_IDX, FIELD_NAMES[PHONE_NUMBER_IDX], this.phoneNumber()),
                                    new Field(NATIONAL_ID_IDX, FIELD_NAMES[NATIONAL_ID_IDX], this.nationalId())}));
   }

   GoldenRecordWithScore toGoldenRecordWithScore() {
      return new GoldenRecordWithScore(toGoldenRecord(), score);
   }

}

