import { Challenge } from './../../models/challenge.model';
import {
  AllChallengesGQL,
  AllChallengesQuery,
  AllChallengesQueryVariables,
} from './challenges.query.graphql-gen';
import { tap, map, filter, debounceTime, switchAll } from 'rxjs/operators';
import { fromEvent } from 'rxjs';
import {
  Component,
  Output,
  OnInit,
  EventEmitter,
  ElementRef,
  Input,
} from '@angular/core';
@Component({
  selector: 'search-box',
  template: `
    <input
      type="text"
      class="form-control"
      placeholder="Challenge name ..."
      autofocus
    />
  `,
})
export class SearchBoxComponent implements OnInit {
  @Input() offset: number;
  @Input() pageSize: number;
  @Output() loading: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() total: EventEmitter<number> = new EventEmitter<number>();
  @Output() results: EventEmitter<Challenge[]> = new EventEmitter<
    Challenge[]
  >();
  @Output() removedSearch: EventEmitter<boolean> = new EventEmitter<boolean>();

  vars: AllChallengesQueryVariables;

  constructor(
    private challengesQuery: AllChallengesGQL,
    private el: ElementRef
  ) {
    this.vars = {
      size: this.pageSize,
      offset: this.offset,
    };
  }

  extractData(data: AllChallengesQuery): Array<Challenge> {
    return data.allChallenges.results.map(
      (graphQlChallenge) => new Challenge(graphQlChallenge)
    );
  }

  ngOnInit(): void {
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
          return this.challengesQuery.watch(this.vars).valueChanges;
        }),
        switchAll()
      )
      .subscribe(
        ({ data, loading }) => {
          this.loading.emit(false);
          this.total.emit(data.allChallenges.total);
          const challenges = this.extractData(data);
          this.results.emit(challenges);
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
