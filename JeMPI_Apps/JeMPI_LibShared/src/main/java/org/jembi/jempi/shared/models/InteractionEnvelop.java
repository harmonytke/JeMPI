package org.jembi.jempi.shared.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record InteractionEnvelop(
      ContentType contentType,
      String tag,
      String /* System Trace Audit Number */ stan,
      Interaction interaction) {

   public enum ContentType {
      BATCH_START_SENTINEL(ContentType.MESSAGE_START_VALUE),
      BATCH_END_SENTINEL(ContentType.MESSAGE_END_VALUE),
      BATCH_INTERACTION(ContentType.MESSAGE_INTERACTION_VALUE);
      public static final int MESSAGE_START_VALUE = 1;
      public static final int MESSAGE_END_VALUE = 2;
      public static final int MESSAGE_INTERACTION_VALUE = 3;

      public final int type;

      ContentType(final int type_) {
         this.type = type_;
      }
   }

}


