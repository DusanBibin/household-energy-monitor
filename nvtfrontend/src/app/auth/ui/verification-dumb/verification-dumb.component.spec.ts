import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VerificationDumbComponent } from './verification-dumb.component';

describe('VerificationDumbComponent', () => {
  let component: VerificationDumbComponent;
  let fixture: ComponentFixture<VerificationDumbComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [VerificationDumbComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VerificationDumbComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
