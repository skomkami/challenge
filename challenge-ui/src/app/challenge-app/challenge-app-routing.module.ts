import { ChallengeComponent } from './challenge/challenge.component';
import { HomeComponent } from './home/home.component';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CreateChallengeComponent } from './create-challenge/create-challenge.component';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    pathMatch: 'full',
    data: {
      breadcrumb: [
        {
          label: 'Welcome page',
          url: '../',
        },
        {
          label: 'Home',
          url: '',
        },
      ],
    },
  },
  {
    path: 'challenge/:id',
    component: ChallengeComponent,
    data: {
      breadcrumb: [
        {
          label: 'Welcome page',
          url: '../',
        },
        {
          label: 'Home', // pageOneID Parameter value will be add
          url: '/home',
        },
        {
          label: 'Challenge', // pageTwoID Parameter value will be add
          url: '/challenge/:id',
        },
      ],
    },
  },
  {
    path: 'create-challenge',
    component: CreateChallengeComponent,
    data: {
      breadcrumb: [
        {
          label: 'Welcome page',
          url: '../',
        },
        {
          label: 'Home', // pageOneID Parameter value will be add
          url: '/home',
        },
        {
          label: 'Create challenge', // pageTwoID Parameter value will be add
          url: '/create-challenge',
        },
      ],
    },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ChallengeAppRoutingModule {}
