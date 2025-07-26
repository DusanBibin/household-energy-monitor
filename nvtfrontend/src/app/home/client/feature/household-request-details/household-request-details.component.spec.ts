import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HouseholdRequestDetailsComponent } from './household-request-details.component';

describe('HouseholdRequestDetailsComponent', () => {
  let component: HouseholdRequestDetailsComponent;
  let fixture: ComponentFixture<HouseholdRequestDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HouseholdRequestDetailsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HouseholdRequestDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
