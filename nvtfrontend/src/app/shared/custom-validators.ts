import { AbstractControl, ValidationErrors, ValidatorFn, FormGroup } from '@angular/forms';

// Validator to check password strength
export function passwordValidator(control: AbstractControl): ValidationErrors | null {
  const value = control.value || '';
  const hasUpperCase = /[A-Z]/.test(value);
  const hasSpecialCharacter = /[!@#$%^&*(),.?":{}|<>]/.test(value);

  const valid = hasUpperCase && hasSpecialCharacter;
  return valid ? null : { passwordStrength: 'Password must contain at least one uppercase letter and one special character.' };
}

// Validator to check if two passwords match
export const passwordsMatchValidator: ValidatorFn = (group: AbstractControl): ValidationErrors | null => {
  const newPassword = group.get('newPassword')?.value;
  const repeatPassword = group.get('repeatPassword')?.value;

  return newPassword === repeatPassword ? null : { passwordsMismatch: 'Passwords do not match.' };
};