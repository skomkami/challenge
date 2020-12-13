import { MarkInvitationAsReadGQL } from './mark-invitation-as-read.mutation.graphql-gen';
import { Invitation } from 'src/app/models/invitation.model';
import {
  UnreadUserInvitationsQuery,
  UnreadUserInvitationsQueryVariables,
  UnreadUserInvitationsGQL,
} from './unread-invitations.query.graphql-gen';
import { QueryComponent } from './../../common/QueryComponent';
import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';

@Component({
  selector: 'app-view-invitations',
  templateUrl: './view-invitations.component.html',
})
export class ViewInvitationsComponent extends QueryComponent<
  UnreadUserInvitationsQuery,
  UnreadUserInvitationsQueryVariables
> {
  userId: string;

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatTable) table: MatTable<Invitation>;
  invitations: MatTableDataSource<Invitation>;
  displayColumns: string[] = ['Invitation', 'Date'];

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: string,
    public dialogRef: MatDialogRef<ViewInvitationsComponent>,
    unreadInvitationsQuery: UnreadUserInvitationsGQL,
    private markInvitationoAsRead: MarkInvitationAsReadGQL,
    private router: Router
  ) {
    super(unreadInvitationsQuery);
    this.userId = data;
  }

  ngOnInit(): void {
    this.vars = {
      userId: this.userId,
      size: this.pageSize,
      offset: this.offset,
    };
    super.ngOnInit();
  }

  close() {
    this.dialogRef.close();
  }

  updateVarsOffset(newOffset: number): void {
    this.vars.offset = newOffset;
  }

  extractData(data: UnreadUserInvitationsQuery): void {
    this.total = data.allInvitations.total;
    const invitations = data.allInvitations.results.map((result) => {
      return new Invitation(result);
    });
    this.invitations = new MatTableDataSource(invitations);
  }

  markAsReadAndNavigate(invitationId: string, challengeId: string): void {
    console.log('marking as read');
    this.markInvitationoAsRead.mutate({ invitationId: invitationId }).subscribe(
      ({ data }) => {
        console.log('Marked as read:', data);
        this.errorMessage = null;
      },
      (error) => {
        console.log(error);
      }
    );
    this.close();
    this.router.navigateByUrl('home/challenge/' + challengeId);
  }
}
