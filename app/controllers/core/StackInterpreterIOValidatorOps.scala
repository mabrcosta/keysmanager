package controllers.core

object StackInterpreterIOValidatorOps {

  implicit class IOValidation[E, A](value: Either[E, A]) {

    def validateRight(right: A => Boolean): Boolean = value match {
      case Left(_)  => false
      case Right(v) => right(v)
    }

  }

}
