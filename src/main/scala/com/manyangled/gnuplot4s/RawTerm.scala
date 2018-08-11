package com.manyangled.gnuplot4s

case class RawTerm(termClause: Option[String]) extends TermInterface {
  def clause(cl: String) = this.copy(termClause = Some(cl))
  def sessionClause = () => {
    termClause.map { cl => Iterator(cl + "\n") }.getOrElse(Iterator.empty)
  }
}

object RawTerm {
  def apply(cl: String): RawTerm = RawTerm(Some(cl))
  def apply(): RawTerm = build
  def build = RawTerm(None)
}
