package com.manyangled.gnuplot4s

import cats.free.Free
import cats.Functor

sealed trait GPScript[+Next]

case class BlockRows(rows: () => Iterator[String])

object GPScript {
  case class End() extends GPScript[Nothing]
  case class Str[Next](str: String, next: Next) extends GPScript[Next]
  case class Opt[A, Next](opt: Option[A], onValue: A => String, onNone: () => Option[String], next: Next) extends GPScript[Next]
  case class Data[Next](blks: Map[String, BlockRows], next: Next) extends GPScript[Next]
  case class Clause[Next](lines: () => Iterator[String], next: Next) extends GPScript[Next]
  case class Sub[NextS, Next](sub: Free[GPScript, NextS], next: Next) extends GPScript[Next]

  implicit val optionRunFunctor: Functor[GPScript] = new Functor[GPScript] {
    def map[A, B](a: GPScript[A])(f: A => B): GPScript[B] = a match {
      case Str(str, next) => Str(str, f(next))
      case Opt(opt, onv, ond, next) => Opt(opt, onv, ond, f(next))
      case Data(blks, next) => Data(blks, f(next))
      case Clause(lines, next) => Clause(lines, f(next))
      case Sub(sub, next) => Sub(sub, f(next))
      case End() => End()
    }
  }

  def end(): Free[GPScript, Unit] = Free.liftF[GPScript, Unit](End())

  def str(s: => String): Free[GPScript, Unit] = Free.liftF[GPScript, Unit](Str(s, ()))

  def opt[A](o: Option[A])(onv: A => String, ond: => Option[String] = { None }): Free[GPScript, Unit] =
    Free.liftF[GPScript, Unit](Opt(o, onv, () => ond, ()))

  def data(blks: Map[String, BlockRows]): Free[GPScript, Unit] =
    Free.liftF[GPScript, Unit](Data(blks, ()))

  def clause(lines: () => Iterator[String]): Free[GPScript, Unit] =
    Free.liftF[GPScript, Unit](Clause(lines, ()))

  def sub[N](sub: Free[GPScript, N]): Free[GPScript, Unit] =
    Free.liftF[GPScript, Unit](Sub(sub, ()))

  def run[Next, U](prog: Free[GPScript, Next], f: String => U = print(_)): Unit =
    prog.fold({ _: Next => () }, {
      case Str(str, next) => {
        f(str)
        run(next, f)
      }
      case Opt(opt, onv, ond, next) => {
        opt.fold(ond().foreach(f)){ v => f(onv(v)); () }
        run(next, f)
      }
      case Data(blks, next) => {
        blks.foreach { case (n, br) =>
          f(s"$$$n << EOD\n")
          br.rows().foreach(f)
          f("EOD\n")
        }
        run(next, f)
      }
      case Clause(lines, next) => {
        lines().foreach(f)
        run(next, f)
      }
      case Sub(sub, next) => {
        run(sub, f)
        run(next, f)
      }
      case End() => {
        ()
      }
    })
}
