package com.manyangled

package object gnuplot4s {
  import cats.free.Free

  trait TermLike[T <: TermLike[T]] { self: T =>
    def sessionSub: Free[GPScript, Unit]
  }

  trait Plottable

  trait PlotLike[P <: PlotLike[P]] { self: P =>
    def sessionSub: Free[GPScript, Unit]
    def plotSub: Free[GPScript, Unit]
  }
  case class NoPlot() extends PlotLike[NoPlot] {
    def sessionSub = for { _ <- GPScript.end() } yield ()
    def plotSub = for { _ <- GPScript.end() } yield ()
  }

  trait ToString[T] {
    def apply(t: T): String
  }

  object ToString {
    implicit def implicitProductToString[P <: Product]: ToString[P] = new ToString[P] {
      def apply(p: P) = s""""${p}""""
    }

    implicit val implicitStringToStr: ToString[String] = new ToString[String] {
      def apply(s: String) = s""""${s}""""
    }

    implicit def implicitNumericToStr[N](implicit n: Numeric[N]): ToString[N] = new ToString[N] {
      def apply(n: N) = n.toString
    }
  }

  trait ToStringSeq[T] {
    def apply(t: T): Seq[String]
  }

  implicit val implicitToStrSeqString: ToStringSeq[String] = new ToStringSeq[String] {
    def apply(s: String) = Vector(s""""${s}"""")
  }

  implicit def implicitToStrNumeric[N](implicit num: Numeric[N]): ToStringSeq[N] = new ToStringSeq[N] {
    def apply(n: N) = Vector(n.toString)
  }

  implicit def implicitToStrTuple2[T1, T2](implicit ts1: ToString[T1], ts2: ToString[T2]):
      ToStringSeq[(T1, T2)] = new ToStringSeq[(T1, T2)] {
    def apply (t: (T1, T2)) = Vector(ts1(t._1), ts2(t._2))
  }

  implicit class EnrichedSession[T <: TermLike[T], P <: PlotLike[P] with Plottable](session: Session[T, P]) {
    import scala.sys.process._

    def plot[R](data: TraversableOnce[R])(implicit toStrSeq: ToStringSeq[R]): Unit = {
      val prog = for {
        _ <- GPScript.data(data)(
          { Some("$data << EOD\n") },
          { r => toStrSeq(r).mkString(" ") + "\n" },
          { Some("EOD\n") });
        _ <- GPScript.sub(session.term.sessionSub);
        _ <- GPScript.opt(session.opt.title) { t => s"""set title "${t}"\n""" };
        _ <- GPScript.opt(session.opt.xLabel) { t => s"""set xlabel "${t}"\n""" };
        _ <- GPScript.opt(session.opt.yLabel) { t => s"""set ylabel "${t}"\n""" };
        _ <- GPScript.sub(session.plot.sessionSub);
        _ <- GPScript.sub(session.plot.plotSub);
        _ <- GPScript.end()
      } yield ()
      val io = scala.sys.process.BasicIO.standard({ in =>
        GPScript.run(prog, { s => {
          in.write(s.getBytes())
        } })
        in.close()
      })
      val cmd = List("/usr/bin/gnuplot")
      cmd.run(io)
    }
  }
}
