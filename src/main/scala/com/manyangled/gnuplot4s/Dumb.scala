package com.manyangled.gnuplot4s

case class Dumb(size: Option[(Int, Int)], aspect: Option[(Int, Int)]) extends TermLike[Dumb] {
  require(size.fold(true) { s => s._1 > 0 && s._2 > 0 })
  require(aspect.fold(true) { a => a._1 > 0 && a._2 > 0 })
  def withSize(w: Int, h: Int) = this.copy(size = Some((w, h)))
  def withSize(s: Int) = this.copy(size = Some((s, s)))
  def withAspect(h: Int, v: Int) = this.copy(aspect = Some((h, v)))
  def withAspect(h: Int) = this.copy(aspect = Some((h, 1)))
  def sessionSub = for {
    _ <- GPScript.str("set terminal dumb")
    _ <- GPScript.opt(size) { s => s" size ${s._1},${s._2}" }
    _ <- GPScript.opt(aspect) { a => s" aspect ${a._1},${a._2}" }
    _ <- GPScript.str("\n")
    _ <- GPScript.end()
  } yield ()
}

object Dumb {
  def build = Dumb(None, None)
}
