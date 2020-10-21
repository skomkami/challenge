import Dependencies.{ io, _ }
import Util._

ThisBuild / organization := "agh.edu.pl"
ThisBuild / scalaVersion := "2.13.3"
ThisBuild / version := "0.0.1-SNAPSHOT"

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
        com.github.alexarchambault.`scalacheck-shapeless_1.14`,
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
        com.typesafe.akka.`akka-http`,
        com.typesafe.akka.`akka-stream`,
        de.heikoseeberger.`akka-http-circe`
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
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
        com.github.pureconfig.pureconfig,
        org.slf4j.`slf4j-simple`
      )
    )

lazy val baseLibraries = Seq(
  com.beachape.`enumeratum-circe`,
  com.github.alexarchambault.`scalacheck-shapeless_1.14`,
  com.softwaremill.quicklens,
  io.circe.`circe-generic`,
  io.circe.`circe-parser`,
  io.circe.`circe-optics`,
  org.scalacheck.scalacheck,
  org.scalatest.scalatest,
  org.scalatestplus.`scalacheck-1-14`,
  org.typelevel.`discipline-scalatest`
)

lazy val commonSettings = Seq(
  addCompilerPlugin(com.olegpy.`better-monadic-for`),
  addCompilerPlugin(org.augustjune.`context-applied`),
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
  dev.zio.`zio-interop-cats`,
  dev.zio.zio,
  io.monix.`monix-eval`,
  org.typelevel.`cats-effect`
)
