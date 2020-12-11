import { AbstractControl, ValidatorFn } from '@angular/forms';

export function positiveNumberValidator(validate: boolean): ValidatorFn {
  return (control: AbstractControl): { [key: string]: any } => {
    if (validate) {
      const isNotOk = Number(control.value) <= 0;
      const validationResult = isNotOk
        ? { nonPositive: { value: control.value } }
        : null;
      return validationResult;
    } else {
      return null;
    }
  };
}
