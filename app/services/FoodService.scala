package services

import model.{Food, FoodDetails, MealPlan, MsreDesc, Page}
import scalikejdbc._

import scala.concurrent.Future


class FoodService {

  def retrieveFood(ndb_no:String): Option[FoodDetails] = {
    DB.readOnly{ implicit session =>
      val maybeFood = sql"select * from alldata where ndb_no = $ndb_no".
        map(FoodDetails.fromRS).single().apply()
      maybeFood
    }
  }


  def getLongDes(ndb_no: String): String = {
    DB.readOnly { implicit session =>
      val longDes = sql"select Long_Desc from food_des where NDB_No=$ndb_no".map(_.string("Long_Desc")).single.apply().getOrElse("")
      longDes
    }
  }




  def deleteMeal(mealid: Int, userid: Int) (implicit s: DBSession = AutoSession): Unit = {
    sql"delete from meal_plan where id=$mealid and userid=$userid".update.apply()

  }


  def deleteFavourite(foodid: String, userid: Int) (implicit  s: DBSession = AutoSession):Unit = {
    sql"delete from favourite_food where foodid=$foodid and userid=$userid".update.apply()
  }



  def saveMeal(date:String,userid:Int, foodid:String, meal_time: String,amount:Double,seq:String)(implicit s: DBSession = AutoSession):Unit = {
      val id = sql"insert into meal_plan(date,userid,foodid,meal_time,amount,Seq) values($date,$userid,$foodid,$meal_time,$amount,$seq)".updateAndReturnGeneratedKey.apply()
    //println("Printing the ID:....")
    //println(id)
  }






def saveFavourite(foodid: String, userid: Int) (implicit  s: DBSession = AutoSession):Unit = {
  val id = sql"insert into favourite_food (foodid, userid) values ($foodid,$userid)".updateAndReturnGeneratedKey().apply()
}


  def getMsreDescription(ndb_no:String): List[MsreDesc] = {
    DB.readOnly{ implicit session =>
      val msreDesc = sql"select Seq, Msre_Desc,Amount,Gm_Wgt from weight where ndb_no = $ndb_no".map(MsreDesc.fromRS).list.apply()

          msreDesc
    }
  }


  def isFavourite(ndb_no: String, userid: Int): Boolean = {
    DB.readOnly { implicit session =>
      println(userid)
      println(ndb_no)
            val fav = sql"select foodid from favourite_food where userid=$userid and foodid=$ndb_no".map(_.string("foodid")).single.apply()
            //println(fav)
            if(fav.getOrElse("")equals(ndb_no)){
               //println(fav)
                 true
              } else {
             false
             }

    }

  }

 def getFoods(): List[Food] = {
   DB.readOnly { implicit session =>
  val maybeFood = sql"select * from alldata".
    map(Food.fromRS).list.apply()
    maybeFood
  }
 }


  def getfavouriteFoods(userid: Int):  List[Food] = {
    DB.readOnly{ implicit session =>
      val maybeFoods = sql"select NDB_No, Shrt_Desc from favourite_food  ff JOIN  food_des fd  ON  ff.foodid=fd.NDB_No AND userid=$userid".
        map(Food.fromRS).list.apply()
      maybeFoods
    }
  }

//FIXME: Recommendation is random for now
  def getRecommendedFoods(userid: Int): List[Food] = {
    DB.readOnly { implicit session =>
      val maybeFood = sql"select * from alldata limit 10".
        map(Food.fromRS).list.apply()
      maybeFood
    }
  }




  def autosearchfood(term: String="%"): List[Food] = {
    DB.readOnly {implicit  session =>
     val mad = "%"+ term +"%"
      val maybeFood =sql"select NDB_No, Shrt_Desc from alldata where Shrt_Desc like $mad limit 10".
        map(Food.fromRS).list().apply()
      println(maybeFood)
      maybeFood
    }
  }





  def getMeals(mealdate :String, userid: Int, mealtype: String): List[MealPlan] = {
    DB.readOnly { implicit session =>
                         //select mp.id, mp.foodid, ad.Shrt_Desc, Protein_g, Lipid_Tot_g, Carbohydrt_g, mp.amount, Energ_Kcal, Msre_Desc, w.Gm_Wgt from alldata ad JOIN meal_plan mp JOIN weight w ON mp.Seq = w.Seq AND mp.foodid = w.NDB_No AND mp.foodid = ad.NDB_No AND YEAR(date)=2019 and MONTH(date)=01 and DAY(date)=09 and userid=1 and meal_time="Breakfast"
      val maybeMeal = sql" select mp.id, mp.foodid, ad.Shrt_Desc, Protein_g, Lipid_Tot_g, Carbohydrt_g, mp.amount, Energ_Kcal, Msre_Desc, w.Gm_Wgt from alldata ad JOIN meal_plan mp JOIN weight w ON mp.Seq = w.Seq AND mp.foodid = w.NDB_No AND mp.foodid=ad.NDB_No  AND date=$mealdate and userid=$userid and meal_time=$mealtype".
        map(MealPlan.fromRS).list().apply()
          maybeMeal
    }
  }




  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[Food]= {
  DB.readOnly{implicit session =>
    val offset = pageSize * page
    val foods = sql"select NDB_No, Shrt_Desc from alldata order by $orderBy limit $pageSize offset $offset".map(Food.fromRS).list.apply()
    val total =sql"select count(*) as c from alldata".map(_.long(1)).single.apply()
    //foods.size
    Page(foods,page,offset,total.get)
  }
  }


  def searchFood(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[Food]= {
    //FIXME: Please fix here to take from filter
    DB.readOnly{implicit session =>

      /*
      val foodtitle="Butter Eggs Milk"
      val foodtitlefirst = foodtitle.split(" ").take(1)
      val foodtitlerest = foodtitle.split(" ").drop(1)
      val orphrase1 = "WHERE Shrt_Desc LIKE '%"+foodtitlefirst(0)+"%'"
      //println(orphrase1)
      val orphraserest = foodtitlerest.map( foodtitle => " OR Shrt_Desc LIKE '%"+foodtitle+"%'")
      val finalstring = orphrase1 + orphraserest.reduce(_ + _)
      //println(finalstring)
*/

      val offset = pageSize * page
/*
      val check = "select NDB_No, Shrt_Desc from alldata "+finalstring+" order by "+orderBy+" limit "+pageSize+" offset "+offset+""
      println(check)
      val check2 = "select count(*) from alldata "+finalstring
      val foods = SQL(check).map(Food.fromRS).list.apply()
      val total =SQL(check2).map(_.long(1)).single.apply()
*/



      val foods = sql"select NDB_No, Shrt_Desc from alldata where Shrt_Desc like $filter order by $orderBy limit $pageSize offset $offset".map(Food.fromRS).list.apply()
      val total =sql"select count(*) as c from alldata where Shrt_Desc like $filter".map(_.long(1)).single.apply()
      //foods.size
      Page(foods,page,offset,total.get)
    }
  }



}
