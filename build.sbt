// xsbt clean unidoc previewSite
// xsbt clean unidoc ghpagesPushSite
name := "gnuplot4s"

organization := "com.manyangled"

version := "0.2.0"

scalaVersion := "2.13.6"

crossScalaVersions := Seq("2.12.14", "2.13.6")

pomIncludeRepository := { _ => false }

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

licenses += ("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0"))

homepage := Some(url("https://github.com/erikerlandson/gnuplot4s/"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/erikerlandson/gnuplot4s.git"),
    "scm:git@github.com:erikerlandson/gnuplot4s.git"
  )
)

developers := List(
  Developer(
    id    = "erikerlandson",
    name  = "Erik Erlandson",
    email = "eje@redhat.com",
    url   = url("https://erikerlandson.github.io/")
  )
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-free" % "2.6.1"
)

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

scalacOptions in (Compile, doc) ++= Seq("-doc-root-content", baseDirectory.value+"/root-doc.txt")

enablePlugins(ScalaUnidocPlugin, GhpagesPlugin)

siteSubdirName in ScalaUnidoc := "latest/api"

addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), siteSubdirName in ScalaUnidoc)

git.remoteRepo := "git@github.com:erikerlandson/gnuplot4s.git"
