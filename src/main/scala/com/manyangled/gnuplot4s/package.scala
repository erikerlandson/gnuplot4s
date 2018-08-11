package com.manyangled

package object gnuplot4s {
  import cats.free.Free

  trait TermInterface {
    def sessionClause: () => Iterator[String]
  }

  trait PlotInterface {
    def plotClause: () => Iterator[String]
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

  implicit class EnrichedSession(session: Session) {
    import scala.sys.process._

    def render: Unit = {
      val prog = this.program
      val io = new ProcessIO({ in: java.io.OutputStream =>
        GPScript.run(prog, { s => {
          in.write(s.getBytes())
        } })
        in.close()
      },
      { out: java.io.InputStream =>
        val isr = new java.io.InputStreamReader(out)
        val bufReader = new java.io.BufferedReader(isr)
        var line = bufReader.readLine()
        while (line != null) {
          println(line)
          line = bufReader.readLine()
        }
        bufReader.close()
        out.close()
      },
      { err => () })
      val cmd = List(session.gpcmd)
      cmd.run(io)
    }

    def print: Unit = GPScript.run(this.program)

    def program: Free[GPScript, Unit] = {
      for {
        _ <- GPScript.data(session.blks);
        _ <- GPScript.clause(session.trm.sessionClause);
        _ <- GPScript.opt(session.opt.title) { t => s"""set title "${t}"\n""" };
        _ <- GPScript.opt(session.opt.xLabel) { t => s"""set xlabel "${t}"\n""" };
        _ <- GPScript.opt(session.opt.yLabel) { t => s"""set ylabel "${t}"\n""" };
        _ <- GPScript.clause(Plot.plotClause(session.plots));
        _ <- GPScript.end()
      } yield ()  
    }
  }
}
