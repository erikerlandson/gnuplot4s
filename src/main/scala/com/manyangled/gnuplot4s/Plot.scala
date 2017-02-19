package com.manyangled.gnuplot4s

sealed trait PlotStyle

object PlotStyle {
  case object Lines extends PlotStyle
  case object Points extends PlotStyle
  case object LinesPoints extends PlotStyle
  def str(ps: PlotStyle) = ps match {
    case Lines => "lines"
    case Points => "points"
    case LinesPoints => "linespoints"
  }
}

case class Plot(usin: (Int, Int), style: PlotStyle) extends PlotLike[Plot] with Plottable {
  require(usin._1 >= 0 && usin._2 >= 1)
  def using(xc: Int, yc: Int) = this.copy(usin = (xc, yc))
  def using(yc: Int) = this.copy(usin = (0, yc))
  def withStyle(ps: PlotStyle) = this.copy(style = ps)
  def sessionSub = for {
    _ <- GPScript.end()
  } yield ()
  def plotSub = for {
    _ <- GPScript.str("plot $data");
    _ <- GPScript.str(s" using ${usin._1}:${usin._2}");
    _ <- GPScript.str(s" with ${PlotStyle.str(style)}");
    _ <- GPScript.str("\n");
    _ <- GPScript.end()
  } yield ()
}

object Plot {
  def build = Plot((0, 1), PlotStyle.LinesPoints)
}
