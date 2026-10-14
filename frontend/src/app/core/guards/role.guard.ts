import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { Rol } from '../models/user.model';
import { AuthService } from '../services/auth.service';

export const roleGuard = (expected: Rol): CanActivateFn => {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    const rol = auth.rol();

    if (rol === expected) {
      return true;
    }
    if (rol === 'ADMIN') {
      return router.createUrlTree(['/dashboard/admin']);
    }
    if (rol === 'EMPLEADO') {
      return router.createUrlTree(['/dashboard/empleado']);
    }
    return router.createUrlTree(['/login']);
  };
};
