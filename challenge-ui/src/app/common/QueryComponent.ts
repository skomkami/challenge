import { OnInit, Component, Directive } from '@angular/core';
import * as Apollo from 'apollo-angular';

@Component({
  template: '',
})
export class QueryComponent<DataType, QueryVariables> implements OnInit {
  loading: boolean;
  pageSize: number = 8;
  offset: number = 0;
  total: number;
  nextPage: boolean;

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

  handlePageChange(event): void {
    const newOffset = event.pageIndex * this.pageSize;
    this.updateVarsOffset(newOffset);
    this.ngOnInit();
  }

  updateVarsOffset(newOffset: number): void {}

  extractData(data: DataType): void {}
}
