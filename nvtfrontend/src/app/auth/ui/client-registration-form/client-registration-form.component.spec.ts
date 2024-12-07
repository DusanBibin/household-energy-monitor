import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientRegistrationFormComponent } from './client-registration-form.component';

describe('ClientRegistrationFormComponent', () => {
  let component: ClientRegistrationFormComponent;
  let fixture: ComponentFixture<ClientRegistrationFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientRegistrationFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClientRegistrationFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
