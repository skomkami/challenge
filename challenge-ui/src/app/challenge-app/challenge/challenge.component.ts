import { UserParticipatesInChallengeGQL } from './user-participates-in-challenge.query.graphql-gen';
import { QueryComponent } from './../../common/QueryComponent';
import {
  ChallengeGQL,
  ChallengeQuery,
  ChallengeQueryVariables,
} from './challenge.query.graphql-gen';
import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Challenge } from 'src/app/models/challenge.model';
import { Summary } from 'src/app/models/summary.model';
import { User } from 'src/app/models/user.model';
import { UserServiceService } from 'src/app/services/user-service.service';

@Component({
  selector: 'app-challenge',
  templateUrl: './challenge.component.html',
})
export class ChallengeComponent extends QueryComponent<
  ChallengeQuery,
  ChallengeQueryVariables
> {
  user?: User;
  challengeId: string;
  challenge: Challenge;
  summaries: Array<Summary>;

  participatesInChallenge: boolean;

  constructor(
    private route: ActivatedRoute,
    private userService: UserServiceService,
    private challengeQuery: ChallengeGQL,
    private participatesQuery: UserParticipatesInChallengeGQL
  ) {
    super(challengeQuery);
    this.loading = true;

    this.vars = {
      challengeId: this.challengeId,
      size: this.pageSize,
      offset: this.offset,
    };

    route.params.subscribe((params) => {
      this.challengeId = params['id'];
      this.vars.challengeId = this.challengeId;
    });

    this.userService.getCurrentUser().subscribe((user) => {
      this.user = user;
    });
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.participatesQuery
      .watch({ userId: this.user.id, challengeId: this.challengeId })
      .valueChanges.subscribe(({ data, loading }) => {
        this.loading = loading;
        this.participatesInChallenge = data.user.participatesInChallenge;
      });
  }

  extractData(data: ChallengeQuery): void {
    this.challenge = new Challenge(data.challenge);
    this.summaries = data.challenge.summaries
      .map((summary) => new Summary(summary))
      .sort((a, b) => {
        return a.position - b.position;
      });
  }
}
