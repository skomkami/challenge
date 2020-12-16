import { Challenge } from './../../models/challenge.model';
import {
  AllUsersQuery,
  AllUsersQueryVariables,
  AllUsersGQL,
} from './all-users.query.graphql-gen';
import { tap, map, filter, debounceTime, switchAll } from 'rxjs/operators';
import { fromEvent } from 'rxjs';
import {
  Component,
  Output,
  OnInit,
  EventEmitter,
  ElementRef,
  Input,
  HostBinding,
} from '@angular/core';
import { User } from '../../models/user.model';
@Component({
  selector: 'invite-search-box',
  template: `
    <input
      type="text"
      class="form-control full-width"
      placeholder="User name ..."
      autofocus
    />
  `,
})
export class InviteSearchBoxComponent implements OnInit {
  @HostBinding('attr.class') cssClass = 'full-width';

  @Input() offset: number;
  @Input() pageSize: number;
  @Input() challengeId: string;
  @Output() loading: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() total: EventEmitter<number> = new EventEmitter<number>();
  @Output() results: EventEmitter<User[]> = new EventEmitter<User[]>();
  @Output() removedSearch: EventEmitter<boolean> = new EventEmitter<boolean>();

  vars: AllUsersQueryVariables;

  constructor(private usersQuery: AllUsersGQL, private el: ElementRef) {}

  extractData(data: AllUsersQuery): Array<User> {
    return data.allUsers.results.map((graphQlUser) => new User(graphQlUser));
  }

  ngOnInit(): void {
    this.vars = {
      challengeId: this.challengeId,
      size: this.pageSize,
      offset: this.offset,
    };
    fromEvent(this.el.nativeElement, 'keyup')
      .pipe(
        map((e: any) => e.target.value),
        filter((text: string) => {
          if (text.length < 1) {
            console.log('emptied');
            this.removedSearch.emit(true);
          }
          return text.length > 1;
        }),
        debounceTime(250),
        tap(() => this.loading.emit(true)),
        map((query: string) => {
          this.vars.nameFilter = '.*' + query + '.*';
          return this.usersQuery.watch(this.vars).valueChanges;
        }),
        switchAll()
      )
      .subscribe(
        ({ data, loading }) => {
          this.loading.emit(false);
          this.total.emit(data.allUsers.total);
          const users = this.extractData(data);
          this.results.emit(users);
        },
        (err: any) => {
          console.log(err);
          this.loading.emit(false);
        },
        () => {
          this.loading.emit(false);
        }
      );
  }
}
