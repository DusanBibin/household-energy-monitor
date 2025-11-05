import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HouseholdDetailsComponent } from './household-details.component';

describe('HouseholdDetailsComponent', () => {
  let component: HouseholdDetailsComponent;
  let fixture: ComponentFixture<HouseholdDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HouseholdDetailsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HouseholdDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
