package org.jembi.jempi.libconfig.linker;

import org.apache.commons.lang3.StringUtils;
import org.jembi.jempi.libconfig.shared.models.CustomDemographicData;

import static org.jembi.jempi.libconfig.shared.models.CustomDemographicData.*;

public final class CustomLinkerDeterministic {

   public static final boolean DETERMINISTIC_DO_LINKING = true;
   public static final boolean DETERMINISTIC_DO_VALIDATING = false;
   public static final boolean DETERMINISTIC_DO_MATCHING = false;

   private CustomLinkerDeterministic() {
   }

   private static boolean isMatch(
         final String left,
         final String right) {
      return StringUtils.isNotBlank(left) && StringUtils.equals(left, right);
   }

   public static boolean canApplyLinking(
         final CustomDemographicData interaction) {
      return CustomLinkerProbabilistic.PROBABILISTIC_DO_LINKING
             || StringUtils.isNotBlank(interaction.fields[NATIONAL_ID_IDX].value())
             || StringUtils.isNotBlank(interaction.fields[GIVEN_NAME_IDX].value())
             && StringUtils.isNotBlank(interaction.fields[FAMILY_NAME_IDX].value())
             && StringUtils.isNotBlank(interaction.fields[PHONE_NUMBER_IDX].value());
   }

   public static boolean linkDeterministicMatch(
         final CustomDemographicData goldenRecord,
         final CustomDemographicData interaction) {
      final var nationalIdL = goldenRecord.fields[NATIONAL_ID_IDX].value();
      final var nationalIdR = interaction.fields[NATIONAL_ID_IDX].value();
      if (isMatch(nationalIdL, nationalIdR)) {
         return true;
      }
      final var givenNameL = goldenRecord.fields[GIVEN_NAME_IDX].value();
      final var givenNameR = interaction.fields[GIVEN_NAME_IDX].value();
      final var familyNameL = goldenRecord.fields[FAMILY_NAME_IDX].value();
      final var familyNameR = interaction.fields[FAMILY_NAME_IDX].value();
      final var phoneNumberL = goldenRecord.fields[PHONE_NUMBER_IDX].value();
      final var phoneNumberR = interaction.fields[PHONE_NUMBER_IDX].value();
      return (isMatch(givenNameL, givenNameR) && isMatch(familyNameL, familyNameR) && isMatch(phoneNumberL, phoneNumberR));
   }

   public static boolean validateDeterministicMatch(
         final CustomDemographicData goldenRecord,
         final CustomDemographicData interaction) {
      return false;
   }

   public static boolean matchNotificationDeterministicMatch(
         final CustomDemographicData goldenRecord,
         final CustomDemographicData interaction) {
      return false;
   }

}
