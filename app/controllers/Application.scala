package controllers

import actors.StatsActor
import akka.actor.ActorSystem
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import play.api._
import play.api.mvc._
import services._
import model._
import play.api.libs.json.Json
import play.api.data.Form
import play.api.data.Forms._
import akka.pattern.ask
import play.api.Logger
import play.mvc.Http.MultipartFormData
import java.nio.file.Paths

import scala.concurrent.ExecutionContext.Implicits.global

case class RecipeData(recipetitle: String,  description: String, ingredient: String, instruction: String)
case class UserLoginData(email: String, password: String)

class Application (components: ControllerComponents, recipeService: RecipeService, authService: AuthService, userAuthAction: UserAuthAction, actorSystem: ActorSystem)
    extends AbstractController(components) {

  def index = Action {
    Ok(views.html.index())
  }


  def restricted = userAuthAction { userAuthRequest =>
     Ok(views.html.restricted(userAuthRequest.user))
   }

  def login = Action {
    Ok(views.html.login(None))
  }

  def doLogin = Action { implicit request =>
    userDataForm.bindFromRequest.fold(
      formWithErrors => Ok(views.html.login(Some("Wrong data"))),
      userData => {
        val maybeCookie = authService.login(userData.email, userData.password)
        maybeCookie match {
          case Some(cookie) =>
            Redirect("/").withCookies(cookie)
          case None =>
            Ok(views.html.login(Some("Login failed")))
        }
      }
    )
  }


  def createrecipe = Action {
    Ok(views.html.createrecipe(None))
  }

  def docreaterecipe = Action(parse.multipartFormData) { implicit request =>
    recipeDataForm.bindFromRequest.fold(
      formWithErrors => Ok(views.html.createrecipe(Some("Fill out the Form!"))),
      userData => {
     //Logger.info(userData.quantityList.toString())
     //Logger.info(userData.measureList.toString())
    // Logger.info(userData.ingredientList.toString())
    //Logger.info(request.body.file("picture").toString())
   request.body.file("picture").map { picture =>
   // only get the last part of the filename
   // otherwise someone can send a path like ../../home/foo/bar.txt to write to other files on the system
   val filename = Paths.get(picture.filename).getFileName
   picture.ref.moveTo(Paths.get(s"/home/kevin/Programming/scala-recipe-project/public/uploads/$filename"), replace = true)
   //Ok("File uploaded")
 recipeService.saveRecipe(userData.recipetitle,1,userData.description,userData.ingredient,userData.instruction,filename.toString())

 }.getOrElse {
   //Redirect(routes.ler.index).flashing("error" -> "Missing file")
     Ok(views.html.createrecipe(Some("Missing File")))
 }
            Ok(views.html.createrecipe(Some("Sucessful processed Form")))

        }
    )
  }


  //def recipe = Action{
  //  val recipe = recipeService.getRecipe()
//    Ok(Json.toJson(recipe))
//  }


  def retrieveRecipe(id: Int) = Action { implicit request =>
    //Logger.info(id.toString)
    //val id = request.body.asFormUrlEncoded.get("scala_surname")(0)
    val recipe = recipeService.retrieveRecipe(id)
    val ingredients = (recipe.get.ingredient).split("\n").map(s => s.replaceAll("^\\s+", ""))
    val instructions = (recipe.get.instruction).split("\n")
    //Ok(recipe.get.name)
    //val recipe ="hello"
    Ok(views.html.showrecipe(recipe,ingredients,instructions))
  }

  //def recipe = Action{
    //val recipe = recipeService.getRecipe(1)
    //Ok(Json.toJson(recipe))
  //}

 //def recipes = Action{
  //val recipes = recipeService.getRecipes()
  //Ok(Json.toJson(recipes))
 //}


 val userDataForm = Form {
 mapping(
   "email" -> nonEmptyText,
   "password" -> nonEmptyText
   )(UserLoginData.apply)(UserLoginData.unapply)
 }


 val recipeDataForm = Form {
   mapping(
     "recipetitle" -> nonEmptyText,
     "description" -> nonEmptyText,
     "ingredient" -> nonEmptyText,
     "instruction" -> nonEmptyText
     )(RecipeData.apply)(RecipeData.unapply)
 }

}
