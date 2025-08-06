import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RealestateListDumbComponent } from './realestate-list-dumb.component';

describe('RealestateListDumbComponent', () => {
  let component: RealestateListDumbComponent;
  let fixture: ComponentFixture<RealestateListDumbComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RealestateListDumbComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RealestateListDumbComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
