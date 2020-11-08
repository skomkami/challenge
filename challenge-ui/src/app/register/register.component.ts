import { User, Sex } from '../models/user.model';
import { Component, OnInit } from '@angular/core';
import { CreateUserGQL } from './register.mutation.graphql-gen'
import { finalize } from 'rxjs/operators';
import {
  FormBuilder,
  FormGroup,
  Validators,
  AbstractControl
  } from '@angular/forms';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})

export class RegisterComponent implements OnInit {

  registerForm: FormGroup;
  name: AbstractControl;
  email: AbstractControl;
  sex: AbstractControl;

  user: User;
  errorMessage: string;

  keys = Object.keys;
  symbols = Sex;

  constructor(private createUser: CreateUserGQL, fb: FormBuilder) {
    this.resetUser();
    this.registerForm = fb.group({
      'name': [this.user.name, Validators.required],
      'email': [this.user.email, Validators.required],
      'sex':[this.user.sex, Validators.required]
    });
    this.name = this.registerForm.controls['name'];
    this.email = this.registerForm.controls['email'];
    this.sex = this.registerForm.controls['sex'];

    this.name.valueChanges.subscribe( name => { this.user.name = name; });
    this.email.valueChanges.subscribe( email => { this.user.email = email; });
    this.sex.valueChanges.subscribe( sex => { this.user.sex = sex; });
  }

  ngOnInit(): void {
  }

  resetUser(): void {
    this.user = new User({sex: Sex.Male});
  }

  onSubmit(form: any): void {
    console.log('you submitted value:', form);
    console.log('user: ', this.user);
    this.createUser
    .mutate({user: this.user})
    .pipe(
      finalize(() => {
        this.resetUser();
        this.registerForm.reset();
      })
    )
    .subscribe(({ data }) => {
      console.log('Created user:', data);
      this.errorMessage = null;
    }, (error) => {
      console.log(error);
      this.errorMessage = error;
    });
  }

}
