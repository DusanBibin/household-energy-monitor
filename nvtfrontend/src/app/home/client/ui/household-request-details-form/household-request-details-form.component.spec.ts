import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HouseholdRequestDetailsFormComponent } from './household-request-details-form.component';

describe('HouseholdRequestDetailsFormComponent', () => {
  let component: HouseholdRequestDetailsFormComponent;
  let fixture: ComponentFixture<HouseholdRequestDetailsFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HouseholdRequestDetailsFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HouseholdRequestDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
