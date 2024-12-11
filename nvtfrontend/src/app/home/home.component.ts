import { Component } from '@angular/core';
import { JwtService } from '../shared/jwt-service/jwt.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  

  constructor(private jwtService: JwtService, private router: Router){

  }

  signOut(){
    this.jwtService.logout();
    this.router.navigate(['../auth/login'])
  }
  
}
