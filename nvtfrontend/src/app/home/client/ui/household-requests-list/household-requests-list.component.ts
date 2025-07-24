import { Component } from '@angular/core';

@Component({
  selector: 'app-household-requests-list',
  standalone: false,
  templateUrl: './household-requests-list.component.html',
  styleUrl: './household-requests-list.component.css'
})
export class HouseholdRequestsListComponent {
  
  protected kurac: number[] = Array(20).fill(0)
  constructor(){
    console.log(this.kurac)
  }
}
