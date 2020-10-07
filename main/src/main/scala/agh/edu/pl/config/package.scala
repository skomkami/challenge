package agh.edu.pl

package object config {
  case class ServiceConfig(
      server: ServerConfig,
      es: EsConfig
    )

  case class ServerConfig(
      bindHost: String,
      bindPort: Int
    )

  case class EsConfig(
      host: String,
      port: String
    )

}
