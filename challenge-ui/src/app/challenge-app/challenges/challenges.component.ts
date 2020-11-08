import { User } from './../../../generated/types.graphql-gen';
import { Component, HostBinding, Input, OnInit } from '@angular/core';

@Component({
  selector: 'challenges',
  templateUrl: './challenges.component.html'
})
export class ChallengesComponent implements OnInit {
  @HostBinding('attr.class') cssClass="sixteen wide mobile ten wide computer column";
  @Input() user: User;
  constructor() { }

  ngOnInit(): void {
  }

}
