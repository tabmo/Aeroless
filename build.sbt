name := """aeroless"""

organization := "io.tabmo"

scalaVersion := "2.11.8"

scalacOptions ++= Seq(
  //"-Ypartial-unification", // enable fix for SI-2712
  //"-Yliteral-types",       // enable SIP-23 implementation
  "-deprecation",           // Warn when deprecated API are used
  "-feature",               // Warn for usages of features that should be importer explicitly
  "-unchecked",             // Warn when generated code depends on assumptions
  "-Ywarn-dead-code",       // Warn when dead code is identified
  "-Ywarn-numeric-widen",   // Warn when numeric are widened
  "-Xlint",                 // Additional warnings (see scalac -Xlint:help)
  "-Ywarn-adapted-args",    // Warn if an argument list is modified to match the receive
  "-language:postfixOps",
  "-language:implicitConversions",
  "-language:reflectiveCalls",
  "-language:existentials",
  "-language:higherKinds",
  "-language:experimental.macros"
)

libraryDependencies ++= Seq(
  "io.tabmo" %% "reactive-aerospike" % "1.0.9",
  "com.chuusai" %% "shapeless" % "2.3.2",
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"
)

parallelExecution in Test := false
fork in Test := false

/*
 * Publish to tabmo organization on bintray
 */
licenses += ("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0"))

bintrayOrganization := Some("tabmo")

//scalaOrganization := "org.typelevel"

// Exclude logback file
mappings in (Compile, packageBin) ~= { _.filter(!_._1.getName.endsWith(".xml")) }

