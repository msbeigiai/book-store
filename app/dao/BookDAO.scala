package dao

import models.Book
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import java.sql.Date
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class BookDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private class BookTable(tag: Tag) extends Table[Book](tag, Some("kafka_test"), "book") {
    implicit val databaseColumnType = MappedColumnType.base[Date, Long](d => d.getTime, d => new Date(d))

    def id = column[Int]("book_id", O.PrimaryKey)
    def title = column[String]("title")
    def price = column[Int]("price")
    def author = column[String]("author")

    override def * : ProvenShape[Book] = (id, title, price, author) <> (Book.tupled, Book.unapply)
  }

  private val bookTable = TableQuery[BookTable]

  def insert(book: Book): Future[Unit] =
    db.run(bookTable += book).mapTo[Unit]

  def allBooks(): Future[Seq[Book]] =
    db.run(bookTable.to[Seq].result)
}
