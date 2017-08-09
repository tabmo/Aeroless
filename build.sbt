name := """aeroless"""

organization := "io.tabmo"

scalaVersion := "2.11.11"
crossScalaVersions := Seq("2.11.11", "2.12.3")
scalacOptions ++= Seq(
  "-deprecation", // Warn when deprecated API are used
  "-feature", // Warn for usages of features that should be importer explicitly
  "-unchecked", // Warn when generated code depends on assumptions
  "-Ywarn-dead-code", // Warn when dead code is identified
  "-Ywarn-numeric-widen", // Warn when numeric are widened
  "-Xlint", // Additional warnings (see scalac -Xlint:help)
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receive
  "-language:postfixOps",
  "-language:implicitConversions",
  "-language:reflectiveCalls",
  "-language:existentials",
  "-language:higherKinds",
  "-language:experimental.macros"
)

libraryDependencies ++= (CrossVersion.partialVersion(scalaVersion.value) match {
  case Some((2, 11)) => List("io.tabmo" %% "reactive-aerospike" % "2.0.0" % Compile)
  case Some((2, 12)) => List("io.tabmo" %% "reactive-aerospike" % "3.0.0" % Compile)
  case _ => Nil
})

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.3.2",
  "org.scalatest" %% "scalatest" % "3.0.1" % Test
)


parallelExecution in Test := false
fork in Test := false

/*
 * Publish to tabmo organization on bintray
 */
licenses += ("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0"))

bintrayOrganization := Some("tabmo")
releaseCrossBuild := true

// Exclude logback file
mappings in(Compile, packageBin) ~= {
  _.filter(!_._1.getName.endsWith(".xml"))
}

