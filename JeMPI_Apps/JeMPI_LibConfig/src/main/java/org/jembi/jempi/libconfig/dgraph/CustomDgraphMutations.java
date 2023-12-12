package org.jembi.jempi.libconfig.dgraph;

import org.jembi.jempi.libconfig.shared.models.CustomDemographicData;
import org.jembi.jempi.libconfig.shared.models.CustomUniqueGoldenRecordData;
import org.jembi.jempi.libconfig.shared.models.CustomUniqueInteractionData;
import org.jembi.jempi.libconfig.shared.utils.AppUtils;

import java.util.Locale;
import java.util.UUID;

import static org.jembi.jempi.libconfig.shared.models.CustomDemographicData.*;

public final class CustomDgraphMutations {

   private CustomDgraphMutations() {
   }

   public static String createInteractionTriple(
         final CustomUniqueInteractionData uniqueInteractionData,
         final CustomDemographicData demographicData,
         final String sourceUID) {
      final String uuid = UUID.randomUUID().toString();
      return String.format(Locale.ROOT,
                           """
                           _:%s  <Interaction.source_id>                     <%s>                  .
                           _:%s  <Interaction.aux_date_created>              %s^^<xs:dateTime>     .
                           _:%s  <Interaction.aux_id>                        %s                    .
                           _:%s  <Interaction.aux_clinical_data>             %s                    .
                           _:%s  <Interaction.given_name>                    %s                    .
                           _:%s  <Interaction.family_name>                   %s                    .
                           _:%s  <Interaction.gender>                        %s                    .
                           _:%s  <Interaction.dob>                           %s                    .
                           _:%s  <Interaction.city>                          %s                    .
                           _:%s  <Interaction.phone_number>                  %s                    .
                           _:%s  <Interaction.national_id>                   %s                    .
                           _:%s  <dgraph.type>                               "Interaction"         .
                           """,
                           uuid, sourceUID,
                           uuid, AppUtils.quotedValue(uniqueInteractionData.auxDateCreated().toString()),
                           uuid, AppUtils.quotedValue(uniqueInteractionData.auxId()),
                           uuid, AppUtils.quotedValue(uniqueInteractionData.auxClinicalData()),
                           uuid, AppUtils.quotedValue(demographicData.fields[GIVEN_NAME_IDX].value()),
                           uuid, AppUtils.quotedValue(demographicData.fields[FAMILY_NAME_IDX].value()),
                           uuid, AppUtils.quotedValue(demographicData.fields[GENDER_IDX].value()),
                           uuid, AppUtils.quotedValue(demographicData.fields[DOB_IDX].value()),
                           uuid, AppUtils.quotedValue(demographicData.fields[CITY_IDX].value()),
                           uuid, AppUtils.quotedValue(demographicData.fields[PHONE_NUMBER_IDX].value()),
                           uuid, AppUtils.quotedValue(demographicData.fields[NATIONAL_ID_IDX].value()),
                           uuid);
   }

   public static String createLinkedGoldenRecordTriple(
         final CustomUniqueGoldenRecordData uniqueGoldenRecordData,
         final CustomDemographicData demographicData,
         final String interactionUID,
         final String sourceUID,
         final float score) {
      final String uuid = UUID.randomUUID().toString();
      return String.format(Locale.ROOT,
                           """
                           _:%s  <GoldenRecord.source_id>                     <%s>                  .
                           _:%s  <GoldenRecord.aux_date_created>              %s^^<xs:dateTime>     .
                           _:%s  <GoldenRecord.aux_auto_update_enabled>       %s^^<xs:boolean>      .
                           _:%s  <GoldenRecord.aux_id>                        %s                    .
                           _:%s  <GoldenRecord.given_name>                    %s                    .
                           _:%s  <GoldenRecord.family_name>                   %s                    .
                           _:%s  <GoldenRecord.gender>                        %s                    .
                           _:%s  <GoldenRecord.dob>                           %s                    .
                           _:%s  <GoldenRecord.city>                          %s                    .
                           _:%s  <GoldenRecord.phone_number>                  %s                    .
                           _:%s  <GoldenRecord.national_id>                   %s                    .
                           _:%s  <GoldenRecord.interactions>                  <%s> (score=%f)       .
                           _:%s  <dgraph.type>                                "GoldenRecord"        .
                           """,
                           uuid, sourceUID,
                           uuid, AppUtils.quotedValue(uniqueGoldenRecordData.auxDateCreated().toString()),
                           uuid, AppUtils.quotedValue(uniqueGoldenRecordData.auxAutoUpdateEnabled().toString()),
                           uuid, AppUtils.quotedValue(uniqueGoldenRecordData.auxId()),
                           uuid, AppUtils.quotedValue(demographicData.fields[GIVEN_NAME_IDX].value()),
                           uuid, AppUtils.quotedValue(demographicData.fields[FAMILY_NAME_IDX].value()),
                           uuid, AppUtils.quotedValue(demographicData.fields[GENDER_IDX].value()),
                           uuid, AppUtils.quotedValue(demographicData.fields[DOB_IDX].value()),
                           uuid, AppUtils.quotedValue(demographicData.fields[CITY_IDX].value()),
                           uuid, AppUtils.quotedValue(demographicData.fields[PHONE_NUMBER_IDX].value()),
                           uuid, AppUtils.quotedValue(demographicData.fields[NATIONAL_ID_IDX].value()),
                           uuid, interactionUID, score,
                           uuid);
   }
}
