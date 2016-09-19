name := "WosGibtsHeitZumEssen"

version := "1.0"

scalaVersion := "2.11.8"

val http4sVersion = "0.15.0a-SNAPSHOT"

resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion
)

libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "1.0.0"
libraryDependencies += "com.fasterxml.jackson.module" % "jackson-module-scala" % "2.0.2"