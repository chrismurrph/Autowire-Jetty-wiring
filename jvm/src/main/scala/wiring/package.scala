/**
 * User: Chris
 * Date: 01/09/2015
 * Time: 3:09 PM
 */
package object wiring {
  implicit class BDKeepsNulls(val repr: java.math.BigDecimal) extends AnyVal {
    def asScala: BigDecimal = if (repr eq null) null else new BigDecimal(repr)
  }
}
