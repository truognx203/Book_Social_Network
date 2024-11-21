import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActiveAccountComponent } from './active-account.component';

describe('ActiveAccountComponent', () => {
  let component: ActiveAccountComponent;
  let fixture: ComponentFixture<ActiveAccountComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ActiveAccountComponent]
    });
    fixture = TestBed.createComponent(ActiveAccountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
