import {
  UserParticipatesInChallengeGQL,
  UserParticipatesInChallengeQuery,
  UserParticipatesInChallengeQueryVariables,
} from './user-participates-in-challenge.query.graphql-gen';
import { QueryComponent } from './../../common/QueryComponent';
import { Component, Input } from '@angular/core';
import { User } from 'src/app/models/user.model';

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
  
  userParticipates: boolean;

  participatesInChallenge: boolean;

  constructor(private participatesQuery: UserParticipatesInChallengeGQL) {
    this.vars = {
      userId: this.user.id,
      challengeId: this.challengeId,
    };
    
    super(participatesQuery);
  }

  extractData(data: UserParticipatesInChallengeQuery): void {
    this.participatesInChallenge = data.
  }
}
