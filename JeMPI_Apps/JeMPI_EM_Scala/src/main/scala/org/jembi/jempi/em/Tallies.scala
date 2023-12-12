package org.jembi.jempi.em

import org.jembi.jempi.em.Fields.FIELDS

import scala.collection.immutable.ArraySeq

case class Tallies(colTally: ArraySeq[Tally] = FIELDS.map(_ => Tally()))
