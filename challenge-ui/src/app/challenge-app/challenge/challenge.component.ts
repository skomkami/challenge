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
import { UserService } from 'src/app/services/user-service.service';
import { ChangeDetectorRef } from '@angular/core';

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
    private userService: UserService,
    private challengeQuery: ChallengeGQL
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
  }

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe((user) => {
      this.user = user;
      super.ngOnInit();
    });
  }

  pushNewSummary(newSummary: Summary): void {
    newSummary.position = this.summaries.length + 1;
    this.summaries.push(newSummary);
    this.participatesInChallenge = true;
  }

  updateRanking(activityValue): void {
    console.log('logged value: ', activityValue);
    this.summaries = this.summaries
      .map((summary) => {
        console.log(summary.user.id);
        console.log(this.user.id);
        if (summary.user.id === this.user.id) {
          summary.summaryValue += activityValue;
          console.log('asdfj');
          return summary;
        } else {
          return summary;
        }
      })
      .sort((a, b) => {
        return a.position - b.position;
      });
  }

  extractData(data: ChallengeQuery): void {
    this.challenge = new Challenge(data.challenge);
    console.log('challege: ', data);
    this.summaries = data.challenge.summaries.results
      .map((summary) => new Summary(summary))
      .sort((a, b) => {
        return a.position - b.position;
      });
  }
}
