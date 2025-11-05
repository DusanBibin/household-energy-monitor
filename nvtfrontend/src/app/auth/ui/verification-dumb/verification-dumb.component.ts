import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { ResponseData } from '../../../shared/model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-verification-dumb',
  standalone: false,
  
  templateUrl: './verification-dumb.component.html',
  styleUrl: './verification-dumb.component.css'
})
export class VerificationDumbComponent implements OnChanges{
  isLoading = false;

  constructor(private router: Router){}
  @Input() response: ResponseData | null = null;


  ngOnChanges(changes: SimpleChanges): void {
      if(changes['response']){
        if(this.response){
          console.log(this.response)
        }
      }
  }



  navigateLogin(){
    this.router.navigate(['/auth'], {replaceUrl: true})

  }
}
