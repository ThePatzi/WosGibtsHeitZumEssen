import sbt.Keys._

name := "WosGibtsHeitZumEssen"

version := "1.0"

val scalaV = "2.11.8"

val http4sVersion = "0.15.0a-SNAPSHOT"

resolvers += Resolver.sonatypeRepo("snapshots")
//resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

lazy val shared = project.in(file("shared"))
  .settings(
    scalaVersion := scalaV,
    name := "shared",
    libraryDependencies ++= Seq(
      "com.google.firebase" % "firebase-server-sdk" % "3.0.1" withSources() withJavadoc(),
      "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.8.2" withSources() withJavadoc(),
      "org.slf4j" % "slf4j-api" % "1.7.21" withSources() withJavadoc(),
      "org.slf4j" % "slf4j-simple" % "1.7.21" withSources() withJavadoc()
    )
  )

lazy val refresher = project.in(file("refresher"))
  .settings(
    scalaVersion := scalaV,
    name := "refresher",
    resolvers += Resolver.jcenterRepo,
    resolvers += Resolver.sonatypeRepo("snapshots"),
    libraryDependencies ++= Seq(
      "net.ruippeixotog" %% "scala-scraper" % "1.0.0" withSources() withJavadoc()
    ),
    mainClass in assembly := Some("com.pichler.wosgibtsheitzumessen.refresher.Main"),
    assemblyJarName in assembly := "WosGibtsHeitZumEssen_refresher.jar"
  )
  .dependsOn(shared)
  .enablePlugins(DockerPlugin)
  .settings(
    dockerfile in docker := {
      val artifact: File = assembly.value
      val artifactTargetPath = s"/app/${artifact.name}"
      new Dockerfile {
        from("java:8")
        add(artifact, artifactTargetPath)
        entryPoint("java", "-jar", artifactTargetPath)
      }
    },
    imageNames in docker := Seq(
      ImageName("patzi/wosgibtsheitzumessen_refresher:latest")
    )
  )

lazy val server = project.in(file("server"))
  .settings(
    scalaVersion := scalaV,
    name := "server",
    resolvers += Resolver.jcenterRepo,
    resolvers += Resolver.sonatypeRepo("snapshots"),
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % http4sVersion
    ),
    mainClass in assembly := Some("com.pichler.wosgibtsheitzumessen.server.api.Main"),
    assemblyJarName in assembly := "WosGibtsHeitZumEssen_server.jar",
    assemblyMergeStrategy in assembly := {
      case PathList(xs@_*) if xs.contains("opuswrapper") || xs.contains("tritonus") => MergeStrategy.last // needed to have both JDA and D4J at the same time
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  )
  .dependsOn(shared)
  .enablePlugins(DockerPlugin)
  .settings(
    dockerfile in docker := {
      val artifact: File = assembly.value
      val artifactTargetPath = s"/app/${artifact.name}"
      new Dockerfile {
        from("java:8")
        add(artifact, artifactTargetPath)
        entryPoint("java", "-jar", artifactTargetPath)
      }
    },
    imageNames in docker := Seq(
      ImageName("patzi/wosgibtsheitzumessen_server:latest")
    )
  )

lazy val telegramBot = project.in(file("telegramBot"))
  .settings(
    scalaVersion := scalaV,
    name := "server",
    resolvers += "jitpack" at "https://jitpack.io",
    libraryDependencies += "com.github.mukel" %% "telegrambot4s" % "v1.2.1",

    mainClass in assembly := Some("com.pichler.wosgibtsheitzumessen.telegrambot.Main"),
    assemblyJarName in assembly := "WosGibtsHeitZumEssen_telegrambot.jar",
    assemblyMergeStrategy in assembly := {
      case PathList(xs@_*) if xs.contains("opuswrapper") || xs.contains("tritonus") => MergeStrategy.last // needed to have both JDA and D4J at the same time
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  )
  .dependsOn(shared)
  .enablePlugins(DockerPlugin)
  .settings(
    dockerfile in docker := {
      val artifact: File = assembly.value
      val artifactTargetPath = s"/app/${artifact.name}"
      new Dockerfile {
        from("java:8")
        add(artifact, artifactTargetPath)
        entryPoint("java", "-jar", artifactTargetPath)
      }
    },
    imageNames in docker := Seq(
      ImageName("patzi/wosgibtsheitzumessen_telegrambot:latest")
    )
  )

lazy val root = project.in(file("."))
  .aggregate(shared, refresher, server, telegramBot)
  .settings(
    scalaVersion := scalaV,
    name := "root"
  )
