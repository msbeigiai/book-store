package forms

import models.Book
import play.api.data.Form
import play.api.data.Forms._

object BookForms {

  val bookForm: Form[Book] = Form(
    mapping(
      "id" -> number,
      "title" -> text,
      "price" -> number,
      "author" -> text
    )(Book.apply)(Book.unapply)
  )

}
