import { Pipe, PipeTransform } from '@angular/core';
import { MeasureValue } from '../models/measure-value.model';
import { Measure } from '../models/measure.model';

@Pipe({ name: 'displayMeasureValue' })
export class DisplayMeasureValue implements PipeTransform {
  transform(value: MeasureValue, measure: Measure): string {
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
