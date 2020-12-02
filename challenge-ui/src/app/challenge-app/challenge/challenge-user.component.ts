import { LogActivityGQL } from './log-activity.mutation.graphql-gen';
import { finalize } from 'rxjs/operators';
import { JoinChallengeGQL } from './join-challenge.mutation.graphql-gen';
import {
  UserParticipatesInChallengeGQL,
  UserParticipatesInChallengeQuery,
  UserParticipatesInChallengeQueryVariables,
} from './user-participates-in-challenge.query.graphql-gen';
import { QueryComponent } from './../../common/QueryComponent';
import { Component, Input } from '@angular/core';
import { User } from 'src/app/models/user.model';
import { Output } from '@angular/core';
import { EventEmitter } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup } from '@angular/forms';
import { positiveNumberValidator } from './../../common/utils';

@Component({
  selector: 'challenge-user',
  templateUrl: './challenge-user.component.html',
})
export class ChallengeUserComponent extends QueryComponent<
  UserParticipatesInChallengeQuery,
  UserParticipatesInChallengeQueryVariables
> {
  @Input() user: User;
  @Input() challengeId: string;
  @Output()
  joinedChallenge: EventEmitter<boolean> = new EventEmitter<boolean>();

  @Output()
  loggedActivity: EventEmitter<number> = new EventEmitter<number>();

  logActivityForm: FormGroup;
  value: AbstractControl;

  participatesInChallenge: boolean;

  errorMessage: string;
  joiningChallenge: boolean;
  loggingActivity: boolean;

  activityValue: number;

  constructor(
    private participatesQuery: UserParticipatesInChallengeGQL,
    private joinChallenge: JoinChallengeGQL,
    private logActivityMutation: LogActivityGQL,
    fb: FormBuilder
  ) {
    super(participatesQuery);
    this.logActivityForm = fb.group({
      value: [this.activityValue, positiveNumberValidator()],
    });
    this.value = this.logActivityForm.controls['value'];
    this.value.valueChanges.subscribe((value) => {
      this.activityValue = value;
    });
  }

  ngOnInit(): void {
    this.vars = {
      userId: this.user.id,
      challengeId: this.challengeId,
    };
    super.ngOnInit();
  }

  extractData(data: UserParticipatesInChallengeQuery): void {
    this.participatesInChallenge = data.user.participatesInChallenge;
  }

  join(): void {
    this.joiningChallenge = true;
    this.joinChallenge
      .mutate({ userId: this.user.id, challengeId: this.challengeId })
      .pipe(
        finalize(() => {
          this.joiningChallenge = false;
          console.log('emmiting joinedChallenge');
          this.joinedChallenge.emit(true);
        })
      )
      .subscribe(
        ({ data }) => {
          console.log('Created summary:', data);
          this.errorMessage = null;
        },
        (error) => {
          console.log(error);
          this.errorMessage = error;
        }
      );
  }

  logActivity(): void {
    this.loggingActivity = true;
    this.logActivityMutation
      .mutate({
        activity: {
          userId: this.user.id,
          challengeId: this.challengeId,
          value: this.activityValue,
        },
      })
      .pipe(
        finalize(() => {
          this.loggingActivity = false;
        })
      )
      .subscribe(
        ({ data }) => {
          console.log('Logged activity:', data);
          this.errorMessage = null;
          this.loggedActivity.emit(this.activityValue);
          this.activityValue = null;
          this.logActivityForm.reset();
        },
        (error) => {
          console.log(error);
          this.errorMessage = error;
        }
      );
  }
}
