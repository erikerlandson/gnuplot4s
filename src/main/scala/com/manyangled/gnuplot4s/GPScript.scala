package com.manyangled.gnuplot4s

import cats.free.Free
import cats.Functor

sealed trait GPScript[+Next]

object GPScript {
  case class End() extends GPScript[Nothing]
  case class Str[Next](str: String, next: Next) extends GPScript[Next]
  case class Opt[A, Next](opt: Option[A], onValue: A => String, onNone: () => Option[String], next: Next) extends GPScript[Next]
  case class Data[R, Next](
    data: TraversableOnce[R],
    onBeg: () => Option[String],
    onRow: R => String,
    onEnd: () => Option[String],
    next: Next) extends GPScript[Next]
  case class Sub[NextS, Next](sub: Free[GPScript, NextS], next: Next) extends GPScript[Next]

  implicit val optionRunFunctor: Functor[GPScript] = new Functor[GPScript] {
    def map[A, B](a: GPScript[A])(f: A => B): GPScript[B] = a match {
      case Str(str, next) => Str(str, f(next))
      case Opt(opt, onv, ond, next) => Opt(opt, onv, ond, f(next))
      case Data(data, onBeg, onRow, onEnd, next) => Data(data, onBeg, onRow, onEnd, f(next))
      case Sub(sub, next) => Sub(sub, f(next))
      case End() => End()
    }
  }

  def end(): Free[GPScript, Unit] = Free.liftF[GPScript, Unit](End())

  def str(s: => String): Free[GPScript, Unit] = Free.liftF[GPScript, Unit](Str(s, ()))

  def opt[A](o: Option[A])(onv: A => String, ond: => Option[String] = { None }): Free[GPScript, Unit] =
    Free.liftF[GPScript, Unit](Opt(o, onv, () => ond, ()))

  def data[R](data: TraversableOnce[R])(
    onBeg: => Option[String] = { None },
    onRow: R => String = (r: R) => r.toString,
    onEnd: => Option[String] = { None }): Free[GPScript, Unit] =
    Free.liftF[GPScript, Unit](Data(data, () => onBeg, onRow, () => onEnd, ()))

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
      case Data(data, onBeg, onRow, onEnd, next) => {
        onBeg().foreach(f)
        data.foreach { r => f(onRow(r)) }
        onEnd().foreach(f)
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
