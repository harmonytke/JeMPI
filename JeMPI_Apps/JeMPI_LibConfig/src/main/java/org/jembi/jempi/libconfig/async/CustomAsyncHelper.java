package org.jembi.jempi.libconfig.async;

import org.apache.commons.csv.CSVRecord;
import org.jembi.jempi.libconfig.shared.models.CustomDemographicData;
import org.jembi.jempi.libconfig.shared.models.CustomSourceId;
import org.jembi.jempi.libconfig.shared.models.CustomUniqueInteractionData;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CustomAsyncHelper {

   private static final int AUX_ID_COL_NUM = 0;
   private static final int AUX_CLINICAL_DATA_COL_NUM = 10;
   private static final int SOURCEID_FACILITY_COL_NUM = 8;
   private static final int SOURCEID_PATIENT_COL_NUM = 9;
   private static final int GIVEN_NAME_COL_NUM = 1;
   private static final int FAMILY_NAME_COL_NUM = 2;
   private static final int GENDER_COL_NUM = 3;
   private static final int DOB_COL_NUM = 4;
   private static final int CITY_COL_NUM = 5;
   private static final int PHONE_NUMBER_COL_NUM = 6;
   private static final int NATIONAL_ID_COL_NUM = 7;

   private CustomAsyncHelper() {
   }

   private static String parseRecordNumber(final String in) {
      final var regex = "^rec-(?<rnum>\\d+)-(?<class>(org|aaa|dup|bbb)?)-?(?<dnum>\\d+)?$";
      final Pattern pattern = Pattern.compile(regex);
      final Matcher matcher = pattern.matcher(in);
      if (matcher.find()) {
         final var rNumber = matcher.group("rnum");
         final var klass = matcher.group("class");
         final var dNumber = matcher.group("dnum");
         return String.format(Locale.ROOT,
                              "rec-%010d-%s-%d",
                              Integer.parseInt(rNumber),
                              klass,
                              (("org".equals(klass) || "aaa".equals(klass))
                                     ? 0
                                     : Integer.parseInt(dNumber)));
      }
      return in;
   }


   public static CustomUniqueInteractionData customUniqueInteractionData(final CSVRecord csvRecord) {
      return new CustomUniqueInteractionData(java.time.LocalDateTime.now(),
                                             parseRecordNumber(csvRecord.get(AUX_ID_COL_NUM)),
                                             csvRecord.get(AUX_CLINICAL_DATA_COL_NUM));
   }

   public static CustomDemographicData customDemographicData(final CSVRecord csvRecord) {
      return new CustomDemographicData(new String[]{csvRecord.get(GIVEN_NAME_COL_NUM),
                                                    csvRecord.get(FAMILY_NAME_COL_NUM),
                                                    csvRecord.get(GENDER_COL_NUM),
                                                    csvRecord.get(DOB_COL_NUM),
                                                    csvRecord.get(CITY_COL_NUM),
                                                    csvRecord.get(PHONE_NUMBER_COL_NUM),
                                                    csvRecord.get(NATIONAL_ID_COL_NUM)});
   }

   public static CustomSourceId customSourceId(final CSVRecord csvRecord) {
      return new CustomSourceId(
            null,
            csvRecord.get(SOURCEID_FACILITY_COL_NUM),
            csvRecord.get(SOURCEID_PATIENT_COL_NUM));
   }

}

