package com.manyangled.gnuplot4s

case class Dumb(siz: Option[(Int, Int)], asp: Option[(Int, Int)]) extends TermInterface {
  require(siz.fold(true) { s => s._1 > 0 && s._2 > 0 })
  require(asp.fold(true) { a => a._1 > 0 && a._2 > 0 })
  def size(w: Int, h: Int) = this.copy(siz = Some((w, h)))
  def size(s: Int) = this.copy(siz = Some((s, s)))
  def aspect(h: Int, v: Int) = this.copy(asp = Some((h, v)))
  def aspect(h: Int) = this.copy(asp = Some((h, 1)))
  def sessionClause = () => {
    Iterator("set terminal dumb") ++
      (siz.map { s => Iterator(s" size ${s._1},${s._2}") }.getOrElse(Iterator.empty)) ++
      (asp.map { a => Iterator(s" aspect ${a._1},${a._2}") }.getOrElse(Iterator.empty)) ++
      Iterator("\n")
  }
}

object Dumb {
  def build = Dumb(None, None)
}
