import Dependencies.{ io, _ }
import Util._

ThisBuild / organization := "agh.edu.pl"
ThisBuild / scalaVersion := "2.13.3"
ThisBuild / version := "1.1"

ThisBuild / scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-language:_",
  "-unchecked",
  "-Wunused:_",
  "-Wvalue-discard",
  "-Xfatal-warnings",
  "-Ymacro-annotations"
)

lazy val `challenge` =
  project
    .in(file("."))
    .aggregate(
      domain,
      persistence,
      core,
      delivery,
      main
    )

lazy val domain =
  project
    .in(file("domain"))
    .settings(commonSettings: _*)
    .settings(
      libraryDependencies ++= Seq(
        eu.timepit.refined,
        org.`sangria-graphql`.sangria,
        org.`sangria-graphql`.`sangria-circe`,
        io.circe.`circe-refined`
      )
    )

lazy val core =
  project
    .in(file("core"))
    .dependsOn(persistence % Cctt)
    .settings(commonSettings: _*)
    .settings(
      libraryDependencies ++= Seq(
        org.typelevel.`cats-core`,
        org.`sangria-graphql`.sangria,
        org.`sangria-graphql`.`sangria-circe`
      ),
      libraryDependencies ++= Seq(
        org.scalacheck.scalacheck,
        org.scalatest.scalatest,
        org.scalatestplus.`scalacheck-1-14`,
        org.typelevel.`discipline-scalatest`
      ).map(_ % Test)
    )

lazy val delivery =
  project
    .in(file("delivery"))
    .dependsOn(core % Cctt)
    .settings(commonSettings: _*)
    .settings(
      libraryDependencies ++= Seq(
        com.fullfacing.`keycloak4s-core`,
        com.fullfacing.`keycloak4s-admin`,
        com.fullfacing.`keycloak4s-admin-monix`,
        com.fullfacing.`keycloak4s-auth-akka-http`,
        com.softwaremill.sttp.`akka-http-backend`,
        com.typesafe.akka.`akka-http`,
        com.typesafe.akka.`akka-stream`,
        de.heikoseeberger.`akka-http-circe`,
        org.apache.httpcomponents.httpclient,
        org.keycloak.`keycloak-core`,
        org.keycloak.`keycloak-adapter-core`,
        org.jboss.logging.`jboss-logging`
      )
    )

lazy val persistence =
  project
    .in(file("persistence"))
    .dependsOn(domain % Cctt)
    .settings(commonSettings: _*)
    .settings(
      libraryDependencies ++= Seq(
        org.typelevel.`cats-effect`,
        com.sksamuel.elastic4s.`elastic4s-core`,
        com.sksamuel.elastic4s.`elastic4s-circe`,
        com.sksamuel.elastic4s.`elastic4s-client-esjava`
      )
    )

lazy val main =
  project
    .in(file("main"))
    .dependsOn(delivery % Cctt)
    .dependsOn(persistence % Cctt)
    .enablePlugins(sbtdocker.DockerPlugin, JavaAppPackaging)
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
        com.github.pureconfig.pureconfig
      )
    )
    .settings(dockerBaseImage := "robsonoduarte/8-jre-alpine-bash:latest")
    .settings(dockerExposedPorts := Seq(8080))
    .settings(dockerUsername := Some("kamilskomro"))
    .settings(packageName := "challenge-backend")

lazy val baseLibraries = Seq(
  com.beachape.`enumeratum-circe`,
  com.outr.scribe,
  com.softwaremill.quicklens,
  com.typesafe.akka.`akka-actor-typed`,
  io.circe.`circe-generic`,
  io.circe.`circe-parser`,
  io.circe.`circe-optics`,
  org.scalacheck.scalacheck,
  org.scalatest.scalatest,
  org.scalatestplus.`scalacheck-1-14`,
  org.typelevel.`discipline-scalatest`
)

lazy val commonSettings = Seq(
  addCompilerPlugin(org.typelevel.`kind-projector`),
  update / evictionWarningOptions := EvictionWarningOptions.empty,
  libraryDependencies ++= baseLibraries,
  libraryDependencies ++= baseLibraries.map(_ % Test),
  Compile / console / scalacOptions --= Seq(
    "-Wunused:_",
    "-Xfatal-warnings"
  ),
  Test / console / scalacOptions :=
    (Compile / console / scalacOptions).value
)

lazy val effects = Seq(
  org.typelevel.`cats-effect`
)
