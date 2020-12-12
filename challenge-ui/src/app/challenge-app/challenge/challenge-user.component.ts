import { Challenge } from 'src/app/models/challenge.model';
import { MeasureValue } from 'src/app/models/measure-value.model';
import { Summary } from 'src/app/models/summary.model';
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
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
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
  @Input() challenge: Challenge;
  @Output()
  joinedChallenge: EventEmitter<Summary> = new EventEmitter<Summary>();

  @Output()
  loggedActivity: EventEmitter<MeasureValue> = new EventEmitter<MeasureValue>();

  logActivityForm: FormGroup;
  integerValue: AbstractControl;
  decimalValue: AbstractControl;

  participatesInChallenge: boolean;

  errorMessage: string;
  joiningChallenge: boolean;
  loggingActivity: boolean;

  activityValue: MeasureValue;

  constructor(
    private participatesQuery: UserParticipatesInChallengeGQL,
    private joinChallenge: JoinChallengeGQL,
    private logActivityMutation: LogActivityGQL,
    private fb: FormBuilder
  ) {
    super(participatesQuery);
    this.resetActivity();
  }

  ngOnInit(): void {
    this.logActivityForm = this.fb.group({
      integerValue: [
        this.activityValue.integerValue,
        Validators.compose([
          positiveNumberValidator(!this.challenge.measure.allowDecimal),
          Validators.pattern('^[0-9]*$'),
        ]),
      ],
      decimalValue: [
        this.activityValue.decimalValue,
        positiveNumberValidator(this.challenge.measure.allowDecimal),
      ],
    });
    this.integerValue = this.logActivityForm.controls['integerValue'];
    this.decimalValue = this.logActivityForm.controls['decimalValue'];
    this.integerValue.valueChanges.subscribe((integerValue) => {
      this.activityValue.integerValue = integerValue;
    });
    this.decimalValue.valueChanges.subscribe((decimalValue) => {
      this.activityValue.decimalValue = decimalValue;
    });
    this.vars = {
      userId: this.user.id,
      challengeId: this.challenge.id,
    };
    super.ngOnInit();
  }

  extractData(data: UserParticipatesInChallengeQuery): void {
    this.participatesInChallenge = data.user.participatesInChallenge;
  }

  join(): void {
    this.joiningChallenge = true;
    this.joinChallenge
      .mutate({ userId: this.user.id, challengeId: this.challenge.id })
      .pipe(
        finalize(() => {
          this.joiningChallenge = false;
          this.participatesInChallenge = true;
        })
      )
      .subscribe(
        ({ data }) => {
          console.log('Created summary:', data);
          this.joinedChallenge.emit(new Summary(data.joinChallenge));
          this.errorMessage = null;
        },
        (error) => {
          console.log(error);
          this.errorMessage = error;
        }
      );
  }

  resetActivity(): void {
    this.activityValue = new MeasureValue();
  }

  logActivity(): void {
    this.loggingActivity = true;
    this.logActivityMutation
      .mutate({
        activity: {
          userId: this.user.id,
          challengeId: this.challenge.id,
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
          this.logActivityForm.reset();
          this.resetActivity();
        },
        (error) => {
          console.log(error);
          this.errorMessage = error;
        }
      );
  }
}
