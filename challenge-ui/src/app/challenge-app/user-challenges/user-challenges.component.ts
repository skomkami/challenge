import { QueryComponent } from './../../common/QueryComponent';
import { Router } from '@angular/router';
import { Summary } from './../../models/summary.model';
import {
  UserChallengesGQL,
  UserChallengesQuery,
  UserChallengesQueryVariables,
} from './user-challenges.query.graphql-gen';
import { User } from './../../../generated/types.graphql-gen';
import {
  Component,
  HostBinding,
  Input,
  OnInit,
  ViewChild,
} from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'user-challenges',
  templateUrl: './user-challenges.component.html',
})
export class UserChallengesComponent extends QueryComponent<
  UserChallengesQuery,
  UserChallengesQueryVariables
> {
  @HostBinding('attr.class') cssClass =
    'sixteen wide mobile ten wide tablet sixteen wide computer column';
  @Input() user: User;

  @ViewChild(MatPaginator) paginator: MatPaginator;
  summaries: MatTableDataSource<Summary>;
  displayColumns: string[] = ['Name', 'Position', 'Points'];

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

  updateVarsOffset(newOffset: number): void {
    this.vars.offset = newOffset;
  }

  extractData(data: UserChallengesQuery): void {
    const summaries = data.user.challenges.results.map(
      (graphQlSummary) => new Summary(graphQlSummary)
    );
    this.summaries = new MatTableDataSource(summaries);
    this.total = data.user.challenges.total;
  }

  goToChallenge(challengeId: string): void {
    console.log('Going to challenge: ', challengeId);
    this.router.navigateByUrl('home/challenge/' + challengeId);
  }
}
