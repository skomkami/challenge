import { UserByEmailGQL } from './user-by-email.query.graphql-gen';
import { User } from '../models/user.model';
import { KeycloakService } from 'keycloak-angular';
import { Injectable } from '@angular/core';
import { Observable } from '@apollo/client/core';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private currentUser?: User;
  loading: boolean;

  constructor(
    private keycloak: KeycloakService,
    private query: UserByEmailGQL
  ) {}

  getCurrentUser() {
    return this.currentUser
      ? Observable.of(this.currentUser)
      : this.query
          .watch({ email: this.keycloak.getUsername() })
          .valueChanges.pipe(
            map((result) => {
              console.log(result);
              const user = new User(result.data.getUserByEmail);
              return user;
            })
          );
  }
}
