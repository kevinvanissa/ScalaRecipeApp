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
import java.text.SimpleDateFormat
import java.time.LocalDate

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

case class RecipeData(recipetitle: String,  description: String, ingredient: String, instruction: String)
case class UserLoginData(email: String, password: String)
case class FoodSearchData(foodtitle: String)
case class MealPlanData(date:String, foodid: String, meal_time: String, amount: String, description: String)
case class DateData(date:String)
case class RegisterData(firstname: String, lastname: String, email: String, password: String, confirm:String)
case class ProfileData(firstname: String, lastname: String, dob: String, weight: String, height: String,
                       gender: String, activity_level: String)

class Application (components: ControllerComponents, recipeService: RecipeService, foodService: FoodService, authService: AuthService, userAuthAction: UserAuthAction, actorSystem: ActorSystem)
                  (implicit ec: ExecutionContext) extends AbstractController(components) {

  def index = Action { implicit request =>
    val user = authService.checkCookie(request)
    if(user.getOrElse("")==""){
      Ok(views.html.index(user))
    }else{
      val now = java.time.LocalDate.now().toString
      Redirect(routes.Application.mealplan(now,which=""))
    }

  }


  def restricted = userAuthAction { userAuthRequest =>
     Ok(views.html.restricted(userAuthRequest.user))
   }

  def login = Action { implicit  request =>
    Ok(views.html.login(None, authService.checkCookie(request)))
  }

  def register = Action{ implicit request =>
    Ok(views.html.register(None,authService.checkCookie(request)))
  }


  def foodsearch(page: Int, orderBy: Int, filter: String) = userAuthAction { implicit  request =>
     // var foods: Page[Food]
/*    foodSearchForm.bindFromRequest.fold(
      formWithErrors => Ok("Errors"),
      foodData => {
        val foods = foodService.searchFood(page = page, orderBy = orderBy, filter = ("%" + filter + "%"))
        Ok(views.html.foodsearch(None, foods, orderBy, filter))
      }
    )*/


    val date = request.session.get("date").getOrElse("")
    //println(request.session.get("date").getOrElse(""))
   // val foods = foodService.searchFood(page = page, orderBy = orderBy, filter = filter)
    val foods = foodService.searchFood(page = page, orderBy = orderBy, filter = ("%" + filter + "%"))
    //println(filter)
    Ok(views.html.foodsearch(foods, orderBy, filter,date, authService.getUser(authService.checkCookie(request).get.userId)  ))
  }





  def doLogin = Action { implicit request =>
    userDataForm.bindFromRequest.fold(
      formWithErrors => Ok(views.html.login(Some("Wrong data"),authService.checkCookie(request))),
      userData => {
        val maybeCookie = authService.login(userData.email, userData.password)
        maybeCookie match {
          case Some(cookie) =>
            //println(cookie)
            val now = java.time.LocalDate.now().toString
            Redirect(routes.Application.mealplan(now,which="")).withCookies(cookie)
            //Redirect("/foodsearch").withCookies(cookie)
          case None =>
            Ok(views.html.login(Some("Login failed"),authService.checkCookie(request)))
        }
      }
    )
  }


  def doLogout = userAuthAction { implicit  request =>
    //Redirect(routes.Application.index)
     Redirect(routes.Application.index).flashing("info" -> "You have successfully logged out!").withCookies(authService.destroyCookie).withNewSession
      //Ok("")
  }



  def createrecipe = Action { implicit  request =>

    Ok(views.html.createrecipe(None,authService.checkCookie(request)))
  }


  def editprofile = userAuthAction { implicit  request =>
      Ok(views.html.editprofile(   authService.getUser(authService.checkCookie(request).get.userId)   ))
    //Ok(views.html.editprofile(authService.checkCookie(request)))

  }



  def searchfoodauto() = Action { implicit  request =>
    //term = "butter"
    val term = request.queryString("term")(0)
    //println(r("term")(0))
      val foods = foodService.autosearchfood(term)
    //println(foods)

    val jsons: play.api.libs.json.JsValue = Json.parse("""
  {
    "name" : "Watership Down",
    "location" : "Kingston",
    "residents" :"Dundee"
  }
  """)

    //val foodstring = foods.map(_.ndb_no+",")
    //println(foodstring)


    val jsonw: play.api.libs.json.JsValue = Json.obj(
      "name" -> "Watership Down",
      "location" -> Json.obj("lat" -> 51.235685, "long" -> -1.309197),
      "residents" -> Json.arr(
        Json.obj(
          "name" -> "Fiver",
          "age" -> 4,
          "role" -> play.api.libs.json.JsNull
        ),
        Json.obj(
          "name" -> "Bigwig",
          "age" -> 6,
          "role" -> "Owsla"
        )
      )
    )

    Ok(Json.obj("result" -> foods))
    //Ok(Json.toJson(jsons))
    //Ok(Json.toJson(foods))
  }




  def mealplan(date: String, which:String) = userAuthAction { implicit  request =>
    val d = (nutval: Double, descval: Double,amount: Double) => {
      val score = ((nutval/100) * descval) * amount
      (score * 100).round / 100.toDouble
    }
    //val format = "YYYY-M-D"
   // val sf = new SimpleDateFormat().parse(year+"-"+month+"-"+day)
    //val date = new java.util.GregorianCalendar(year,month-1,day).getTime()

    //val l = java.time.LocalDate.parse(date).plusDays(1)
    //println(l)
    //val g =  if (which == "next") java.time.LocalDate.parse(date).plusDays(1).toString else java.time.LocalDate.parse(date).minusDays(1).toString

    val datestring =
      which match{
        case "next" => java.time.LocalDate.parse(date).plusDays(1).toString
        case "prev" => java.time.LocalDate.parse(date).minusDays(1).toString
        case _   => date

      }


    //println(year+"-"+month+"-"+day)

      val userid:Int = request.user.userId
      //println( request.cookies)

      val favouriteFoods = foodService.getfavouriteFoods(userid)
      val recommendedFoods = foodService.getRecommendedFoods(userid)

     val breakfastList: List[MealPlan] = foodService.getMeals(datestring,userid,"Breakfast")
     val lunchList: List[MealPlan] = foodService.getMeals(datestring,userid,"Lunch")
     val dinnerList: List[MealPlan] = foodService.getMeals(datestring,userid,"Dinner")

     val breakfastcal = ((for (m <- breakfastList) yield d(m.energ_kcal,m.gm_wgt,m.amount)).sum * 100).round / 100.toDouble
    val lunchcal = ((for (m <- lunchList) yield d(m.energ_kcal,m.gm_wgt,m.amount)).sum * 100).round / 100.toDouble
    val dinnercal = ((for (m <- dinnerList) yield d(m.energ_kcal,m.gm_wgt,m.amount)).sum * 100).round / 100.toDouble

     val totalcal = ((breakfastcal + lunchcal + dinnercal)*100).round / 100.toDouble

   //val x = (lunchcal*100).round / 100.toDouble
    //println(x)
//======================================================
    val breakfastprot = ((for (m <- breakfastList) yield d(m.protein_g,m.gm_wgt,m.amount)).sum * 100).round / 100.toDouble
    val lunchprot = ((for (m <- lunchList) yield d(m.protein_g,m.gm_wgt,m.amount)).sum * 100).round / 100.toDouble
    val dinnerprot = ((for (m <- dinnerList) yield d(m.protein_g,m.gm_wgt,m.amount)).sum * 100).round / 100.toDouble

    val totalprot = ((breakfastprot + lunchprot + dinnerprot)*100).round / 100.toDouble
    val protList: Array[Double] = Array(breakfastprot,lunchprot,dinnerprot,totalprot)
    //======================================================

    val breakfastfat = ((for (m <- breakfastList) yield d(m.lipid_tot_g,m.gm_wgt,m.amount)).sum * 100).round / 100.toDouble
    val lunchfat = ((for (m <- lunchList) yield d(m.lipid_tot_g,m.gm_wgt,m.amount)).sum * 100).round / 100.toDouble
    val dinnerfat = ((for (m <- dinnerList) yield d(m.lipid_tot_g,m.gm_wgt,m.amount)).sum * 100).round / 100.toDouble

    val totalfat = ((breakfastfat + lunchfat + dinnerfat)*100).round / 100.toDouble

    val fatList: Array[Double] = Array(breakfastfat,lunchfat,dinnerfat,totalfat)
    //======================================================


    val breakfastcarb = ((for (m <- breakfastList) yield d(m.carbohydrt_g,m.gm_wgt,m.amount)).sum * 100).round / 100.toDouble
    val lunchcarb = ((for (m <- lunchList) yield d(m.carbohydrt_g,m.gm_wgt,m.amount)).sum * 100).round / 100.toDouble
    val dinnercarb = ((for (m <- dinnerList) yield d(m.carbohydrt_g,m.gm_wgt,m.amount)).sum * 100).round / 100.toDouble

    val totalcarb = ((breakfastcarb + lunchcarb + dinnercarb)*100).round / 100.toDouble
    val carbList: Array[Double] = Array(breakfastcarb,lunchcarb,dinnercarb,totalcarb)

    //======================================================

   // val error = Option(request.session.get("error").getOrElse(""))


   // val x =  error match {
    //    case Some(s) => Some(s)
    //    case None => None
    //  }




    Ok(views.html.mealplan(protList,fatList,carbList,breakfastList, lunchList,dinnerList,d,breakfastcal,lunchcal,dinnercal,totalcal,datestring,authService.getUser(authService.checkCookie(request).get.userId),favouriteFoods,recommendedFoods)).withSession(request.session + ("date" -> datestring)+("error" -> ""))
  }

  def docreaterecipe = Action(parse.multipartFormData) { implicit request =>
    recipeDataForm.bindFromRequest.fold(
      formWithErrors => Ok(views.html.createrecipe(Some("Fill out the Form!"),authService.checkCookie(request))),
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
     Ok(views.html.createrecipe(Some("Missing File"),authService.checkCookie(request)))
 }
            Ok(views.html.createrecipe(Some("Sucessful processed Form"),authService.checkCookie(request)))

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
    //val recipe = recipeService.
    val ingredients = recipe.get.ingredient.split("\n").map(s => s.replaceAll("^\\s+", ""))
    val instructions = (recipe.get.instruction).split("\n")
    //Ok(recipe.get.name)
    //val recipe ="hello"
    Ok(views.html.showrecipe(recipe,ingredients,instructions,authService.checkCookie(request)))
  }

  //def recipe = Action{
    //val recipe = recipeService.getRecipe(1)
    //Ok(Json.toJson(recipe))
  //}

 //def recipes = Action{
  //val recipes = recipeService.getRecipes()
  //Ok(Json.toJson(recipes))
 //}


/**
   * Display the paginated list of computers.
   *
   * @param page Current page number (starts from 0)
   * @param orderBy Column to be sorted
   * @param filter Filter applied on computer names
   */
  def list(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
    val foods = foodService.list(page = page, orderBy = orderBy, filter = ("%" + filter + "%"))
      Ok(views.html.list(foods, orderBy, filter,authService.checkCookie(request)))
    }


  def foods = userAuthAction{ implicit request =>
   val foods = foodService.getFoods()
   Ok(views.html.showfoods(foods, authService.getUser(authService.checkCookie(request).get.userId) ))

  }


  def retrieveFood(ndb_no: String) = userAuthAction { implicit  request =>
    val food = foodService.retrieveFood(ndb_no)
    val msreDesc = foodService.getMsreDescription(ndb_no)
    //FIXME: make Action to be userAuthAction and then enable to other line
    val ffood: Boolean = foodService.isFavourite(ndb_no,authService.checkCookie(request).get.userId)
    println("See boolean below")
    println(ffood)
   // val ffood: Boolean = foodService.isFavourite(ndb_no,authService.checkCookie(request).get.userId)
    val date = request.session.get("date").getOrElse("")
    Ok(views.html.food(food,msreDesc,date, authService.getUser(authService.checkCookie(request).get.userId)  ,ffood))
  }


  def saveFavourite(foodid: String) = userAuthAction { implicit  request =>

    val favFood = foodService.saveFavourite(foodid, authService.checkCookie(request).get.userId)
       Redirect(routes.Application.retrieveFood(foodid))
  }



/* FIXME: THE DELETE SENT THE USER TO THE WRONG DATE MEAL PLAN*/
  def deleteMeal(mealid: Int) = Action { implicit  request =>
    foodService.deleteMeal(mealid, authService.checkCookie(request).get.userId)
    val date = request.session.get("date").getOrElse("")
    Redirect(routes.Application.mealplan(date,which=""))
    //Ok("")
  }


  def deleteFavourite(foodid:String) = Action { implicit  request =>
    foodService.deleteFavourite(foodid, authService.checkCookie(request).get.userId)
    val date = request.session.get("date").getOrElse("")
    Redirect(routes.Application.mealplan(date,which=""))
    //Ok("")
  }



  def getlongdes = userAuthAction { implicit  request =>
    val postval = request.body.asFormUrlEncoded.get("foodid").head

    val longdes = foodService.getLongDes(postval)
    Ok(longdes)
  }


  def getDate = userAuthAction { implicit  request =>

    dateDataForm.bindFromRequest.fold(
    formWithErrors => {
      val date = request.session.get("date").getOrElse("")
      //Redirect(routes.Application.mealplan(date,which="")).withSession(request.session + ("error" -> "Sorry, cannot process empty form!"))
      Redirect(routes.Application.mealplan(date,which="")).flashing("failure" -> "Please fill in date")
     },

      userData => {
      Redirect(routes.Application.mealplan(userData.date,which=""))
    })
   }


  def saveProfile = userAuthAction { implicit  request =>

    profileDataForm.bindFromRequest.fold(
      formWithErrors => {
        Redirect(routes.Application.editprofile).flashing("failure" -> "Error Creating Profile !")
      },

      userData => {
        val profile = authService.saveProfile(authService.checkCookie(request).get.userId,userData.firstname,userData.lastname,userData.dob,
          userData.weight,userData.height,userData.gender,userData.activity_level)
        Redirect(routes.Application.editprofile).flashing("success" -> "Profile Successfully Saved !")
      })
 }



  def doRegister = Action {implicit  request =>
    registerDataForm.bindFromRequest.fold(
      formWithErrors => Redirect("/register").flashing("failure" -> "Cannot register user"),

      userData => {

        if(userData.password.trim != userData.confirm.trim){
          Redirect("/register").flashing("failure" -> "Passwords do not match!")
        }else if (authService.checkEmail(userData.email)){
          Redirect("/register").flashing("failure" -> "This user already exists!")
        }else{

          val id = authService.createUser(userData.firstname, userData.lastname, userData.email, userData.password)
          Redirect("/register").flashing("success" -> "Registration Successful. You can now Log in")
        }


      })


  }


  def saveMealPlan = userAuthAction { implicit  request =>
    //println("This is favourite -> ")
    //println(mealPlanDataForm.bindFromRequest.get.fav)

    mealPlanDataForm.bindFromRequest.fold(

        formWithErrors => Ok("Cannot bind form"),
        userData => {
          //println("Reached Here!!!!!!")

           //println(userData.fav)
          val id = foodService.saveMeal(userData.date,authService.checkCookie(request).get.userId,userData.foodid,userData.meal_time,userData.amount.toDouble,userData.description.split(" ")(1))
          //Redirect("/mealplan/"+userData.date)
          Redirect(routes.Application.mealplan(userData.date,which=""))
        }
      )

    //Ok("Problems occurred saving meal plan")
  }




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



  val foodSearchForm = Form {
    mapping(
      "foodtitle" -> nonEmptyText
    )(FoodSearchData.apply)(FoodSearchData.unapply)
}


  val mealPlanDataForm = Form {
    mapping(
      "date" -> nonEmptyText,
       "foodid" -> nonEmptyText,
      "meal_time" -> nonEmptyText,
      "amount" -> nonEmptyText,
      "description" -> nonEmptyText,
     )(MealPlanData.apply)(MealPlanData.unapply)
  }

  val registerDataForm = Form {
    mapping(
      "firstname" -> nonEmptyText,
      "lastname"  -> nonEmptyText,
      "email" -> nonEmptyText,
      "password" -> nonEmptyText,
      "confirm" -> nonEmptyText
    )(RegisterData.apply)(RegisterData.unapply) //verifying("Password and Confirm password does not match", info => info.password == info.confirm)
  }


  val profileDataForm = Form{
    mapping(
      "firstname" -> nonEmptyText,
      "lastname"  -> nonEmptyText,
      "dob" -> nonEmptyText,
      "weight" -> nonEmptyText,
      "height" -> nonEmptyText,
      "gender"-> nonEmptyText,
      "activity_level" -> nonEmptyText
    )(ProfileData.apply)(ProfileData.unapply)

  }


val dateDataForm = Form {
  mapping(
    "date" -> nonEmptyText
  )(DateData.apply)(DateData.unapply)
}


}
