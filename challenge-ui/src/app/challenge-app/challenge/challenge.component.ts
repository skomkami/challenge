import {
  InviteUserComponent,
  InviteComponentInput,
} from './../invite-user/invite-user.component';
import { QueryComponent } from './../../common/QueryComponent';
import {
  ChallengeGQL,
  ChallengeQuery,
  ChallengeQueryVariables,
} from './challenge.query.graphql-gen';
import { Component, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Challenge } from 'src/app/models/challenge.model';
import { Summary } from 'src/app/models/summary.model';
import {
  MeasureValue,
  measureValueOrdering,
} from 'src/app/models/measure-value.model';
import { User } from 'src/app/models/user.model';
import { UserService } from 'src/app/services/user-service.service';
import { MatPaginator } from '@angular/material/paginator';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';

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
  @ViewChild(MatTable) table: MatTable<Summary>;
  summaries: MatTableDataSource<Summary>;
  displayColumns: string[] = ['User', 'Position', 'Points', 'Last active'];

  constructor(
    private route: ActivatedRoute,
    private userService: UserService,
    private challengeQuery: ChallengeGQL,
    private router: Router,
    private matDialog: MatDialog
  ) {
    super(challengeQuery);
    this.loading = true;

    this.vars = {
      challengeId: this.challengeId,
      size: this.pageSize,
      offset: this.offset,
    };

    route.params.subscribe((params) => {
      this.challengeId = params['challengeId'];
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
    this.total = this.total + 1;
    newSummary.position = this.total;
    console.log(newSummary);
    const newData = this.summaries.data;
    newData.push(newSummary);
    this.summaries.data = newData;
    this.participatesInChallenge = true;
  }

  updateRanking(activityValue: MeasureValue): void {
    const summaryToUpdate = this.summaries.data.find(
      (summary) => summary.user.id === this.user.id
    );

    const summariesWithoutCurrent = this.summaries.data.filter((summary) => {
      const res = summary.user.id !== this.user.id;
      return res;
    });
    this.summaries.data = summariesWithoutCurrent;
    const updatedValue = summaryToUpdate.summaryValue.updated(
      activityValue,
      this.challenge.measure
    );
    summaryToUpdate.summaryValue = updatedValue;
    summaryToUpdate.lastActive = new Date().toISOString();
    const updatedSummaries = this.summaries.data;
    updatedSummaries.push(summaryToUpdate);
    updatedSummaries.sort((a, b) => {
      return measureValueOrdering(
        a.summaryValue,
        b.summaryValue,
        this.challenge.measure
      );
    });
    updatedSummaries.map((summary, index) => {
      const newSummary = summary;
      newSummary.position = index + 1;
      return newSummary;
    });
    console.log('updated summaries: ', updatedSummaries);
    this.summaries.data = updatedSummaries;
  }

  updateVarsOffset(newOffset: number): void {
    this.vars.offset = newOffset;
  }

  extractData(data: ChallengeQuery): void {
    this.challenge = new Challenge(data.challenge);
    console.log('challege: ', data);
    this.total = data.challenge.summaries.total;
    const summaries = data.challenge.summaries.results.map(
      (summary) => new Summary(summary)
    );
    this.summaries = new MatTableDataSource(summaries);
  }

  finished(): boolean {
    const currentDate = new Date();
    const challengeFinishDate = new Date(this.challenge.finishesOn);
    return currentDate >= challengeFinishDate;
  }

  goToActivities(userId: string): void {
    this.router.navigateByUrl(
      'home/challenge/' + this.challengeId + '/user-activities/' + userId
    );
  }

  openInviteDialog() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.data = new InviteComponentInput(
      this.user.id,
      this.challengeId
    );
    this.matDialog.open(InviteUserComponent, dialogConfig);
  }
}
