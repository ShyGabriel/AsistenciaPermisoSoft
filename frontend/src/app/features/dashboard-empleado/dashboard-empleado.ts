import { Component, inject } from '@angular/core';

import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-dashboard-empleado',
  templateUrl: './dashboard-empleado.html',
  styleUrl: './dashboard-empleado.css',
})
export class DashboardEmpleado {
  private readonly auth = inject(AuthService);

  readonly session = this.auth.session;

  logout(): void {
    this.auth.logout();
  }
}
