package model

import scalikejdbc.WrappedResultSet

case class User(userId: Int, email: String, password: String,firstname :String, lastname: String, weight: Option[Double], height: Option[Double],
                dob: String, gender: String, activity_level: String)

object User {
  def fromRS(rs: WrappedResultSet): User = {
      User(rs.int("id"),
        rs.string("email"), rs.string("password"),rs.string("firstname"),rs.string("lastname"),
    rs.doubleOpt("weight"),rs.doubleOpt("height"),
        rs.string("dob"),rs.string("gender"),
        rs.string("activity_level"))
      }
}
