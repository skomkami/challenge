import { UserServiceService } from './../../services/user-service.service';
import { Component, HostBinding, OnInit } from '@angular/core';
import { User } from 'src/app/models/user.model';

@Component({
  selector: 'home',
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {
  @HostBinding('attr.class') cssClass = "full-height";

  user?: User;

  constructor(private userService: UserServiceService){
  }

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe( user => {
      this.user = user;
    })
  }

}
