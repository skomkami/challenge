import {
  ValueOrder,
  ValueSummarization,
} from './../../../generated/types.graphql-gen';
import { DatePipe } from '@angular/common';
import { CreateChallengeGQL } from './create-challenge.mutation.graphql-gen';
import { User } from '../../models/user.model';
import { Measure } from '../../models/measure.model';
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
  description: AbstractControl;
  unitName: AbstractControl;
  allowDecimal: AbstractControl;
  finishesOn: AbstractControl;
  valueOrder: AbstractControl;
  valueSummarization: AbstractControl;

  keys = Object.keys;
  valueOrderSymbols = ValueOrder;
  valueSummarizationSymbols = ValueSummarization;

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
      description: [this.challenge.description, Validators.required],
      unitName: [this.challenge.measure.unitName, Validators.required],
      allowDecimal: [this.challenge.measure.allowDecimal],
      finishesOn: [this.challenge.finishesOn, Validators.required],
      valueOrder: [this.challenge.measure.valueOrder, Validators.required],
      valueSummarization: [
        this.challenge.measure.valueSummarization,
        Validators.required,
      ],
    });
    this.name = this.form.controls['name'];
    this.finishesOn = this.form.controls['finishesOn'];
    this.description = this.form.controls['description'];
    this.unitName = this.form.controls['unitName'];
    this.allowDecimal = this.form.controls['allowDecimal'];
    this.valueOrder = this.form.controls['valueOrder'];
    this.valueSummarization = this.form.controls['valueSummarization'];

    this.name.valueChanges.subscribe((name) => {
      this.challenge.name = name;
    });
    this.description.valueChanges.subscribe((description) => {
      this.challenge.description = description;
    });
    this.unitName.valueChanges.subscribe((unitName) => {
      this.challenge.measure.unitName = unitName;
    });
    this.finishesOn.valueChanges.subscribe((finishesOn) => {
      const date = new Date(finishesOn);
      this.challenge.finishesOn = date.toISOString();
    });
    this.allowDecimal.valueChanges.subscribe((allowDecimal) => {
      this.challenge.measure.allowDecimal = allowDecimal;
    });
    this.valueOrder.valueChanges.subscribe((valueOrder) => {
      this.challenge.measure.valueOrder = valueOrder;
    });
    this.valueSummarization.valueChanges.subscribe((valueSummarization) => {
      this.challenge.measure.valueSummarization = valueSummarization;
    });
  }

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe((user) => {
      this.user = user;
      this.challenge.createdBy = user.id;
    });
  }

  reset(): void {
    this.challenge = new Challenge({
      finishesOn: new Date(),
      createdBy: this.user && this.user.id,
      measure: new Measure({
        valueOrder: ValueOrder.BiggerWins,
        valueSummarization: ValueSummarization.Summarize,
      }),
    });
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
