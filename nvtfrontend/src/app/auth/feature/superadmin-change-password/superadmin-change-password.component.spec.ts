import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SuperadminChangePasswordComponent } from './superadmin-change-password.component';

describe('SuperadminChangePasswordComponent', () => {
  let component: SuperadminChangePasswordComponent;
  let fixture: ComponentFixture<SuperadminChangePasswordComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SuperadminChangePasswordComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SuperadminChangePasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
