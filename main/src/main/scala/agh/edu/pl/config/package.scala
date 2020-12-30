package agh.edu.pl

import com.fullfacing.keycloak4s.core.models._
import org.keycloak.representations.adapters.config.AdapterConfig
import scala.jdk.CollectionConverters._

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

    def toConfigWithAuth: ConfigWithAuth =
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

    def buildAdapterConfig: AdapterConfig = {
      val adapterConfig = new AdapterConfig
      val credentials: Map[String, AnyRef] = Map("secret" -> clientSecret)

      adapterConfig.setRealm(realm)
      adapterConfig.setResource(clientId)
      adapterConfig.setAuthServerUrl(s"http://$host:$port/auth")
      adapterConfig.setSslRequired("external")
      adapterConfig.setCredentials(credentials.asJava)

      adapterConfig
    }
  }
}
