import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HouseholdDetailsFormComponent } from './household-details-form.component';

describe('HouseholdDetailsFormComponent', () => {
  let component: HouseholdDetailsFormComponent;
  let fixture: ComponentFixture<HouseholdDetailsFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HouseholdDetailsFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HouseholdDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
