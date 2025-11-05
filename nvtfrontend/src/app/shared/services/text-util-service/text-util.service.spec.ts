import { TestBed } from '@angular/core/testing';

import { TextUtilServiceService } from './text-util.service';

describe('TextUtilServiceService', () => {
  let service: TextUtilServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TextUtilServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
