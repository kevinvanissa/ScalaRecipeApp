package services

import model.Recipe
import scalikejdbc._


class RecipeService {

 def getRecipe(id: Int): Option[Recipe] = {
   DB.readOnly { implicit session =>
  val maybeRecipe = sql"select * from recipe where rid = $id".
    map(Recipe.fromRS).single().apply()
    maybeRecipe
  }
 }

  def retrieveRecipe(id: Int): Option[Recipe] = {
    DB.readOnly { implicit session =>
    val maybeRecipe = sql"select * from recipe where rid = $id".
      map(Recipe.fromRS).single().apply()
      maybeRecipe
    }
  }




 def getRecipes(): List[Recipe] = {
   DB.readOnly { implicit session =>
  val maybeRecipe = sql"select * from recipe".
    map(Recipe.fromRS).list.apply()
    maybeRecipe
  }
 }

def saveRecipe(recipetitle: String, rtype:Int, description: String,
  ingredient: String, instruction: String, picture: String)(implicit s: DBSession = AutoSession):Unit = {
    //val inst = instruction
  val id = sql"insert into recipe(name,rtype,description,ingredient,instruction,image) values (${recipetitle}, ${rtype},${description},${ingredient},${instruction},${picture})"
        .updateAndReturnGeneratedKey.apply()

}

}
