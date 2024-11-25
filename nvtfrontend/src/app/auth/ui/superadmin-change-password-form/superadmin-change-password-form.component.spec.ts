import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SuperadminChangePasswordFormComponent } from './superadmin-change-password-form.component';

describe('SuperadminChangePasswordFormComponent', () => {
  let component: SuperadminChangePasswordFormComponent;
  let fixture: ComponentFixture<SuperadminChangePasswordFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SuperadminChangePasswordFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SuperadminChangePasswordFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
