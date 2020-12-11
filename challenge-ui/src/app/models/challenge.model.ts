import { Measure } from './measure.model';
import { User } from './user.model';

export class Challenge {
  id?: string;
  name: string;
  description: string;
  createdBy: string;
  createdOn: string;
  finishesOn: string;
  leader?: User;
  measure: Measure;

  constructor(obj?: any) {
    this.id = obj && obj.id;
    this.name = obj && obj.name;
    this.description = obj && obj.description;
    this.createdBy = obj && obj.createdBy && obj.createdBy.name;
    this.createdOn = obj && obj.createdOn;
    this.finishesOn = obj && obj.finishesOn;
    if (obj && obj.leader) {
      this.leader = new User(obj.leader);
    }
    if (obj && obj.measure) {
      this.measure = new Measure(obj.measure);
    }
  }
}
