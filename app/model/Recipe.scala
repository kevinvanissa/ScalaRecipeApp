package model

import play.api.libs.json.Json
import scalikejdbc.WrappedResultSet

case class Recipe(name: String, description: String, ingredient: String, instruction: String, rimage: String)

object Recipe {

  //implicit val writes = Json.writes[Recipe]

  def fromRS(rs: WrappedResultSet): Recipe = {
   Recipe(rs.string("name"),
     rs.string("description"), rs.string("ingredient"), rs.string("instruction"), rs.string("image"))
 }






}
