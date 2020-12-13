export enum Gender {
  Female = 'Female',
  Male = 'Male',
}

export class User {
  id?: string;
  name: string;
  email: string;
  gender: Gender;
  age?: number;
  createdAt?: string;
  challenges?: number;
  participatesInChallenge?: boolean;
  hasInvitationToChallenge?: boolean;
  unreadInvitations: number;

  constructor(obj?: any) {
    this.id = obj && obj.id;
    this.name = obj && obj.name;
    this.email = obj && obj.email;
    this.gender = obj && obj.gender;
    this.age = obj && obj.age;
    this.createdAt = obj && obj.createdAt;
    this.challenges = obj && obj.challenges && obj.challenges.total;
    this.participatesInChallenge = obj && obj.participatesInChallenge;
    this.hasInvitationToChallenge =
      obj &&
      obj.invitations &&
      obj.invitations.results &&
      obj.invitations.results.length > 0;
    this.unreadInvitations =
      obj && obj.unreadInvitations && obj.unreadInvitations.total;
  }
}
