package org.jembi.jempi.em

import scala.collection.immutable.ArraySeq

object Fields {

  private val GIVEN_NAME_IDX = 0
  private val FAMILY_NAME_IDX = 1
  private val GENDER_IDX = 2
  private val DOB_IDX = 3
  private val CITY_IDX = 4
  private val PHONE_NUMBER_IDX = 5
  private val NATIONAL_ID_IDX = 6

  val FIELDS: ArraySeq[Field] = ArraySeq(
    Field("Given Name", GIVEN_NAME_IDX),
    Field("Family Name", FAMILY_NAME_IDX),
    Field("Gender", GENDER_IDX),
    Field("Date of Birth", DOB_IDX),
    Field("City", CITY_IDX),
    Field("Mobile", PHONE_NUMBER_IDX),
    Field("National ID", NATIONAL_ID_IDX)
  )

}
