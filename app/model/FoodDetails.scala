package model

import play.api.libs.json.Json
import scalikejdbc.WrappedResultSet


case class FoodDetails(ndb_no: String, shrt_desc: String, protein_g: Double, lipid_tot_g: Double, carbohydrt_g:Double,gmwt_1: Double, gmwt_desc1:String,gmwt_2:Double,gmwt_desc2:String,energ_kcal: Double)

object FoodDetails {

  //implicit val writes = Json.writes[Food]

  def fromRS(rs: WrappedResultSet): FoodDetails = {
    FoodDetails(rs.string("ndb_no"),rs.string("shrt_desc"),rs.double("protein_g"),rs.double("lipid_tot_g"),
      rs.double("carbohydrt_g"),rs.double("gmwt_1"),rs.string("gmwt_desc1"),rs.double("gmwt_2"),
      rs.string("gmwt_desc2"),rs.double("energ_kcal"))
  }

}
