package controllers

import akka.actor.{ActorSystem, Props}
import models.{Book, BookActor}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.duration._
import play.api.mvc.{AbstractController, Action, AnyContent, BaseController, ControllerComponents, Request, Result}
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class BooksController @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, cc: ControllerComponents)
                               (implicit ec: ExecutionContext)
  extends AbstractController(cc)
  with play.api.i18n.I18nSupport
  with HasDatabaseConfigProvider[JdbcProfile] {

  val system = ActorSystem("BookSystem")
  val bookActor = system.actorOf(Props[BookActor], "bookActor")

  import BookActor._
  import akka.pattern.ask
  import akka.util.Timeout
  implicit val timeout: Timeout = Timeout(2 seconds)

  import forms.BookForms._

  def index(): Action[AnyContent] = Action.async { implicit request =>
    val booksFuture = (bookActor ? AllBooks).mapTo[Set[Book]]
    for {
      books <- booksFuture
    } yield {
      val resultBooks: Future[Seq[Book]] = db.run(Book.filter)
      Ok(views.html.books.index(books))
    }

  }

  def create(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.books.create(bookForm))
  }

  def save(): Action[AnyContent] = Action { implicit request =>
    bookForm.bindFromRequest.fold (
      formWithErrors => {
        BadRequest(views.html.books.create(formWithErrors))
      },
      book => {
        bookActor ! AddBook(book)
        Redirect(routes.BooksController.index()).flashing("success" -> "Book saved!")
      }
    )
  }

  def edit(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val foundBook = (bookActor ? FindById(id)).mapTo[Option[Book]]
    foundBook.map {
      case Some(v) => Ok(views.html.books.edit(bookForm.fill(v)))
      case None => NotFound("Not found")
    }

  }

  def update(): Action[AnyContent] = Action { implicit request =>
    bookForm.bindFromRequest.fold (
      formWithErrors => {
        BadRequest(views.html.books.create(formWithErrors))
      },
      book => {
        bookActor ! UpdateBook(book)
        Redirect(routes.BooksController.index()).flashing("success" -> "Book saved!")
      }
    )

  }

  def destroy(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val foundBook = (bookActor ? FindById(id)).mapTo[Option[Book]]
    foundBook.map {
      case Some(v) =>
        bookActor ! DeleteBook(v)
        Redirect(routes.BooksController.index()).flashing("success" -> s"Book $v successfully deleted!")
      case None => NotFound("Not found")
    }
  }

  def show(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val foundBook = (bookActor ? FindById(id)).mapTo[Option[Book]]
    foundBook.map {
      case Some(v) => Ok(views.html.books.show(v))
      case None => NotFound("Not found")
    }
  }
}
