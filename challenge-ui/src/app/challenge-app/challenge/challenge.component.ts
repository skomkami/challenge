import { QueryComponent } from './../../common/QueryComponent';
import {
  ChallengeGQL,
  ChallengeQuery,
  ChallengeQueryVariables,
} from './challenge.query.graphql-gen';
import { Component, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Challenge } from 'src/app/models/challenge.model';
import { Summary } from 'src/app/models/summary.model';
import { User } from 'src/app/models/user.model';
import { UserService } from 'src/app/services/user-service.service';
import { ChangeDetectorRef } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';

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

  participatesInChallenge: boolean;

  @ViewChild(MatPaginator) paginator: MatPaginator;
  summaries: MatTableDataSource<Summary>;
  displayColumns: string[] = ['User', 'Position', 'Points', 'Last active'];

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
    newSummary.position = this.total + 1;
    this.summaries.data.push(newSummary);
    this.participatesInChallenge = true;
  }

  updateRanking(activityValue): void {
    console.log('logged value: ', activityValue);
    const updatedSummaries = this.summaries.data
      .map((summary) => {
        console.log(summary.user.id);
        console.log(this.user.id);
        if (summary.user.id === this.user.id) {
          summary.summaryValue += activityValue;
          return summary;
        } else {
          return summary;
        }
      })
      .sort((a, b) => {
        return a.position - b.position;
      });
    this.summaries = new MatTableDataSource(updatedSummaries);
  }

  updateVarsOffset(newOffset: number): void {
    this.vars.offset = newOffset;
  }

  extractData(data: ChallengeQuery): void {
    this.challenge = new Challenge(data.challenge);
    console.log('challege: ', data);
    this.total = data.challenge.summaries.total;
    const summaries = data.challenge.summaries.results
      .map((summary) => new Summary(summary))
      .sort((a, b) => {
        return a.position - b.position;
      });
    this.summaries = new MatTableDataSource(summaries);
  }
}
