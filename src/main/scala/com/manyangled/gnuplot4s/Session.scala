package com.manyangled.gnuplot4s

case class Session(
  blks: Map[String, BlockRows],
  trm: TermInterface,
  out: Session.Output,
  opt: Session.Options,
  plots: Vector[PlotInterface],
  gpcmd: String
) {
  def gnuplot(cmd: String) = this.copy(gpcmd = cmd)
  def term(t2: TermInterface) = this.copy(trm = t2)
  def output(o: Session.Output) = this.copy(out = o)
  def plot() = this.copy(plots = Vector.empty[PlotInterface])
  def plot(p: PlotInterface) = this.copy(plots = this.plots :+ p)
  def title(title: String) = this.copy(opt = opt.copy(title = Some(title)))
  def xLabel(xLabel: String) = this.copy(opt = opt.copy(xLabel = Some(xLabel)))
  def yLabel(yLabel: String) = this.copy(opt = opt.copy(yLabel = Some(yLabel)))
  def block() = this.copy(blks = Map.empty[String, BlockRows])
  def block[R](name: String, data: Seq[R])(implicit toStrSeq: ToStringSeq[R]) = {
    val br = BlockRows(() => { data.toIterator.map { r => toStrSeq(r).mkString(" ") + "\n" }  })
    this.copy(blks = this.blks + (name -> br))
  }
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

  trait Output
  object Output {
    case object Console extends Output
    case object Collect extends Output
    case class File(fname: String) extends Output
    case class Pipe(cmd: String) extends Output
  }

  def apply(): Session = build
  def build = Session(
    Map.empty[String, BlockRows],
    Dumb.build,
    Output.Console,
    Options.build,
    Vector.empty[PlotInterface],
    "/usr/bin/gnuplot"
  )
}
