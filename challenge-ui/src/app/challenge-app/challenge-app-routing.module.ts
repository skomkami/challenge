import { ChallengeComponent } from './challenge/challenge.component';
import { HomeComponent } from './home/home.component';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CreateChallengeComponent } from './create-challenge/create-challenge.component';
import { ActivitiesComponent } from './activities/activities.component';

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
    path: 'challenge/:challengeId',
    component: ChallengeComponent,
    data: {
      breadcrumb: [
        {
          label: 'Welcome page',
          url: '../',
        },
        {
          label: 'Home',
          url: '/home',
        },
        {
          label: 'Challenge',
          url: '/challenge/:challengeId',
        },
      ],
    },
  },
  {
    path: 'challenge/:challengeId/user-activities/:userId',
    component: ActivitiesComponent,
    data: {
      breadcrumb: [
        {
          label: 'Welcome page',
          url: '../',
        },
        {
          label: 'Home',
          url: '/home',
        },
        {
          label: 'Challenge',
          url: '/challenge/:challengeId',
        },
        {
          label: 'User activities',
          url: '/challenge/:challengeId/user-activities/:userId',
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
