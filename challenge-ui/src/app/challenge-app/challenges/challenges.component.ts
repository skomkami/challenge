import {
  AllChallengesGQL,
  AllChallengesQuery,
  AllChallengesQueryVariables,
} from './challenges.query.graphql-gen';
import { Challenge } from './../../models/challenge.model';
import { QueryComponent } from '../../common/QueryComponent';
import { User } from './../../../generated/types.graphql-gen';
import { Component, HostBinding, Input } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'challenges',
  templateUrl: './challenges.component.html',
})
export class ChallengesComponent extends QueryComponent<
  AllChallengesQuery,
  AllChallengesQueryVariables
> {
  @HostBinding('attr.class') cssClass =
    'sixteen wide mobile ten wide computer column';
  @Input() user: User;

  challenges: Array<Challenge>;

  constructor(
    private challengesQuery: AllChallengesGQL,
    private router: Router
  ) {
    super(challengesQuery);
    this.vars = {
      size: this.pageSize,
      offset: this.offset,
    };
  }

  extractData(data: AllChallengesQuery): void {
    this.challenges = data.allChallenges.map(
      (graphQlChallenge) => new Challenge(graphQlChallenge)
    );
  }

  goToChallenge(challengeId: string): void {
    console.log('Going to challenge: ', challengeId);
    this.router.navigateByUrl('challenge/' + challengeId);
  }
}
