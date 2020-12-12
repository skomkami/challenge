import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { QueryComponent } from './../../common/QueryComponent';
import { ActivatedRoute, Router } from '@angular/router';
import {
  ActivitiesGQL,
  ActivitiesQuery,
  ActivitiesQueryVariables,
} from './activities.query.graphql-gen';
import { Component, ViewChild } from '@angular/core';
import { Activity } from '../../models/activity.model';
import { User } from '../../models/user.model';
import { Challenge } from '../../models/challenge.model';

@Component({
  selector: 'activities',
  templateUrl: './activities.component.html',
})
export class ActivitiesComponent extends QueryComponent<
  ActivitiesQuery,
  ActivitiesQueryVariables
> {
  userId: string;
  challengeId: string;
  user?: User;
  challenge: Challenge;

  @ViewChild(MatPaginator) paginator: MatPaginator;
  activities: MatTableDataSource<Activity>;
  displayColumns: string[] = ['Points', 'Date'];

  constructor(
    private route: ActivatedRoute,
    private activitiesQuery: ActivitiesGQL
  ) {
    super(activitiesQuery);
    this.loading = true;

    this.vars = {
      challengeId: this.challengeId,
      userId: this.userId,
      size: this.pageSize,
      offset: this.offset,
    };

    route.params.subscribe((params) => {
      this.challengeId = params['challengeId'];
      this.vars.challengeId = this.challengeId;
      this.userId = params['userId'];
      this.vars.userId = this.userId;
    });
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  updateVarsOffset(newOffset: number): void {
    this.vars.offset = newOffset;
  }

  extractData(data: ActivitiesQuery): void {
    console.log(data);
    this.challenge = new Challenge(data.challenge);
    this.user = new User(data.user);
    this.total = data.allUserChallengeActivities.total;
    const activities = data.allUserChallengeActivities.results.map(
      (activity) => {
        console.log(activity);
        return new Activity(activity);
      }
    );
    console.log(activities);
    this.activities = new MatTableDataSource(activities);
  }
}
