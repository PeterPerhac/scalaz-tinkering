package example

import scala.language.higherKinds

object App {

  type Now[X] = X

  trait Terminal[C[_]] {
    def read: C[String]

    def write(s: String): C[Unit]
  }

  trait Execution[C[_]] {
    def doAndThen[A, B](c: C[A])(f: A => C[B]): C[B]

    def create[B](b: B): C[B]
  }

  implicit class Ops[A, C[_]](c: C[A]) {
    def flatMap[B](f: A => C[B])(implicit e: Execution[C]): C[B] =
      e.doAndThen(c)(f)

    def map[B](f: A => B)(implicit e: Execution[C]): C[B] =
      e.doAndThen(c)(f andThen e.create)
  }

  def echo[C[_]](implicit t: Terminal[C], e: Execution[C]): C[String] =
    for {
      in <- t.read
      _ <- t.write(in)
    } yield in

  implicit object TerminalSync extends Terminal[Now] {
    def read: String = "Something"

    def write(s: String): Unit = println(s)
  }

  implicit object TerminalIO extends Terminal[IO] {
    def read: IO[String] = IO(io.StdIn.readLine)

    def write(t: String): IO[Unit] = IO(println(t))
  }

  class IO[A](val interpret: () => A) {
    def map[B](f: A => B): IO[B] = IO(f(interpret()))

    def flatMap[B](f: A => IO[B]): IO[B] = IO(f(interpret()).interpret())
  }

  object IO {
    def apply[A](a: => A): IO[A] = new IO(() => a)
  }

  implicit object IOExecution extends Execution[IO] {
    override def doAndThen[A, B](c: IO[A])(f: A => IO[B]): IO[B] = c flatMap f

    override def create[B](b: B): IO[B] = IO(b)
  }


  implicit object NowExecution extends Execution[Now] {
    override def doAndThen[A, B](c: Now[A])(f: A => Now[B]): Now[B] = f(c)

    override def create[B](b: B): Now[B] = b
  }

  def main(args: Array[String]): Unit = {
    val e = echo[IO]
    e.interpret()

    val e2 = echo[Now]
  }

}
