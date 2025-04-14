import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { UserService } from './shared/services/user-service/user.service';
import { Role } from './shared/model';
import { forkJoin } from 'rxjs';
import { FileService } from './shared/services/file-service/file.service';
import { JwtService } from './shared/services/jwt-service/jwt.service';
import { AuthService } from './auth/data-access/auth.service';
@Component({
    selector: 'app-root',
    imports: [RouterOutlet],
    templateUrl: './app.component.html',
    styleUrl: './app.component.css'
})
export class AppComponent implements OnInit{
  title = 'nvtfrontend';


  constructor(private fileService: FileService, private userService: UserService, private jwtService: JwtService, private authService: AuthService){

  }

  ngOnInit(): void {



    // this.authService.checkAuth().subscribe({
    //   next: value => {
    //     forkJoin({
    //       profileImg: this.fileService.getProfileImage(),
    //       partialUserData: this.userService.getPartialUserData()
    //     }).subscribe({
    //       next: ({profileImg, partialUserData}) => {
            
    //         console.log(partialUserData)
    //         let imgUri: string = URL.createObjectURL(profileImg);
    //         this.jwtService.setUser({data: partialUserData, profileImage: imgUri})
    
    //         console.log(imgUri)
            
    //       },
    //       error: (error) => {
    //         console.log(error)
    //         console.log("nesto ne valja u forku")
    //       }
    //     })
    //   },
    //   error: err => {
    //     console.log(err)
    //     console.log("nesto ne valja u authu")
    //   }
    // })


    forkJoin({
      profileImg: this.fileService.getProfileImage(),
      partialUserData: this.userService.getPartialUserData()
    }).subscribe({
      next: ({profileImg, partialUserData}) => {
        
        console.log(partialUserData)
        let imgUri: string = URL.createObjectURL(profileImg);
        this.jwtService.setUser({data: partialUserData, profileImage: imgUri})

        console.log(imgUri)
        
      },
      error: () => {
        // console.log(error)
        // console.log("nesto ne valja u forku")
      }
    })


    
  }
}
