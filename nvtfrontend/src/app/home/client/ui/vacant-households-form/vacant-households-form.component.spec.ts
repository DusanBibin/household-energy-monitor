import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VacantHouseholdsFormComponent } from './vacant-households-form.component';

describe('VacantHouseholdsFormComponent', () => {
  let component: VacantHouseholdsFormComponent;
  let fixture: ComponentFixture<VacantHouseholdsFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VacantHouseholdsFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VacantHouseholdsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
