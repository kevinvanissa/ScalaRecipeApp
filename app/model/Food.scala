package model

import play.api.libs.json.Json
import scalikejdbc.WrappedResultSet




case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}



case class Food(ndb_no:String, shrt_desc: String)

object Food {

  implicit val writes = Json.writes[Food]

  def fromRS(rs: WrappedResultSet): Food = {
   Food(rs.string("ndb_no"),rs.string("shrt_desc"))
 }

}
