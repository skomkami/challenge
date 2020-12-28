import { Measure } from './measure.model';
import { User } from './user.model';

export enum Accessibility {
  Public = 'Public',
  Private = 'Private',
}

export class Challenge {
  id?: string;
  name: string;
  description: string;
  createdById: string;
  createdBy: User;
  createdOn: string;
  finishesOn: string;
  leader?: User;
  measure: Measure;
  accessibility: Accessibility;
  userHasAccess: boolean;

  constructor(obj?: any) {
    this.id = obj && obj.id;
    this.name = obj && obj.name;
    this.description = obj && obj.description;
    this.createdById = obj && obj.createdById;
    if (obj && obj.createdBy) {
      this.createdBy = new User(obj.createdBy);
    }
    this.createdOn = obj && obj.createdOn;
    this.finishesOn = obj && obj.finishesOn;
    if (obj && obj.leader) {
      this.leader = new User(obj.leader);
    }
    if (obj && obj.measure) {
      this.measure = new Measure(obj.measure);
    }
    this.accessibility = obj && obj.accessibility;
    this.userHasAccess = obj && obj.userHasAccess;
  }
}
