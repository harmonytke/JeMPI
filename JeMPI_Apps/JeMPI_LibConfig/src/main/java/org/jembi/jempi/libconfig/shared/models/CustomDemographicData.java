package org.jembi.jempi.libconfig.shared.models;

import java.util.Arrays;
import java.util.stream.IntStream;

public class CustomDemographicData {

   public static final int GIVEN_NAME_IDX = 0;
   public static final int FAMILY_NAME_IDX = 1;
   public static final int GENDER_IDX = 2;
   public static final int DOB_IDX = 3;
   public static final int CITY_IDX = 4;
   public static final int PHONE_NUMBER_IDX = 5;
   public static final int NATIONAL_ID_IDX = 6;
   public static final String[] FIELD_NAMES = {"givenName", "familyName", "gender", "dob", "city", "phoneNumber", "nationalId"};
   public final Field[] fields;

   public CustomDemographicData(final String[] values) {
      fields = IntStream.range(0, values.length)
                        .boxed()
                        .map(i -> new Field(i, FIELD_NAMES[i], values[i]))
                        .toArray(Field[]::new);
   }

   public CustomDemographicData(final Field[] fields) {
      this.fields = fields;
   }

   public CustomDemographicData clean() {
      return new CustomDemographicData(
            Arrays.stream(this.fields)
                  .map(x -> new Field(x.idx, x.name, x.value.toLowerCase().replaceAll("\\W", "")))
                  .toArray(Field[]::new));
   }

   public record Field(
         int idx,
         String name,
         String value) {
   }

}
