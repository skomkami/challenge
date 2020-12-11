import { Pipe, PipeTransform } from '@angular/core';
import { Measure } from '../models/measure.model';
import { Summary } from '../models/summary.model';

@Pipe({ name: 'summaryValue' })
export class SummaryValue implements PipeTransform {
  transform(summary: Summary, measureOpt?: Measure): string {
    const measure =
      (summary.challenge && summary.challenge.measure) || measureOpt;
    const value = summary.summaryValue;

    var finalValue = undefined;
    if (measure.allowDecimal) {
      finalValue = value.decimalValue;
    } else {
      finalValue = value.integerValue;
    }

    if (finalValue) {
      return finalValue + ' ' + measure.unitName;
    } else {
      return 'No activity yet';
    }
  }
}
