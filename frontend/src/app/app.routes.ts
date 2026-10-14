import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/login/login').then((m) => m.Login),
  },
  {
    path: 'dashboard/admin',
    canActivate: [authGuard, roleGuard('ADMIN')],
    loadComponent: () =>
      import('./features/dashboard-admin/dashboard-admin').then((m) => m.DashboardAdmin),
  },
  {
    path: 'dashboard/empleado',
    canActivate: [authGuard, roleGuard('EMPLEADO')],
    loadComponent: () =>
      import('./features/dashboard-empleado/dashboard-empleado').then((m) => m.DashboardEmpleado),
  },
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: '**', redirectTo: 'login' },
];
