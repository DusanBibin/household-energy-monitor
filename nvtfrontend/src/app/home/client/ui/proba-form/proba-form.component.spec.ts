import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProbaFormComponent } from './proba-form.component';

describe('ProbaFormComponent', () => {
  let component: ProbaFormComponent;
  let fixture: ComponentFixture<ProbaFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProbaFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProbaFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
