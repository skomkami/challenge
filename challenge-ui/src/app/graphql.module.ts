import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import { NgModule } from '@angular/core';
import { APOLLO_OPTIONS } from 'apollo-angular';
import {
  ApolloClientOptions,
  ApolloLink,
  InMemoryCache,
} from '@apollo/client/core';
import { HttpLink } from 'apollo-angular/http';
import { HttpHeaders } from '@angular/common/http';
import { DefaultOptions } from '@apollo/client/core/ApolloClient';

const uri = 'http://localhost:8080/graphql';
export function createApollo(
  httpLink: HttpLink,
  keycloak: KeycloakService
): ApolloClientOptions<any> {
  const http = httpLink.create({ uri });
  const cache = new InMemoryCache();

  const defaultOptions: DefaultOptions = {
    watchQuery: {
      fetchPolicy: 'no-cache',
    },
    query: {
      fetchPolicy: 'no-cache',
      errorPolicy: 'all',
    },
  };

  if (keycloak && keycloak.isLoggedIn) {
    const middleware = new ApolloLink((operation, forward) => {
      operation.setContext({
        headers: new HttpHeaders().set(
          'Authorization',
          `Bearer ${keycloak.getToken()}`
        ),
      });
      return forward(operation);
    });
    const link = middleware.concat(http);
    return {
      link: link,
      cache: cache,
      defaultOptions: defaultOptions,
    };
  }

  return {
    link: http,
    cache: cache,
    defaultOptions: defaultOptions,
  };
}

@NgModule({
  imports: [KeycloakAngularModule],
  providers: [
    {
      provide: APOLLO_OPTIONS,
      useFactory: createApollo,
      deps: [HttpLink],
    },
  ],
})
export class GraphQLModule {}
