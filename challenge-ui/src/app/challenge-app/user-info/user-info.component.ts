import { User } from './../../../generated/types.graphql-gen';
import { Component, HostBinding, Input, OnInit } from '@angular/core';

@Component({
  selector: 'user-info',
  templateUrl: './user-info.component.html'
})
export class UserInfoComponent implements OnInit {
  @HostBinding('attr.class') cssClass = "sixteen wide mobile six wide tablet sixteen wide computer column";
  @Input() user: User;

  constructor() { }

  ngOnInit(): void {
  }

}
