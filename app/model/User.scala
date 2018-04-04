package model

import scalikejdbc.WrappedResultSet

case class User(userId: Int, email: String, password: String)

object User {
  def fromRS(rs: WrappedResultSet): User = {
      User(rs.int("uid"),
        rs.string("email"), rs.string("password"))
      }
}
