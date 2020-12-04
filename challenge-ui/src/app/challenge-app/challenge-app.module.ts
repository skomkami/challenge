import { SearchBoxComponent } from './challenges/search-box.componenet';
import { UserComponent } from './user/user.component';
import { HomeComponent } from './home/home.component';
import { ChallengeAppComponent } from './challenge-app.component';
import { ChallengeAppRoutingModule } from './challenge-app-routing.module';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserInfoComponent } from './user/user-info.component';
import { UserChallengesComponent } from './user-challenges/user-challenges.component';
import { ChallengesComponent } from './challenges/challenges.component';
import { CreateChallengeComponent } from './create-challenge/create-challenge.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';

import {
  NgxMatDatetimePickerModule,
  NgxMatNativeDateModule,
  NgxMatTimepickerModule,
} from '@angular-material-components/datetime-picker';
import { ChallengeComponent } from './challenge/challenge.component';
import { ChallengeUserComponent } from './challenge/challenge-user.component';

@NgModule({
  declarations: [
    ChallengeAppComponent,
    HomeComponent,
    UserComponent,
    UserInfoComponent,
    UserChallengesComponent,
    ChallengesComponent,
    CreateChallengeComponent,
    ChallengeComponent,
    ChallengeUserComponent,
    SearchBoxComponent,
  ],
  imports: [
    CommonModule,
    ChallengeAppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    MatDatepickerModule,
    MatInputModule,

    NgxMatDatetimePickerModule,
    NgxMatTimepickerModule,
    NgxMatNativeDateModule,
  ],
})
export class ChallengeAppModule {}
