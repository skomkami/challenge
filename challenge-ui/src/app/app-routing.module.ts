import { AuthGuard } from './app.authguard';
import { RegisterComponent } from './register/register.component';
import { WelcomePageComponent } from './welcome-page/welcome-page.component';
import { NgModule, Component } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

const routes: Routes = [
  { path: '', component: WelcomePageComponent, pathMatch: 'full' },
  {
    path: 'register',
    component: RegisterComponent,
    data: {
      breadcrumb: [
        {
          label: 'Welcome page',
          url: '/',
        },
        {
          label: 'Register',
          url: '/register',
        },
      ],
    },
  },
  {
    path: 'home',
    loadChildren: './challenge-app/challenge-app.module#ChallengeAppModule',
    canActivate: [AuthGuard],
    data: {
      breadcrumb: [
        {
          label: 'Welcome page',
          url: '/',
        },
        {
          label: 'Home',
          url: '/home',
        },
      ],
    },
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
