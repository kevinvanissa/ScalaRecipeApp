package model

import play.api.libs.json.Json
import scalikejdbc.WrappedResultSet

case class MealPlan(id: Int, foodid: String, shrt_desc:String, protein_g: Double, lipid_tot_g: Double, carbohydrt_g:Double, amount: Double, energ_kcal: Double,
                    msre_desc: String, gm_wgt: Double)

object MealPlan {

  //implicit val writes = Json.writes[Recipe]

  def fromRS(rs: WrappedResultSet): MealPlan = {
   MealPlan(rs.int("id"),
     rs.string("foodid"), rs.string("Shrt_Desc"), rs.double("Protein_g"),rs.double("Lipid_Tot_g"),
     rs.double("Carbohydrt_g"), rs.double("amount"),rs.double("Energ_Kcal") , rs.string("Msre_Desc") , rs.double("Gm_Wgt") )
 }

}
