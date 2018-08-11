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

case class Plot(usin: (Int, Int), styl: PlotStyle, blk: String) extends PlotInterface {
  require(usin._1 >= 0 && usin._2 >= 1)
  def using(xc: Int, yc: Int) = this.copy(usin = (xc, yc))
  def using(yc: Int) = this.copy(usin = (0, yc))
  def style(ps: PlotStyle) = this.copy(styl = ps)
  def block(b: String) = this.copy(blk = b)
  def plotClause = () => {
    List(
      s" $$$blk",
      s" using ${usin._1}:${usin._2}",
      s" with ${PlotStyle.str(styl)}"
    ).toIterator
  }
}

object Plot {
  def apply(): Plot = build
  def build = Plot((0, 1), PlotStyle.LinesPoints, "data")

  def plotClause(plots: Seq[PlotInterface]): () => Iterator[String] = {
    if (plots.length < 1) {
      () => { Iterator.empty }
    } else {
      () => {
        val z = Iterator("plot") ++ ((plots.head.plotClause)())
        val r = plots.tail.map(_.plotClause).foldLeft(z) { case (t, c) =>
          t ++ Iterator(", ") ++ (c())
        }
        r ++ Iterator("\n")
      }
    }
  }
}
