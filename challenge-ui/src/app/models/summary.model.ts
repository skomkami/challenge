import { MeasureValue } from './measure-value.model';
import { Challenge } from './challenge.model';
import { User } from './user.model';

export class Summary {
  id?: string;
  challenge: Challenge;
  summaryValue: MeasureValue;
  position: number;
  userId: string;
  user?: User;
  lastActive?: string;

  constructor(obj?: any) {
    this.id = obj && obj.id;
    this.challenge = new Challenge(obj && obj.challenge);
    this.lastActive = obj && obj.lastActive;
    if (obj && obj.summaryValue) {
      this.summaryValue = new MeasureValue(obj.summaryValue);
    }
    this.position = obj && obj.position;
    this.userId = obj && obj.userId;
    if (obj && obj.user) {
      this.user = new User(obj.user);
    }
  }
}
