import { UserComponent } from './user/user.component';
import { HomeComponent } from './home/home.component';
import { ChallengeAppComponent } from './challenge-app.component';
import { ChallengeAppRoutingModule } from './challenge-app-routing.module';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserInfoComponent } from './user-info/user-info.component';
import { UserChallengesComponent } from './user-challenges/user-challenges.component';
import { ChallengesComponent } from './challenges/challenges.component';


@NgModule({
  declarations: [
    ChallengeAppComponent,
    HomeComponent,
    UserComponent,
    UserInfoComponent,
    UserChallengesComponent,
    ChallengesComponent
  ],
  imports: [
    CommonModule,
    ChallengeAppRoutingModule
  ]
})
export class ChallengeAppModule { }
