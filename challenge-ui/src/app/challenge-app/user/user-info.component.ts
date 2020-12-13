import { ViewInvitationsComponent } from './../view-invitations/view-invitations.component';
import { User } from 'src/app/models/user.model';
import { Component, HostBinding, Input, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';

@Component({
  selector: 'user-info',
  templateUrl: './user-info.component.html',
})
export class UserInfoComponent implements OnInit {
  @HostBinding('attr.class') cssClass =
    'sixteen wide mobile six wide tablet sixteen wide computer column';
  @Input() user: User;

  constructor(private matDialog: MatDialog) {}

  ngOnInit(): void {}

  openInvitationsDialog(userId: string) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.data = userId;
    this.matDialog.open(ViewInvitationsComponent, dialogConfig);
  }
}
