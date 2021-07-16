# gnuplot4s
A well typed scala interface to gnuplot

### Docs
API documentation is [here](https://erikerlandson.github.io/gnuplot4s/latest/api/)

### How to use in your project

As of 0.2.0, `gnuplot4s` is published via Sonatype, and so default resolvers will normally be sufficient.
The `gnuplot4s` library is built for Scala 2.12 and 2.13.

```scala
libraryDependencies += "com.manyangled" %% "gnuplot4s" % "0.2.0"
```

The API for `gnuplot4s` `0.2.0` is identical to `0.1.0`, with the exception that the `cats-free` dependency has been upgraded to `2.6.1`,
and it is no longer compiled for Scala 2.11.

### Examples

```scala
scala> import com.manyangled.gnuplot4s._
import com.manyangled.gnuplot4s._

scala> val data = Array((0,0), (1,1), (2,2))
data: Array[(Int, Int)] = Array((0,0), (1,1), (2,2))

scala> val gnuplot = Session().block("data", data).plot(Plot().block("data").style(PlotStyle.Points)).term(Dumb())
gnuplot: com.manyangled.gnuplot4s.Session = Session(Map(data -> BlockRows(<function0>)),Dumb(None,None),Console,Options(None,None,None),Vector(Plot((0,1),Points,data)),/usr/bin/gnuplot)

scala> gnuplot.render

scala> 
                         
                                                                               
    2 +-+--------------+-----------------+----------------+--------------+-A   
      +                +                 +                +                +   
      |                                            $data using 0:1    A    |   
      |                                                                    |   
      |                                                                    |   
  1.5 +-+                                                                +-+   
      |                                                                    |   
      |                                                                    |   
      |                                                                    |   
    1 +-+                                A                               +-+   
      |                                                                    |   
      |                                                                    |   
      |                                                                    |   
      |                                                                    |   
  0.5 +-+                                                                +-+   
      |                                                                    |   
      |                                                                    |   
      |                                                                    |   
      +                +                 +                +                +   
    0 A-+--------------+-----------------+----------------+--------------+-+   
      0               0.5                1               1.5               2   

scala> val gnuplot = Session().block("data", data).plot(Plot().block("data").style(PlotStyle.LinesPoints)).term(Dumb())
gnuplot: com.manyangled.gnuplot4s.Session = Session(Map(data -> BlockRows(<function0>)),Dumb(None,None),Console,Options(None,None,None),Vector(Plot((0,1),LinesPoints,data)),/usr/bin/gnuplot)

scala> gnuplot.render

scala> 

                                                                               
    2 +-+--------------+-----------------+----------------+--------------+*A   
      +                +                 +                +           **** +   
      |                                            $data using 0:1****A*** |   
      |                                                       ****         |   
      |                                                   ****             |   
  1.5 +-+                                              ***               +-+   
      |                                            ****                    |   
      |                                        ****                        |   
      |                                    ****                            |   
    1 +-+                               *A*                              +-+   
      |                             ****                                   |   
      |                          ***                                       |   
      |                      ****                                          |   
      |                   ***                                              |   
  0.5 +-+             ****                                               +-+   
      |            ***                                                     |   
      |        ****                                                        |   
      |     ***                                                            |   
      + ****           +                 +                +                +   
    0 A*+--------------+-----------------+----------------+--------------+-+   
      0               0.5                1               1.5               2   
                                                                               
```
