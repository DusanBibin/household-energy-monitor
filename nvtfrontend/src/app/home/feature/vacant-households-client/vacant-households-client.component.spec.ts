import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VacantHouseholdsClientComponent } from './vacant-households-client.component';

describe('VacantHouseholdsClientComponent', () => {
  let component: VacantHouseholdsClientComponent;
  let fixture: ComponentFixture<VacantHouseholdsClientComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VacantHouseholdsClientComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VacantHouseholdsClientComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
