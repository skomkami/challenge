import { Component, HostBinding, Input, OnInit } from '@angular/core';
import { User } from 'src/app/models/user.model';

@Component({
  selector: 'user-component',
  templateUrl: './user.component.html',
})
export class UserComponent implements OnInit {
  @HostBinding('attr.class') cssClass =
    'sixteen wide mobile six wide computer column';
  @Input() user: User;

  constructor() {}

  ngOnInit(): void {}
}
