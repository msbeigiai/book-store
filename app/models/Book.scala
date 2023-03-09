package models

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.pattern.pipe
import dao.BookDAO
import play.api.Configuration

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global

case class Book(id: Int, title: String, price: Int, author: String)

/*object Book {
  var books: Set[Book] = Set(
    new Book(1, "C++", 20, "ABC"),
    new Book(2, "JAVA", 30, "XYZ")
  )

  def allBooks(): Set[Book] = books
  def findById(id: Int): Book = books.find(_.id == id).get
  def addBook(book: Book): Set[Book] = books + book
  def removeBook(book: Book): Set[Book] = books - book
}*/


object BookActor {
  var books: Set[Book] = Set(
    Book(1, "C++", 20, "ABC"),
    Book(2, "JAVA", 30, "XYZ")
  )
  case object AllBooks
  case class AddBook(book: Book)
  case class FindById(id: Int)
  case class UpdateBook(book: Book)
  case class DeleteBook(book: Book)

  def props(bookDAO: BookDAO): Props = Props(new BookActor(bookDAO))
}

class BookActor @Inject() (bookDAO: BookDAO) extends Actor with ActorLogging {

  import BookActor._

  override def receive: Receive = {
    case AllBooks =>
      log.info("Getting all books.")
      bookDAO.allBooks().pipeTo(sender())

    case AddBook(book) =>
      log.info(s"Adding new book $book to the database.")
      bookDAO.insert(book)

    case FindById(id) =>
      log.info(s"Finding book by id $id")
      bookDAO.findById(id).pipeTo(sender())/*.mapTo[Option[Book]]*/

    case UpdateBook(updatedBook) =>
      log.info(s"Updating book with id ${updatedBook.id}")
      bookDAO.updateBook(updatedBook)

    case DeleteBook(book) =>
      log.warning(s"Deleting book with id ${book.id}")
      bookDAO.deleteBook(book)

  }
}


