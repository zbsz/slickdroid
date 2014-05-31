package scala.slick.android

import java.sql.{SQLException, Timestamp, Time, Date}

/** Basic conversions for setting parameters in PositionedParameters. */
trait SetParameter[-T] extends ((T, PositionedParameters) => Unit) { self =>
  def applied(value: T): SetParameter[Unit] = new SetParameter[Unit] {
    def apply(u: Unit, pp: PositionedParameters) {
      self.apply(value, pp)
    }
  }
}

/** SetParameter for tuple types. */
class SetTupleParameter[-T <: Product](val children: SetParameter[_]*) extends SetParameter[T] {
  def apply(param: T, pp: PositionedParameters): Unit =
    children.iterator.zip(param.productIterator).foreach(t => t._1.asInstanceOf[SetParameter[Any]](t._2, pp))
  override def toString = "SetTupleParameter<"+children.length+">"
}

object SetParameter {
  implicit object SetBigDecimal extends SetParameter[BigDecimal] { def apply(v: BigDecimal, pp: PositionedParameters) { pp.setBigDecimal(v) } }
  implicit object SetBoolean extends SetParameter[Boolean] { def apply(v: Boolean, pp: PositionedParameters) { pp.setBoolean(v) } }
  implicit object SetByte extends SetParameter[Byte] { def apply(v: Byte, pp: PositionedParameters) { pp.setByte(v) } }
  implicit object SetDate extends SetParameter[Date] { def apply(v: Date, pp: PositionedParameters) { pp.setDate(v) } }
  implicit object SetDouble extends SetParameter[Double] { def apply(v: Double, pp: PositionedParameters) { pp.setDouble(v) } }
  implicit object SetFloat extends SetParameter[Float] { def apply(v: Float, pp: PositionedParameters) { pp.setFloat(v) } }
  implicit object SetInt extends SetParameter[Int] { def apply(v: Int, pp: PositionedParameters) { pp.setInt(v) } }
  implicit object SetLong extends SetParameter[Long] { def apply(v: Long, pp: PositionedParameters) { pp.setLong(v) } }
  implicit object SetShort extends SetParameter[Short] { def apply(v: Short, pp: PositionedParameters) { pp.setShort(v) } }
  implicit object SetString extends SetParameter[String] { def apply(v: String, pp: PositionedParameters) { pp.setString(v) } }
  implicit object SetTime extends SetParameter[Time] { def apply(v: Time, pp: PositionedParameters) { pp.setTime(v) } }
  implicit object SetTimestamp extends SetParameter[Timestamp] { def apply(v: Timestamp, pp: PositionedParameters) { pp.setTimestamp(v) } }

  implicit object SetBigDecimalOption extends SetParameter[Option[BigDecimal]] { def apply(v: Option[BigDecimal], pp: PositionedParameters) { pp.setBigDecimalOption(v) } }
  implicit object SetBooleanOption extends SetParameter[Option[Boolean]] { def apply(v: Option[Boolean], pp: PositionedParameters) { pp.setBooleanOption(v) } }
  implicit object SetByteOption extends SetParameter[Option[Byte]] { def apply(v: Option[Byte], pp: PositionedParameters) { pp.setByteOption(v) } }
  implicit object SetDateOption extends SetParameter[Option[Date]] { def apply(v: Option[Date], pp: PositionedParameters) { pp.setDateOption(v) } }
  implicit object SetDoubleOption extends SetParameter[Option[Double]] { def apply(v: Option[Double], pp: PositionedParameters) { pp.setDoubleOption(v) } }
  implicit object SetFloatOption extends SetParameter[Option[Float]] { def apply(v: Option[Float], pp: PositionedParameters) { pp.setFloatOption(v) } }
  implicit object SetIntOption extends SetParameter[Option[Int]] { def apply(v: Option[Int], pp: PositionedParameters) { pp.setIntOption(v) } }
  implicit object SetLongOption extends SetParameter[Option[Long]] { def apply(v: Option[Long], pp: PositionedParameters) { pp.setLongOption(v) } }
  implicit object SetShortOption extends SetParameter[Option[Short]] { def apply(v: Option[Short], pp: PositionedParameters) { pp.setShortOption(v) } }
  implicit object SetStringOption extends SetParameter[Option[String]] { def apply(v: Option[String], pp: PositionedParameters) { pp.setStringOption(v) } }
  implicit object SetTimeOption extends SetParameter[Option[Time]] { def apply(v: Option[Time], pp: PositionedParameters) { pp.setTimeOption(v) } }
  implicit object SetTimestampOption extends SetParameter[Option[Timestamp]] { def apply(v: Option[Timestamp], pp: PositionedParameters) { pp.setTimestampOption(v) } }

  @inline implicit def createSetTuple2[T1, T2](implicit c1: SetParameter[T1], c2: SetParameter[T2]): SetTupleParameter[(T1, T2)] = new SetTupleParameter[(T1, T2)](c1, c2)
  @inline implicit def createSetTuple3[T1, T2, T3](implicit c1: SetParameter[T1], c2: SetParameter[T2], c3: SetParameter[T3]): SetTupleParameter[(T1, T2, T3)] = new SetTupleParameter[(T1, T2, T3)](c1, c2, c3)
  @inline implicit def createSetTuple4[T1, T2, T3, T4](implicit c1: SetParameter[T1], c2: SetParameter[T2], c3: SetParameter[T3], c4: SetParameter[T4]): SetTupleParameter[(T1, T2, T3, T4)] = new SetTupleParameter[(T1, T2, T3, T4)](c1, c2, c3, c4)
  @inline implicit def createSetTuple5[T1, T2, T3, T4, T5](implicit c1: SetParameter[T1], c2: SetParameter[T2], c3: SetParameter[T3], c4: SetParameter[T4], c5: SetParameter[T5]): SetTupleParameter[(T1, T2, T3, T4, T5)] = new SetTupleParameter[(T1, T2, T3, T4, T5)](c1, c2, c3, c4, c5)
  @inline implicit def createSetTuple6[T1, T2, T3, T4, T5, T6](implicit c1: SetParameter[T1], c2: SetParameter[T2], c3: SetParameter[T3], c4: SetParameter[T4], c5: SetParameter[T5], c6: SetParameter[T6]): SetTupleParameter[(T1, T2, T3, T4, T5, T6)] = new SetTupleParameter[(T1, T2, T3, T4, T5, T6)](c1, c2, c3, c4, c5, c6)
  @inline implicit def createSetTuple7[T1, T2, T3, T4, T5, T6, T7](implicit c1: SetParameter[T1], c2: SetParameter[T2], c3: SetParameter[T3], c4: SetParameter[T4], c5: SetParameter[T5], c6: SetParameter[T6], c7: SetParameter[T7]): SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7)] = new SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7)](c1, c2, c3, c4, c5, c6, c7)
  @inline implicit def createSetTuple8[T1, T2, T3, T4, T5, T6, T7, T8](implicit c1: SetParameter[T1], c2: SetParameter[T2], c3: SetParameter[T3], c4: SetParameter[T4], c5: SetParameter[T5], c6: SetParameter[T6], c7: SetParameter[T7], c8: SetParameter[T8]): SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8)] = new SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8)](c1, c2, c3, c4, c5, c6, c7, c8)
  @inline implicit def createSetTuple9[T1, T2, T3, T4, T5, T6, T7, T8, T9](implicit c1: SetParameter[T1], c2: SetParameter[T2], c3: SetParameter[T3], c4: SetParameter[T4], c5: SetParameter[T5], c6: SetParameter[T6], c7: SetParameter[T7], c8: SetParameter[T8], c9: SetParameter[T9]): SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9)] = new SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9)](c1, c2, c3, c4, c5, c6, c7, c8, c9)
  @inline implicit def createSetTuple10[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10](implicit c1: SetParameter[T1], c2: SetParameter[T2], c3: SetParameter[T3], c4: SetParameter[T4], c5: SetParameter[T5], c6: SetParameter[T6], c7: SetParameter[T7], c8: SetParameter[T8], c9: SetParameter[T9], c10: SetParameter[T10]): SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10)] = new SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10)](c1, c2, c3, c4, c5, c6, c7, c8, c9, c10)
  @inline implicit def createSetTuple11[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11](implicit c1: SetParameter[T1], c2: SetParameter[T2], c3: SetParameter[T3], c4: SetParameter[T4], c5: SetParameter[T5], c6: SetParameter[T6], c7: SetParameter[T7], c8: SetParameter[T8], c9: SetParameter[T9], c10: SetParameter[T10], c11: SetParameter[T11]): SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11)] = new SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11)](c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11)
  @inline implicit def createSetTuple12[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12](implicit c1: SetParameter[T1], c2: SetParameter[T2], c3: SetParameter[T3], c4: SetParameter[T4], c5: SetParameter[T5], c6: SetParameter[T6], c7: SetParameter[T7], c8: SetParameter[T8], c9: SetParameter[T9], c10: SetParameter[T10], c11: SetParameter[T11], c12: SetParameter[T12]): SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12)] = new SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12)](c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12)
  @inline implicit def createSetTuple13[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13](implicit c1: SetParameter[T1], c2: SetParameter[T2], c3: SetParameter[T3], c4: SetParameter[T4], c5: SetParameter[T5], c6: SetParameter[T6], c7: SetParameter[T7], c8: SetParameter[T8], c9: SetParameter[T9], c10: SetParameter[T10], c11: SetParameter[T11], c12: SetParameter[T12], c13: SetParameter[T13]): SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13)] = new SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13)](c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13)
  @inline implicit def createSetTuple14[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14](implicit c1: SetParameter[T1], c2: SetParameter[T2], c3: SetParameter[T3], c4: SetParameter[T4], c5: SetParameter[T5], c6: SetParameter[T6], c7: SetParameter[T7], c8: SetParameter[T8], c9: SetParameter[T9], c10: SetParameter[T10], c11: SetParameter[T11], c12: SetParameter[T12], c13: SetParameter[T13], c14: SetParameter[T14]): SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14)] = new SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14)](c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14)
  @inline implicit def createSetTuple15[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15](implicit c1: SetParameter[T1], c2: SetParameter[T2], c3: SetParameter[T3], c4: SetParameter[T4], c5: SetParameter[T5], c6: SetParameter[T6], c7: SetParameter[T7], c8: SetParameter[T8], c9: SetParameter[T9], c10: SetParameter[T10], c11: SetParameter[T11], c12: SetParameter[T12], c13: SetParameter[T13], c14: SetParameter[T14], c15: SetParameter[T15]): SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15)] = new SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15)](c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15)
  @inline implicit def createSetTuple16[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16](implicit c1: SetParameter[T1], c2: SetParameter[T2], c3: SetParameter[T3], c4: SetParameter[T4], c5: SetParameter[T5], c6: SetParameter[T6], c7: SetParameter[T7], c8: SetParameter[T8], c9: SetParameter[T9], c10: SetParameter[T10], c11: SetParameter[T11], c12: SetParameter[T12], c13: SetParameter[T13], c14: SetParameter[T14], c15: SetParameter[T15], c16: SetParameter[T16]): SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16)] = new SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16)](c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16)
  @inline implicit def createSetTuple17[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17](implicit c1: SetParameter[T1], c2: SetParameter[T2], c3: SetParameter[T3], c4: SetParameter[T4], c5: SetParameter[T5], c6: SetParameter[T6], c7: SetParameter[T7], c8: SetParameter[T8], c9: SetParameter[T9], c10: SetParameter[T10], c11: SetParameter[T11], c12: SetParameter[T12], c13: SetParameter[T13], c14: SetParameter[T14], c15: SetParameter[T15], c16: SetParameter[T16], c17: SetParameter[T17]): SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17)] = new SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17)](c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17)
  @inline implicit def createSetTuple18[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18](implicit c1: SetParameter[T1], c2: SetParameter[T2], c3: SetParameter[T3], c4: SetParameter[T4], c5: SetParameter[T5], c6: SetParameter[T6], c7: SetParameter[T7], c8: SetParameter[T8], c9: SetParameter[T9], c10: SetParameter[T10], c11: SetParameter[T11], c12: SetParameter[T12], c13: SetParameter[T13], c14: SetParameter[T14], c15: SetParameter[T15], c16: SetParameter[T16], c17: SetParameter[T17], c18: SetParameter[T18]): SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18)] = new SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18)](c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18)
  @inline implicit def createSetTuple19[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19](implicit c1: SetParameter[T1], c2: SetParameter[T2], c3: SetParameter[T3], c4: SetParameter[T4], c5: SetParameter[T5], c6: SetParameter[T6], c7: SetParameter[T7], c8: SetParameter[T8], c9: SetParameter[T9], c10: SetParameter[T10], c11: SetParameter[T11], c12: SetParameter[T12], c13: SetParameter[T13], c14: SetParameter[T14], c15: SetParameter[T15], c16: SetParameter[T16], c17: SetParameter[T17], c18: SetParameter[T18], c19: SetParameter[T19]): SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19)] = new SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19)](c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19)
  @inline implicit def createSetTuple20[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20](implicit c1: SetParameter[T1], c2: SetParameter[T2], c3: SetParameter[T3], c4: SetParameter[T4], c5: SetParameter[T5], c6: SetParameter[T6], c7: SetParameter[T7], c8: SetParameter[T8], c9: SetParameter[T9], c10: SetParameter[T10], c11: SetParameter[T11], c12: SetParameter[T12], c13: SetParameter[T13], c14: SetParameter[T14], c15: SetParameter[T15], c16: SetParameter[T16], c17: SetParameter[T17], c18: SetParameter[T18], c19: SetParameter[T19], c20: SetParameter[T20]): SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20)] = new SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20)](c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19, c20)
  @inline implicit def createSetTuple21[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21](implicit c1: SetParameter[T1], c2: SetParameter[T2], c3: SetParameter[T3], c4: SetParameter[T4], c5: SetParameter[T5], c6: SetParameter[T6], c7: SetParameter[T7], c8: SetParameter[T8], c9: SetParameter[T9], c10: SetParameter[T10], c11: SetParameter[T11], c12: SetParameter[T12], c13: SetParameter[T13], c14: SetParameter[T14], c15: SetParameter[T15], c16: SetParameter[T16], c17: SetParameter[T17], c18: SetParameter[T18], c19: SetParameter[T19], c20: SetParameter[T20], c21: SetParameter[T21]): SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21)] = new SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21)](c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19, c20, c21)
  @inline implicit def createSetTuple22[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22](implicit c1: SetParameter[T1], c2: SetParameter[T2], c3: SetParameter[T3], c4: SetParameter[T4], c5: SetParameter[T5], c6: SetParameter[T6], c7: SetParameter[T7], c8: SetParameter[T8], c9: SetParameter[T9], c10: SetParameter[T10], c11: SetParameter[T11], c12: SetParameter[T12], c13: SetParameter[T13], c14: SetParameter[T14], c15: SetParameter[T15], c16: SetParameter[T16], c17: SetParameter[T17], c18: SetParameter[T18], c19: SetParameter[T19], c20: SetParameter[T20], c21: SetParameter[T21], c22: SetParameter[T22]): SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22)] = new SetTupleParameter[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22)](c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19, c20, c21, c22)

  object SetSimpleProduct extends SetParameter[Product] {
    def apply(prod: Product, pp: PositionedParameters): Unit =
      for(i <- 0 until prod.productArity) prod.productElement(i) match {
        case v: Boolean => pp.setBoolean(v)
        case v: Byte => pp.setByte(v)
        case v: Date => pp.setDate(v)
        case v: Double => pp.setDouble(v)
        case v: Float => pp.setFloat(v)
        case v: Int => pp.setInt(v)
        case v: Long => pp.setLong(v)
        case v: Short => pp.setShort(v)
        case v: String => pp.setString(v)
        case v: Time => pp.setTime(v)
        case v: Timestamp => pp.setTimestamp(v)
        case v => throw new SQLException("SetProduct doesn't know how to handle parameter "+i+" ("+v+")")
      }
  }

  implicit object SetUnit extends SetParameter[Unit] { def apply(none: Unit, pp: PositionedParameters) = () }

  def apply[T](implicit f: (T, PositionedParameters) => Unit) = new SetParameter[T] {
    def apply(v: T, pp: PositionedParameters) = f(v, pp)
  }
}
