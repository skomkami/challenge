export enum Sex {
  Female = 'Female',
  Male = 'Male'
};

export class User {
  id?: string;
  name: string;
  email: string;
  sex: Sex;
  age?: number;
  createdAt?: string
  
  constructor(obj?: any) {
    this.id = obj && obj.id;
    this.name = obj && obj.name
    this.email = obj && obj.email
    this.sex = obj && obj.sex
    this.age = obj && obj.age
    this.createdAt = obj && obj.createdAt
  }
}