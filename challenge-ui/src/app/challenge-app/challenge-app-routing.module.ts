import { ChallengeComponent } from './challenge/challenge.component';
import { HomeComponent } from './home/home.component';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CreateChallengeComponent } from './create-challenge/create-challenge.component';

const routes: Routes = [
  { path: '', component: HomeComponent, pathMatch: 'full' },
  { path: 'create-challenge', component: CreateChallengeComponent },
  { path: 'challenge/:id', component: ChallengeComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ChallengeAppRoutingModule {}
