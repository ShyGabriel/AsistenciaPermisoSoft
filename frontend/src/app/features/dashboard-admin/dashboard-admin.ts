import { Component, inject } from '@angular/core';

import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-dashboard-admin',
  templateUrl: './dashboard-admin.html',
  styleUrl: './dashboard-admin.css',
})
export class DashboardAdmin {
  private readonly auth = inject(AuthService);

  readonly session = this.auth.session;

  logout(): void {
    this.auth.logout();
  }
}
