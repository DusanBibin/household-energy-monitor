import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SchedulesDumbComponent } from './schedules-dumb.component';

describe('SchedulesDumbComponent', () => {
  let component: SchedulesDumbComponent;
  let fixture: ComponentFixture<SchedulesDumbComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SchedulesDumbComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SchedulesDumbComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
