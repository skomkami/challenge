import {
  AllChallengesGQL,
  AllChallengesQuery,
  AllChallengesQueryVariables,
} from './challenges.query.graphql-gen';
import { Challenge } from './../../models/challenge.model';
import { QueryComponent } from '../../common/QueryComponent';
import { User } from './../../../generated/types.graphql-gen';
import { Component, HostBinding, Input, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';

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
  @ViewChild(MatPaginator) paginator: MatPaginator;

  challenges: MatTableDataSource<Challenge>;
  displayColumns: string[] = ['Name', 'Finishes on', 'Created by', 'Leader'];

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
    this.total = data.allChallenges.total;
    const challenges = data.allChallenges.results.map(
      (graphQlChallenge) => new Challenge(graphQlChallenge)
    );
    this.loadChallenges(challenges);
  }

  loadChallenges(challenges: Challenge[]): void {
    this.challenges = new MatTableDataSource(challenges);
  }

  goToChallenge(challengeId: string): void {
    console.log('Going to challenge: ', challengeId);
    this.router.navigateByUrl('home/challenge/' + challengeId);
  }

  reset(): void {
    this.ngOnInit();
  }

  redirectToCreateChallenge(): void {
    this.router.navigateByUrl('home/create-challenge');
  }
}
