import { QueryComponent } from './../../common/QueryComponent';
import { Router } from '@angular/router';
import { Summary } from './../../models/summary.model';
import {
  UserChallengesGQL,
  UserChallengesQuery,
  UserChallengesQueryVariables,
} from './user-challenges.query.graphql-gen';
import { User } from './../../../generated/types.graphql-gen';
import { Component, HostBinding, Input, OnInit } from '@angular/core';

@Component({
  selector: 'user-challenges',
  templateUrl: './user-challenges.component.html',
})
export class UserChallengesComponent extends QueryComponent<
  UserChallengesQuery,
  UserChallengesQueryVariables
> {
  @HostBinding('attr.class') cssClass =
    'sixteen wide mobile ten wide tablet sixteen wide computer column full-height';
  @Input() user: User;

  summaries: Array<Summary>;

  constructor(
    private summariesQuery: UserChallengesGQL,
    private router: Router
  ) {
    super(summariesQuery);
  }

  ngOnInit(): void {
    this.vars = {
      userId: this.user.id,
      size: this.pageSize,
      offset: this.offset,
    };
    super.ngOnInit();
  }

  extractData(data: UserChallengesQuery): void {
    this.summaries = data.user.challenges.results.map(
      (graphQlSummary) => new Summary(graphQlSummary)
    );
    this.total = data.user.challenges.total;
    this.nextPage = data.user.challenges.hasNextPage;
  }
}
