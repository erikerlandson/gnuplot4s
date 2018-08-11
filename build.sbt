// xsbt clean unidoc previewSite
// xsbt clean unidoc ghpagesPushSite
name := "gnuplot4s"

organization := "com.manyangled"

version := "0.1.0"

scalaVersion := "2.11.12"

crossScalaVersions := Seq("2.11.12", "2.12.6")

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-free" % "0.9.0",
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)

licenses += ("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0"))

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

scalacOptions in (Compile, doc) ++= Seq("-doc-root-content", baseDirectory.value+"/root-doc.txt")

enablePlugins(ScalaUnidocPlugin, GhpagesPlugin)

siteSubdirName in ScalaUnidoc := "latest/api"

addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), siteSubdirName in ScalaUnidoc)

git.remoteRepo := "git@github.com:erikerlandson/gnuplot4s.git"
