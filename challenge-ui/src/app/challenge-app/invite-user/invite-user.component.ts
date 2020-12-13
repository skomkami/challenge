import { finalize } from 'rxjs/operators';
import { SendInvitationGQL } from './send-invitation.mutation.graphql-gen';
import {
  AllUsersQuery,
  AllUsersQueryVariables,
  AllUsersGQL,
} from './all-users.query.graphql-gen';
import { QueryComponent } from './../../common/QueryComponent';
import { Component, Inject, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { User } from 'src/app/models/user.model';

@Component({
  selector: 'invite-user',
  templateUrl: './invite-user.component.html',
})
export class InviteUserComponent extends QueryComponent<
  AllUsersQuery,
  AllUsersQueryVariables
> {
  challengeId: string;
  currentUserId: string;

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatTable) table: MatTable<User>;
  users: MatTableDataSource<User>;
  displayColumns: string[] = ['User', 'Invite'];

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: InviteComponentInput,
    public dialogRef: MatDialogRef<InviteUserComponent>,
    private sendInvitation: SendInvitationGQL,
    allUsersQuery: AllUsersGQL
  ) {
    super(allUsersQuery);
    this.challengeId = data && data.challengeId;
    this.currentUserId = data && data.currentUserId;
    this.vars = {
      challengeId: this.challengeId,
      size: this.pageSize,
      offset: this.offset,
    };
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  close() {
    this.dialogRef.close();
  }

  updateVarsOffset(newOffset: number): void {
    this.vars.offset = newOffset;
  }

  extractData(data: AllUsersQuery): void {
    this.total = data.allUsers.total;
    const users = data.allUsers.results.map((result) => {
      return new User(result);
    });
    console.log(users);
    this.users = new MatTableDataSource(users);
  }

  invite(userId: string, btnRef: HTMLButtonElement): void {
    console.log('inviting user: ', userId);
    console.log('btn', btnRef);
    btnRef.classList.add('loading');

    this.sendInvitation
      .mutate({
        forUser: userId,
        fromUser: this.currentUserId,
        challengeId: this.challengeId,
      })
      .pipe(
        finalize(() => {
          btnRef.classList.remove('loading');
          btnRef.disabled = true;
        })
      )
      .subscribe(
        ({ data }) => {
          console.log('Invited user:', data);
          this.errorMessage = null;
        },
        (error) => {
          console.log(error);
          this.errorMessage = error;
        }
      );
  }
}

export class InviteComponentInput {
  currentUserId: string;
  challengeId: string;

  constructor(userId: string, challengeId: string) {
    this.challengeId = challengeId;
    this.currentUserId = userId;
  }
}
