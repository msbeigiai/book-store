package models

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

case class Book(id: Int, title: String, price: Int, author: String) {}

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
}

class BookActor extends Actor with ActorLogging {

  import BookActor._

  override def receive: Receive = {
    case AllBooks =>
      log.info("Getting all books.")
      sender() ! books

    case AddBook(book) =>
      log.info(s"Adding new book $book to the database.")
      books = books + book

    case FindById(id) =>
      log.info(s"Finding book by id $id")
      sender() ! books.find(_.id == id)

    case UpdateBook(updatedBook) =>
      log.info(s"Updating book with id ${updatedBook.id}")
      books = books.map(book => if (book.id == updatedBook.id) updatedBook else book)

  }
}


