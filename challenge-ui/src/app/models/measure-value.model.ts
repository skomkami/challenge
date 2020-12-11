import { Measure, ValueOrder, ValueSummarization } from './measure.model';

export class MeasureValue {
  integerValue?: number;
  decimalValue?: number;

  constructor(obj?: any) {
    this.integerValue = obj && obj.integerValue;
    this.decimalValue = obj && obj.decimalValue;
  }

  updated(value: MeasureValue, measure: Measure): MeasureValue {
    if (measure.valueOrder == ValueOrder.BiggerWins) {
      if (measure.valueSummarization == ValueSummarization.Best) {
        return new MeasureValue({
          integerValue:
            (this.integerValue &&
              Math.max(this.integerValue, value.integerValue)) ||
            value.integerValue,
          decimalValue:
            (this.decimalValue &&
              Math.max(this.decimalValue, value.decimalValue)) ||
            value.decimalValue,
        });
      } else {
        return new MeasureValue({
          integerValue:
            (this.integerValue && this.integerValue + value.integerValue) ||
            value.integerValue,
          decimalValue:
            (this.decimalValue && this.decimalValue + value.decimalValue) ||
            value.decimalValue,
        });
      }
    } else {
      if (measure.valueSummarization == ValueSummarization.Best) {
        return new MeasureValue({
          integerValue:
            (this.integerValue &&
              Math.min(this.integerValue, value.integerValue)) ||
            value.integerValue,
          decimalValue:
            (this.decimalValue &&
              Math.min(this.decimalValue, value.decimalValue)) ||
            value.decimalValue,
        });
      } else {
        //not supported
        return this;
      }
    }
  }
}

export function measureValueOrdering(
  a: MeasureValue,
  b: MeasureValue,
  measure: Measure
): number {
  const x = (measure.allowDecimal && a.decimalValue) || a.integerValue;
  const y = (measure.allowDecimal && b.decimalValue) || b.integerValue;
  if (!x && !y) {
    return 0;
  } else if (x && !y) {
    return -1;
  } else if (!x && y) {
    return 1;
  } else {
    const compareResult = x - y;
    if (measure.valueOrder == ValueOrder.BiggerWins) {
      return -compareResult;
    } else {
      return compareResult;
    }
  }
}
