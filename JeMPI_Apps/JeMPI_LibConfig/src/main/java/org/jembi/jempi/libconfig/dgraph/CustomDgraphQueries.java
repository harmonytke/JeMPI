package org.jembi.jempi.libconfig.dgraph;

import org.apache.commons.lang3.StringUtils;
import org.jembi.jempi.libconfig.shared.models.CustomDemographicData;
import org.jembi.jempi.libconfig.shared.utils.AppUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static org.jembi.jempi.libconfig.shared.models.CustomDemographicData.*;

public final class CustomDgraphQueries {

   static final String EMPTY_FIELD_SENTINEL = "EMPTY_FIELD_SENTINEL";
   static final List<
         BiFunction<BiFunction<String, Map<String, String>, DgraphGoldenRecords>,
               CustomDemographicData,
               DgraphGoldenRecords>> DETERMINISTIC_MATCH_FUNCTIONS =
         List.of();

   static final List<
         BiFunction<
               BiFunction<
                     String, Map<String, String>, DgraphGoldenRecords>,
               CustomDemographicData,
               DgraphGoldenRecords>> DETERMINISTIC_LINK_FUNCTIONS =
         List.of(CustomDgraphQueries::queryLinkDeterministicA,
                 CustomDgraphQueries::queryLinkDeterministicB);

   private static final String QUERY_LINK_DETERMINISTIC_A =
         """
         query query_link_deterministic_a($national_id: string) {
            all(func:type(GoldenRecord)) @filter(eq(GoldenRecord.national_id, $national_id)) {
               uid
               GoldenRecord.source_id {
                  uid
               }
               GoldenRecord.aux_date_created
               GoldenRecord.aux_auto_update_enabled
               GoldenRecord.aux_id
               GoldenRecord.given_name
               GoldenRecord.family_name
               GoldenRecord.gender
               GoldenRecord.dob
               GoldenRecord.city
               GoldenRecord.phone_number
               GoldenRecord.national_id
            }
         }
         """;
   private static final String QUERY_LINK_DETERMINISTIC_B =
         """
         query query_link_deterministic_b($given_name: string, $family_name: string, $phone_number: string) {
            var(func:type(GoldenRecord)) @filter(eq(GoldenRecord.given_name, $given_name)) {
               A as uid
            }
            var(func:type(GoldenRecord)) @filter(eq(GoldenRecord.family_name, $family_name)) {
               B as uid
            }
            var(func:type(GoldenRecord)) @filter(eq(GoldenRecord.phone_number, $phone_number)) {
               C as uid
            }
            all(func:type(GoldenRecord)) @filter(uid(A) AND uid(B) AND uid(C)) {
               uid
               GoldenRecord.source_id {
                  uid
               }
               GoldenRecord.aux_date_created
               GoldenRecord.aux_auto_update_enabled
               GoldenRecord.aux_id
               GoldenRecord.given_name
               GoldenRecord.family_name
               GoldenRecord.gender
               GoldenRecord.dob
               GoldenRecord.city
               GoldenRecord.phone_number
               GoldenRecord.national_id
            }
         }
         """;
   private static final String QUERY_LINK_PROBABILISTIC =
         """
         query query_link_probabilistic($given_name: string, $family_name: string, $city: string, $phone_number: string, $national_id: string) {
            var(func:type(GoldenRecord)) @filter(match(GoldenRecord.given_name, $given_name, 3)) {
               A as uid
            }
            var(func:type(GoldenRecord)) @filter(match(GoldenRecord.family_name, $family_name, 3)) {
               B as uid
            }
            var(func:type(GoldenRecord)) @filter(match(GoldenRecord.city, $city, 3)) {
               C as uid
            }
            var(func:type(GoldenRecord)) @filter(match(GoldenRecord.phone_number, $phone_number, 2)) {
               D as uid
            }
            var(func:type(GoldenRecord)) @filter(match(GoldenRecord.national_id, $national_id, 3)) {
               E as uid
            }
            all(func:type(GoldenRecord)) @filter(((uid(A) AND uid(B)) OR (uid(A) AND uid(C)) OR (uid(B) AND uid(C))) OR uid(D) OR uid(E)) {
               uid
               GoldenRecord.source_id {
                  uid
               }
               GoldenRecord.aux_date_created
               GoldenRecord.aux_auto_update_enabled
               GoldenRecord.aux_id
               GoldenRecord.given_name
               GoldenRecord.family_name
               GoldenRecord.gender
               GoldenRecord.dob
               GoldenRecord.city
               GoldenRecord.phone_number
               GoldenRecord.national_id
            }
         }
         """;

   private CustomDgraphQueries() {
   }

   private static DgraphGoldenRecords queryLinkDeterministicA(
         final BiFunction<String, Map<String, String>, DgraphGoldenRecords> runGoldenRecordsQuery,
         final CustomDemographicData demographicData) {
      if (StringUtils.isBlank(demographicData.fields[NATIONAL_ID_IDX].value())) {
         return new DgraphGoldenRecords(List.of());
      }
      final Map<String, String> map = Map.of("$national_id", demographicData.fields[NATIONAL_ID_IDX].value());
      return runGoldenRecordsQuery.apply(QUERY_LINK_DETERMINISTIC_A, map);
   }

   private static DgraphGoldenRecords queryLinkDeterministicB(
         final BiFunction<String, Map<String, String>, DgraphGoldenRecords> runGoldenRecordsQuery,
         final CustomDemographicData demographicData) {
      final var givenName = demographicData.fields[GIVEN_NAME_IDX].value();
      final var familyName = demographicData.fields[FAMILY_NAME_IDX].value();
      final var phoneNumber = demographicData.fields[PHONE_NUMBER_IDX].value();
      final var givenNameIsBlank = StringUtils.isBlank(givenName);
      final var familyNameIsBlank = StringUtils.isBlank(familyName);
      final var phoneNumberIsBlank = StringUtils.isBlank(phoneNumber);
      if ((givenNameIsBlank || familyNameIsBlank || phoneNumberIsBlank)) {
         return new DgraphGoldenRecords(List.of());
      }
      final var map = Map.of("$given_name",
                             StringUtils.isNotBlank(givenName)
                                   ? givenName
                                   : EMPTY_FIELD_SENTINEL,
                             "$family_name",
                             StringUtils.isNotBlank(familyName)
                                   ? familyName
                                   : EMPTY_FIELD_SENTINEL,
                             "$phone_number",
                             StringUtils.isNotBlank(phoneNumber)
                                   ? phoneNumber
                                   : EMPTY_FIELD_SENTINEL);
      return runGoldenRecordsQuery.apply(QUERY_LINK_DETERMINISTIC_B, map);
   }

   private static DgraphGoldenRecords queryLinkProbabilistic(
         final BiFunction<String, Map<String, String>, DgraphGoldenRecords> runGoldenRecordsQuery,
         final CustomDemographicData demographicData) {
      final var givenName = demographicData.fields[GIVEN_NAME_IDX].value();
      final var familyName = demographicData.fields[FAMILY_NAME_IDX].value();
      final var city = demographicData.fields[CITY_IDX].value();
      final var phoneNumber = demographicData.fields[PHONE_NUMBER_IDX].value();
      final var nationalId = demographicData.fields[NATIONAL_ID_IDX].value();
      final var givenNameIsBlank = StringUtils.isBlank(givenName);
      final var familyNameIsBlank = StringUtils.isBlank(familyName);
      final var cityIsBlank = StringUtils.isBlank(city);
      final var phoneNumberIsBlank = StringUtils.isBlank(phoneNumber);
      final var nationalIdIsBlank = StringUtils.isBlank(nationalId);
      if ((((givenNameIsBlank || familyNameIsBlank) && (givenNameIsBlank || cityIsBlank) && (familyNameIsBlank || cityIsBlank)) && phoneNumberIsBlank && nationalIdIsBlank)) {
         return new DgraphGoldenRecords(List.of());
      }
      final var map = Map.of("$given_name",
                             StringUtils.isNotBlank(givenName)
                                   ? givenName
                                   : EMPTY_FIELD_SENTINEL,
                             "$family_name",
                             StringUtils.isNotBlank(familyName)
                                   ? familyName
                                   : EMPTY_FIELD_SENTINEL,
                             "$city",
                             StringUtils.isNotBlank(city)
                                   ? city
                                   : EMPTY_FIELD_SENTINEL,
                             "$phone_number",
                             StringUtils.isNotBlank(phoneNumber)
                                   ? phoneNumber
                                   : EMPTY_FIELD_SENTINEL,
                             "$national_id",
                             StringUtils.isNotBlank(nationalId)
                                   ? nationalId
                                   : EMPTY_FIELD_SENTINEL);
      return runGoldenRecordsQuery.apply(QUERY_LINK_PROBABILISTIC, map);
   }

   private static void mergeCandidates(
         final List<CustomDgraphGoldenRecord> goldenRecords,
         final DgraphGoldenRecords block) {
      final var candidates = block.all();
      if (!candidates.isEmpty()) {
         candidates.forEach(candidate -> {
            var found = false;
            for (CustomDgraphGoldenRecord goldenRecord : goldenRecords) {
               if (candidate.goldenId().equals(goldenRecord.goldenId())) {
                  found = true;
                  break;
               }
            }
            if (!found) {
               goldenRecords.add(candidate);
            }
         });
      }
   }

   static LinkedList<CustomDgraphGoldenRecord> deterministicFilter(
         final BiFunction<String, Map<String, String>, DgraphGoldenRecords> runQuery,
         final List<
               BiFunction<
                     BiFunction<
                           String,
                           Map<String, String>,
                           DgraphGoldenRecords>,
                     CustomDemographicData,
                     DgraphGoldenRecords>> listFunction,
         final CustomDemographicData interaction) {
      final LinkedList<CustomDgraphGoldenRecord> candidateGoldenRecords = new LinkedList<>();
      for (BiFunction<
            BiFunction<
                  String, Map<String, String>, DgraphGoldenRecords>,
            CustomDemographicData,
            DgraphGoldenRecords> deterministicFunction : listFunction) {
         final var block = deterministicFunction.apply(runQuery, interaction);
         if (!block.all().isEmpty()) {
            final var list = block.all();
            if (!AppUtils.isNullOrEmpty(list)) {
               candidateGoldenRecords.addAll(list);
               return candidateGoldenRecords;
            }
         }
      }
      return candidateGoldenRecords;
   }

   public static List<CustomDgraphGoldenRecord> findLinkCandidates(
         final BiFunction<String, Map<String, String>, DgraphGoldenRecords> runQuery,
         final CustomDemographicData interaction) {
      var result = deterministicFilter(runQuery, DETERMINISTIC_LINK_FUNCTIONS, interaction);
      if (!result.isEmpty()) {
         return result;
      }
      result = new LinkedList<>();
      mergeCandidates(result, queryLinkProbabilistic(runQuery, interaction));
      return result;
   }

   public static List<CustomDgraphGoldenRecord> findMatchCandidates(
         final BiFunction<String, Map<String, String>, DgraphGoldenRecords> runQuery,
         final CustomDemographicData interaction) {
      var result = deterministicFilter(runQuery, DETERMINISTIC_MATCH_FUNCTIONS, interaction);
      if (!result.isEmpty()) {
         return result;
      }
      result = new LinkedList<>();
      return result;
   }


}
