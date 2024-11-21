import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {AuthenticationService} from "../../services/services/authentication.service";

@Component({
  selector: 'app-active-account',
  templateUrl: './active-account.component.html',
  styleUrls: ['./active-account.component.scss']
})
export class ActiveAccountComponent {

  message = '';
  isOkay = true;
  submitted = false;

  constructor(
    private router: Router,
    private authService: AuthenticationService,
  ) {
  }


  confirmAccount(token: string) {
    this.authService.confirm({
      token
    }).subscribe({
      next: () => {
        this.message = 'Your account has been successfully activated. Now you can proceed to login';
        this.submitted = true;
        this.isOkay=true;
      }, error: () => {
        this.message = 'Token expired or invalid';
        this.isOkay = false;
        this.submitted = false;
      }
    })
  }

  redirectToLogin() {
    this.router.navigate(['login']);
  }
}
