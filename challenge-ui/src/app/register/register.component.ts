import { User, Gender } from '../models/user.model';
import { Component, OnInit } from '@angular/core';
import { CreateUserGQL } from './register.mutation.graphql-gen';
import { finalize } from 'rxjs/operators';
import {
  FormBuilder,
  FormGroup,
  Validators,
  AbstractControl,
} from '@angular/forms';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  name: AbstractControl;
  email: AbstractControl;
  gender: AbstractControl;

  user: User;
  errorMessage: string;
  loading: boolean;

  keys = Object.keys;
  symbols = Gender;

  constructor(private createUser: CreateUserGQL, fb: FormBuilder) {
    this.loading = false;
    this.resetUser();
    this.registerForm = fb.group({
      name: [this.user.name, Validators.required],
      email: [this.user.email, Validators.required],
      gender: [this.user.gender, Validators.required],
    });
    this.name = this.registerForm.controls['name'];
    this.email = this.registerForm.controls['email'];
    this.gender = this.registerForm.controls['gender'];

    this.name.valueChanges.subscribe((name) => {
      this.user.name = name;
    });
    this.email.valueChanges.subscribe((email) => {
      this.user.email = email;
    });
    this.gender.valueChanges.subscribe((gender) => {
      this.user.gender = gender;
    });
  }

  ngOnInit(): void {}

  resetUser(): void {
    this.user = new User({ gender: Gender.Male });
  }

  onSubmit(form: any): void {
    console.log('you submitted value:', form);
    console.log('user: ', this.user);
    this.loading = true;
    this.createUser
      .mutate({ user: this.user })
      .pipe(
        finalize(() => {
          this.loading = false;
          this.registerForm.reset();
          this.resetUser();
        })
      )
      .subscribe(
        ({ data }) => {
          console.log('Created user:', data);
          this.errorMessage = null;
        },
        (error) => {
          console.log(error);
          this.errorMessage = error;
        }
      );
  }
}
