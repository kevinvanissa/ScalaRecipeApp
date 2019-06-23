package model

import play.api.libs.json.Json
import scalikejdbc.WrappedResultSet


case class MsreDesc(seq: String, msre_desc:String, amount: Int, gm_wgt: Double)

object MsreDesc {


  def fromRS(rs: WrappedResultSet): MsreDesc = {
    MsreDesc(rs.string("Seq"),rs.string("msre_desc"),rs.int("amount"),rs.double("gm_wgt"))
  }



  def calculatev(nutval: Double, descval: Double,amount: Int): Double = {
  	val score = ((nutval/100) * descval) * amount
 	 score
}

}
