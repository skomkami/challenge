import { User } from './user.model';

export class Summary {
  id?: string;
  challengeName: string;
  summaryValue: number;
  position: number;
  userId: string;
  user?: User;
  lastActive?: string;

  constructor(obj?: any) {
    this.id = obj && obj.id;
    this.challengeName = obj && obj.challenge && obj.challenge.name;
    this.lastActive = obj && obj.challenge && obj.challenge.lastActive;
    this.summaryValue = obj && obj.summaryValue;
    this.position = obj && obj.position;
    this.userId = obj && obj.userId;
    if (obj && obj.user) {
      this.user = new User(obj.user);
    }
  }
}
