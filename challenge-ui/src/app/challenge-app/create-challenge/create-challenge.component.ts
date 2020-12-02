import { DatePipe } from '@angular/common';
import { CreateChallengeGQL } from './create-challenge.mutation.graphql-gen';
import { User } from '../../models/user.model';
import { Challenge } from '../../models/challenge.model';
import { Component, OnInit } from '@angular/core';
import { UserService } from 'src/app/services/user-service.service';
import {
  AbstractControl,
  FormBuilder,
  Validators,
  FormGroup,
} from '@angular/forms';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'create-challenge',
  templateUrl: './create-challenge.component.html',
})
export class CreateChallengeComponent implements OnInit {
  challenge: Challenge;
  user?: User;

  errorMessage: string;
  loading: boolean;

  form: FormGroup;
  name: AbstractControl;
  finishesOn: AbstractControl;

  constructor(
    private userService: UserService,
    private createChallenge: CreateChallengeGQL,
    private datePipe: DatePipe,
    fb: FormBuilder
  ) {
    this.loading = false;
    this.reset();
    this.form = fb.group({
      name: [this.challenge.name, Validators.required],
      finishesOn: [this.challenge.finishesOn, Validators.required],
    });
    this.name = this.form.controls['name'];
    this.finishesOn = this.form.controls['finishesOn'];

    this.name.valueChanges.subscribe((name) => {
      this.challenge.name = name;
    });
    this.finishesOn.valueChanges.subscribe((finishesOn) => {
      const date = new Date(finishesOn);
      this.challenge.finishesOn = date.toISOString();
    });
  }

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe((user) => {
      this.user = user;
      this.challenge.createdBy = user.id;
    });
  }

  reset(): void {
    this.challenge = new Challenge({ finishesOn: new Date() });
  }

  onSubmit(form: any): void {
    console.log('you submitted value:', form);
    console.log('challenge: ', this.challenge);
    this.loading = true;
    this.createChallenge
      .mutate({ challenge: this.challenge })
      .pipe(
        finalize(() => {
          this.loading = false;
          this.form.reset();
          this.reset();
        })
      )
      .subscribe(
        ({ data }) => {
          console.log('Created challenge:', data);
          this.errorMessage = null;
        },
        (error) => {
          console.log(error);
          this.errorMessage = error;
        }
      );
  }
}
