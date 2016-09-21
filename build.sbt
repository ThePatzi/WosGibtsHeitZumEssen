name := "WosGibtsHeitZumEssen"

version := "1.0"

scalaVersion := "2.11.8"

val http4sVersion = "0.15.0a-SNAPSHOT"

resolvers += Resolver.sonatypeRepo("snapshots")
//resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion
)

libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "1.0.0" withSources() withJavadoc()
libraryDependencies += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.8.2" withSources() withJavadoc()
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.21" withSources() withJavadoc()
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.21" withSources() withJavadoc()
libraryDependencies += "com.google.firebase" % "firebase-server-sdk" % "3.0.1" withSources() withJavadoc()