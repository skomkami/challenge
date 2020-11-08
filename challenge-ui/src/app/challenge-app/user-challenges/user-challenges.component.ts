import { User } from './../../../generated/types.graphql-gen';
import { Component, HostBinding, Input, OnInit } from '@angular/core';

@Component({
  selector: 'user-challenges',
  templateUrl: './user-challenges.component.html'
})
export class UserChallengesComponent implements OnInit {
  @HostBinding('attr.class') cssClass = "sixteen wide mobile ten wide tablet sixteen wide computer column";
  @Input() user: User;

  constructor() { }

  ngOnInit(): void {
  }

}
