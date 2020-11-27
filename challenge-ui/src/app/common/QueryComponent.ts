import { OnInit, Component, Directive } from '@angular/core';
import * as Apollo from 'apollo-angular';

@Component({
  template: '',
})
export class QueryComponent<DataType, QueryVariables> implements OnInit {
  loading: boolean;
  pageSize: number = 10;
  offset: number = 0;

  vars: QueryVariables;

  constructor(private queryService: Apollo.Query<DataType, QueryVariables>) {}

  ngOnInit(): void {
    this.queryService
      .watch(this.vars)
      .valueChanges.subscribe(({ data, loading }) => {
        this.loading = loading;
        this.extractData(data);
      });
  }

  extractData(data: DataType): void {}
}
