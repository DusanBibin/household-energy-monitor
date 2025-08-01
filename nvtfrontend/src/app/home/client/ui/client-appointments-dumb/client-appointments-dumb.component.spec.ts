import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientAppointmentsDumbComponent } from './client-appointments-dumb.component';

describe('ClientAppointmentsDumbComponent', () => {
  let component: ClientAppointmentsDumbComponent;
  let fixture: ComponentFixture<ClientAppointmentsDumbComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientAppointmentsDumbComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClientAppointmentsDumbComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
