import { MeasureValue } from './measure-value.model';

export class Activity {
  id?: string;
  userId: string;
  date: string;
  value: MeasureValue;

  constructor(obj?: any) {
    this.id = obj && obj.id;
    this.date = obj && obj.date;
    if (obj && obj.value) {
      this.value = new MeasureValue(obj.value);
    }
  }
}
