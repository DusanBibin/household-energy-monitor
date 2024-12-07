import { AbstractControl, ValidationErrors, ValidatorFn, FormGroup } from '@angular/forms';

// Validator to check password strength
export function passwordValidator(control: AbstractControl): ValidationErrors | null {
  const value = control.value || '';
  const hasLowerCase = /[a-z]/.test(value);
  const hasUpperCase = /[A-Z]/.test(value);
  const hasSpecialCharacter = /[!@#$%^&*(),.?":{}|<>]/.test(value);
  const hasNumeric = /[0-9]/.test(value);

  let message = '';
  if(!hasLowerCase) message = 'Password must contain at least one lowercase letter'
  if(!hasUpperCase) message = 'Password must contain at least one uppercase letter'
  if(!hasSpecialCharacter) message = 'Password must containt at least one special character'
  if(!hasNumeric) message = 'Password must containt at least one numeric character'

  const valid = hasLowerCase && hasUpperCase && hasSpecialCharacter && hasNumeric;
  return valid ? null : { passwordStrength: message};
}

// Validator to check if two passwords match
export function passwordsMatchValidator(controlName1: string, controlName2: string): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null => {
    const control1 = group.get(controlName1)?.value;
    const control2 = group.get(controlName2)?.value;

    return control1 === control2 ? null : { passwordsMismatch: 'Passwords do not match.' };
  };
}