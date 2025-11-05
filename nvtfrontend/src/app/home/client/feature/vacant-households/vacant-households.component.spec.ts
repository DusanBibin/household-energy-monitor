import { ComponentFixture, TestBed } from '@angular/core/testing';
import { VacantHouseholdsComponent } from './vacant-households.component';

describe('VacantHouseholdsComponent', () => {
  let component: VacantHouseholdsComponent;
  let fixture: ComponentFixture<VacantHouseholdsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VacantHouseholdsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VacantHouseholdsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
