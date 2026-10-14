import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private readonly formBuilder = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  readonly form = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
  });

  constructor() {
    if (this.auth.isAuthenticated()) {
      this.redirectByRol();
    }
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.error.set(null);

    this.auth.login(this.form.getRawValue()).subscribe({
      next: () => this.redirectByRol(),
      error: (err) => {
        this.loading.set(false);
        this.error.set(err?.status === 401 ? 'Credenciales inválidas' : 'No se pudo iniciar sesión');
      },
    });
  }

  private redirectByRol(): void {
    const rol = this.auth.rol();
    this.router.navigate([rol === 'ADMIN' ? '/dashboard/admin' : '/dashboard/empleado']);
  }
}
