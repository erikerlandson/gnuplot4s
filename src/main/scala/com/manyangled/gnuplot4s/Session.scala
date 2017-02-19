package com.manyangled.gnuplot4s

case class Session[T <: TermLike[T], P <: PlotLike[P]](
  term: T,
  plot: P,
  opt: Session.Options
) {
  def withTerm[T2 <: TermLike[T2]](t2: T2) = Session(t2, plot, opt)
  def withPlot[P2 <: PlotLike[P2]](p2: P2) = Session(term, p2, opt)
  def withTitle(title: String) = this.copy(opt = opt.copy(title = Some(title)))
  def withXLabel(xLabel: String) = this.copy(opt = opt.copy(xLabel = Some(xLabel)))
  def withYLabel(yLabel: String) = this.copy(opt = opt.copy(yLabel = Some(yLabel)))
}

object Session {
  case class Options(
    title: Option[String],
    xLabel: Option[String],
    yLabel: Option[String]
  )
  object Options {
    def build = Options(None, None, None)
  }

  def build = Session(
    Dumb.build,
    NoPlot(),
    Options.build
  )
}
