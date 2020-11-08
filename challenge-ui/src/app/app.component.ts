import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'challenge';

  isLoggedIn: boolean = false;

  constructor(private keycloakService: KeycloakService, private router: Router) { }

  ngOnInit(): void {
    this.keycloakService.isLoggedIn().then(isLogged => this.isLoggedIn = isLogged);
  }

  async logout() {
    console.log("logging out");
    await this.keycloakService.logout("http://localhost:4200")
    .then( _ =>  {
      this.isLoggedIn = false;
    });
  }

  async login() {
    console.log("logging in");
    this.router.navigateByUrl("/home");
  }

}
