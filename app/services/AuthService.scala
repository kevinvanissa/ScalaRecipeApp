package services

import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.TimeUnit

import model.User
import org.apache.commons.codec.binary.Base64
import org.mindrot.jbcrypt.BCrypt
import play.api.cache.SyncCacheApi
import play.api.mvc.{Cookie, RequestHeader}
import scalikejdbc._


import scala.concurrent.duration.Duration


class   AuthService(cacheApi: SyncCacheApi) {


  def login(userCode: String, password: String): Option[Cookie] = {
    //println(checkUser(userCode, password))
    for {
      user <- checkUser(userCode, password)
      cookie <- Some(createCookie(user))
} yield {
  cookie
  }
}

def checkCookie(header: RequestHeader): Option[User] = {

   for {
     cookie <- header.cookies.get(cookieHeader)
     user <- cacheApi.get[User](cookie.value)
   } yield {
     user
   }
 }



  private def checkUser(userCode: String, password: String): Option[User] =
    DB.readOnly { implicit session =>
        val maybeUser = sql"select * from user where email = $userCode".
          map(User.fromRS).single().apply()
        maybeUser.flatMap { user =>
          if (BCrypt.checkpw(password, user.password)) {
            Some(user)
          } else None
        }
    }


  def getUser(userid: Int): Option[User] =
    DB.readOnly {implicit session =>
      val maybeUser = sql"select * from user where id=$userid".map(User.fromRS).single().apply()
      maybeUser
    }


  def checkEmail(email: String) : Boolean =
    DB.readOnly {implicit  session =>
      val emailCount = sql"select email from user where email=$email".map(_.string("email")).single().apply()
      if (emailCount.getOrElse("").equals(email)){
        true
      }else{
        false
      }
    }

  def createUser(firstname: String, lastname: String, email: String, password: String) (implicit  s: DBSession = AutoSession) : Unit = {
    //import org.mindrot.jbcrypt.BCrypt
    def hash(password: String) = BCrypt.hashpw(password, BCrypt.gensalt(12 ))

    val passwordhash = hash(password)
    val id = sql"insert into user(firstname,lastname,email, password) values ($firstname,$lastname,$email,$passwordhash)".updateAndReturnGeneratedKey().apply()
  }

  def saveProfile(userid: Int, firstname: String, lastname: String, dob: String, weight: String, height: String, gender: String, activity_level: String)  (implicit  s: DBSession = AutoSession): Unit={
       val id = sql"update user set firstname=$firstname, lastname=$lastname, dob=$dob, weight=$weight, height=$height, gender=$gender,activity_level=$activity_level where id=$userid".update().apply()
  }




    val mda = MessageDigest.getInstance("SHA-512")
    val cookieHeader = "X-Auth-Token"

  private def createCookie(user: User): Cookie = {
    val randomPart = UUID.randomUUID().toString.toUpperCase
    val userPart = user.userId.toString.toUpperCase
    val key = s"$randomPart|$userPart"
    val token = Base64.encodeBase64String(mda.digest(key.getBytes))
    val duration = Duration.create(10, TimeUnit.HOURS)
    cacheApi.set(token, user, duration)
    Cookie(cookieHeader, token, maxAge = Some(duration.toSeconds.toInt))
 }

  def destroyCookie() : Cookie = {
    val token =""
    val duration = Duration.create(0, TimeUnit.HOURS)
    Cookie(cookieHeader, token, maxAge = Some(duration.toSeconds.toInt))
  }




}
