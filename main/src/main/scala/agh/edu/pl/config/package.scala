package agh.edu.pl

import com.fullfacing.keycloak4s.core.models._

package object config {
  case class ServiceConfig(
      server: ServerConfig,
      es: EsConfig,
      keycloak: KeycloakConf
    )

  case class ServerConfig(
      bindHost: String,
      bindPort: Int
    )

  case class EsConfig(
      host: String,
      port: String
    )

  case class KeycloakConf(
      scheme: String,
      host: String,
      port: Int,
      realm: String,
      clientId: String,
      clientSecret: String
    ) {

    def toConfingWithAuth: ConfigWithAuth =
      ConfigWithAuth(
        scheme = scheme,
        host = host,
        port = port,
        realm = realm,
        authn = KeycloakConfig.Secret(
          realm = realm,
          clientId = clientId,
          clientSecret = clientSecret
        )
      )
  }
}
