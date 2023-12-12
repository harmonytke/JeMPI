package org.jembi.jempi.em

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

import scala.collection.immutable.ArraySeq

@JsonIgnoreProperties(ignoreUnknown = true)
case class InteractionEnvelop(
    contentType: String,
    tag: Option[String],
    stan: Option[String],
    interaction: Option[Interaction]
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
case class Interaction(
    uniqueInteractionData: UniqueInteractionData,
    demographicData: DemographicData
)

@JsonIgnoreProperties(ignoreUnknown = true)
case class UniqueInteractionData(auxId: String)

@JsonIgnoreProperties(ignoreUnknown = true)
case class DemographicField(name: String, value: String)

@JsonIgnoreProperties(ignoreUnknown = true)
case class DemographicData(fields: ArraySeq[DemographicField])

//    givenName: String,
//    familyName: String,
//    gender: String,
//    dob: String,
//    city: String,
//    phoneNumber: String,
//    nationalId: String
//)
