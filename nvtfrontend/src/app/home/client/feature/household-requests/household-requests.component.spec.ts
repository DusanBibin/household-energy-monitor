import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HouseholdRequestsComponent } from './household-requests.component';

describe('HouseholdRequestsComponent', () => {
  let component: HouseholdRequestsComponent;
  let fixture: ComponentFixture<HouseholdRequestsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HouseholdRequestsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HouseholdRequestsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
