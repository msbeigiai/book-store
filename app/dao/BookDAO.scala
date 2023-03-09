package dao

import models.Book
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import java.sql.Date
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BookDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  class BookTable(tag: Tag) extends Table[Book](tag, Some("book"), "kafka_test") {
    implicit val databaseColumnType = MappedColumnType.base[Date, Long](d => d.getTime, d => new Date(d))

    def id = column[Int]("id", O.PrimaryKey)
    def title = column[String]("title")
    def price = column[Int]("price")
    def author = column[String]("author")

    override def * : ProvenShape[Book] = (id, title, price, author) <> (Book.tupled, Book.unapply)
  }

  lazy val bookTable = TableQuery[BookTable]

  def insert(book: Book): Future[Unit] =
    db.run(bookTable += book).map(_ => ())
}
