import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { superadminFirstLoginGuard } from './superadmin-first-login.guard';

describe('superadminFirstLoginGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => superadminFirstLoginGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
