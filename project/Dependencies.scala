import sbt._

object Dependencies {

  case object Versions {
    val apacheHttpComponent = "4.5.13"
    val circe = "0.13.0"
    val sangria = "2.0.0"
    val sangriaCirce = "1.3.0"
    val quicklens = "1.6.1"
    val akkaHttp = "10.2.0"
    val akkaHttpCirce = "1.35.0"
    val akkaCore = "2.6.10"
    val elastic4s = "7.9.1"
    val elastic4sCirce = "6.7.8"
    val jboss = "3.4.1.Final"
    val keycloak = "11.0.2"
    val keycloak4s = "2.2.8"
    val pureconfig = "0.14.0"
    val refined = "0.9.17"
    val scalaTest = "3.2.2"
    val scribe = "3.0.3"
    val sttp = "2.2.8"
  }

  case object com {

    case object beachape {
      val `enumeratum-circe` = "com.beachape" %% "enumeratum-circe" % "1.6.1"
    }

    case object fullfacing {
      val `keycloak4s-core` =
        "com.fullfacing" %% "keycloak4s-core" % Versions.keycloak4s
      val `keycloak4s-admin` =
        "com.fullfacing" %% "keycloak4s-admin" % Versions.keycloak4s
      val `keycloak4s-admin-monix` =
        "com.fullfacing" %% "keycloak4s-admin-monix" % Versions.keycloak4s
      val `keycloak4s-auth-akka-http` =
        "com.fullfacing" %% "keycloak4s-auth-akka-http" % Versions.keycloak4s
    }

    case object github {
      case object pureconfig {
        val pureconfig =
          "com.github.pureconfig" %% "pureconfig" % Versions.pureconfig
      }
    }

    case object outr {
      val scribe = "com.outr" %% "scribe" % Versions.scribe
    }
    case object sksamuel {
      case object elastic4s {
        val `elastic4s-core` =
          "com.sksamuel.elastic4s" %% "elastic4s-core" % Versions.elastic4s
        val `elastic4s-client-esjava` =
          "com.sksamuel.elastic4s" %% "elastic4s-client-esjava" % Versions.elastic4s
        val `elastic4s-circe` =
          "com.sksamuel.elastic4s" %% "elastic4s-circe" % Versions.elastic4sCirce
      }
    }

    case object softwaremill {
      val quicklens =
        "com.softwaremill.quicklens" %% "quicklens" % Versions.quicklens
      case object sttp {
        val `akka-http-backend` =
          "com.softwaremill.sttp.client" %% "akka-http-backend" % Versions.sttp
      }
    }

    case object typesafe {
      case object akka {
        val `akka-actor` =
          "com.typesafe.akka" %% "akka-actor" % Versions.akkaCore
        val `akka-http` =
          "com.typesafe.akka" %% "akka-http" % Versions.akkaHttp
        val `akka-stream` =
          "com.typesafe.akka" %% "akka-stream" % Versions.akkaCore
      }
    }
  }

  case object de {
    case object heikoseeberger {
      val `akka-http-circe` =
        "de.heikoseeberger" %% "akka-http-circe" % Versions.akkaHttpCirce
    }
  }

  case object eu {
    case object timepit {
      val refined = "eu.timepit" %% "refined" % Versions.refined
    }
  }

  case object io {
    case object circe {
      val `circe-generic` = dependency("generic")
      val `circe-parser` = dependency("parser")
      val `circe-optics` = dependency("optics")
      val `circe-refined` = dependency("refined")

      private def dependency(artifact: String): ModuleID =
        "io.circe" %% s"circe-$artifact" % Versions.circe
    }
  }

  case object org {
    case object apache {
      case object httpcomponents {
        val httpclient =
          "org.apache.httpcomponents" % "httpclient" % Versions.apacheHttpComponent
      }
    }

    case object jboss {
      case object logging {
        val `jboss-logging` =
          "org.jboss.logging" % "jboss-logging" % Versions.jboss
      }
    }

    case object keycloak {
      val `keycloak-core` = "org.keycloak" % "keycloak-core" % Versions.keycloak
      val `keycloak-adapter-core` =
        "org.keycloak" % "keycloak-adapter-core" % Versions.keycloak
    }

    case object scalacheck {
      val scalacheck =
        "org.scalacheck" %% "scalacheck" % "1.14.3"
    }

    case object scalatest {
      val scalatest =
        "org.scalatest" %% "scalatest" % Versions.scalaTest
    }

    case object scalatestplus {
      val `scalacheck-1-14` =
        "org.scalatestplus" %% "scalacheck-1-14" % "3.2.2.0"
    }

    case object `sangria-graphql` {
      val sangria = "org.sangria-graphql" %% "sangria" % Versions.sangria
      val `sangria-circe` =
        "org.sangria-graphql" %% "sangria-circe" % Versions.sangriaCirce
    }

    case object typelevel {
      val `cats-core` =
        "org.typelevel" %% "cats-core" % "2.2.0"

      val `cats-effect` =
        "org.typelevel" %% "cats-effect" % "2.2.0"

      val `discipline-scalatest` =
        "org.typelevel" %% "discipline-scalatest" % "2.0.1"

      val `kind-projector` =
        "org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full
    }
  }
}
