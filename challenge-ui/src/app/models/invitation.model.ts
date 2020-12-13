import { User } from './user.model';
import { Challenge } from './challenge.model';
export class Invitation {
  id: string;
  forUser?: User;
  fromUser: User;
  toChallenge: Challenge;
  sendTime: string;

  constructor(obj: any) {
    this.id = obj && obj.id;
    if (obj && obj.forUser) {
      this.forUser = new User(obj && obj.forUser);
    }
    this.fromUser = new User(obj && obj.fromUser);
    this.toChallenge = new Challenge(obj && obj.toChallenge);
    this.sendTime = obj && obj.sendTime;
  }
}
