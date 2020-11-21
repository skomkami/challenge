import { Router } from '@angular/router';
import { Summary } from './../../models/summary.model';
import { UserChallengesGQL } from './user-challenges.query.graphql-gen';
import { User } from './../../../generated/types.graphql-gen';
import { Component, HostBinding, Input, OnInit } from '@angular/core';

@Component({
  selector: 'user-challenges',
  templateUrl: './user-challenges.component.html'
})
export class UserChallengesComponent implements OnInit {
  @HostBinding('attr.class') cssClass = "sixteen wide mobile ten wide tablet sixteen wide computer column";
  @Input() user: User;

  loading: boolean;
  summaries: Array<Summary>;

  constructor(private summariesQuery: UserChallengesGQL, private router: Router) {    
  }

  ngOnInit(): void {
    this.summariesQuery
    .watch({userId: this.user.id})
    .valueChanges
    .subscribe(({data, loading}) => {
      this.loading = loading;
      this.summaries = data.user.challenges.map( graphQlSummary => 
        new Summary(graphQlSummary)
      )
    });
  }

  redirectToCreateChallenge(): void {
    this.router.navigateByUrl("home/create-challenge");
  }

}
