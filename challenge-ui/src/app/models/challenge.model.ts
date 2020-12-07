import { User } from './user.model';

export class Challenge {
  id?: string;
  name: string;
  createdBy: string;
  createdOn: string;
  finishesOn: string;
  leader?: User;

  constructor(obj?: any) {
    this.id = obj && obj.id;
    this.name = obj && obj.name;
    this.createdBy = obj && obj.createdBy && obj.createdBy.name;
    this.createdOn = obj && obj.createdOn;
    this.finishesOn = obj && obj.finishesOn;
    if (obj && obj.leader) {
      this.leader = new User(obj.leader);
    }
  }
}
