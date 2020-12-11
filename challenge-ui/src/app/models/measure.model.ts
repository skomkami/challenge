export enum ValueSummarization {
  Summarize = 'Summarize',
  Best = 'Best',
}

export enum ValueOrder {
  BiggerWins = 'BiggerWins',
  SmallerWins = 'SmallerWins',
}

export class Measure {
  unitName: string;
  allowDecimal: boolean;
  valueSummarization: ValueSummarization;
  valueOrder: ValueOrder;

  constructor(obj?: any) {
    this.unitName = obj && obj.unitName;
    this.allowDecimal = (obj && obj.allowDecimal) || false;
    this.valueSummarization = obj && obj.valueSummarization;
    this.valueOrder = obj && obj.valueOrder;
  }
}
