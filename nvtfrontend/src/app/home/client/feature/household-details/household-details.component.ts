import { Component, OnInit } from '@angular/core';
import { HouseholdDetailsFormComponent } from "../../ui/household-details-form/household-details-form.component";
import { ActivatedRoute, Router } from '@angular/router';
import { ClientService } from '../../data-access/client.service';

@Component({
  selector: 'app-household-details',
  standalone: false,
  templateUrl: './household-details.component.html',
  styleUrl: './household-details.component.css'
})
export class HouseholdDetailsComponent implements OnInit {

  constructor(private route: ActivatedRoute, private clientService: ClientService){

  }

  ngOnInit(): void {
    

  }
}
