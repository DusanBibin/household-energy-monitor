import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HouseholdRequestsListComponent } from './household-requests-list.component';

describe('HouseholdRequestsListComponent', () => {
  let component: HouseholdRequestsListComponent;
  let fixture: ComponentFixture<HouseholdRequestsListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HouseholdRequestsListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HouseholdRequestsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
