import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, Session } from '../models/user.model';

const STORAGE_KEY = 'asis.session';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  private readonly sessionSignal = signal<Session | null>(this.readStoredSession());

  readonly session = this.sessionSignal.asReadonly();
  readonly rol = computed(() => this.sessionSignal()?.rol ?? null);
  readonly isAuthenticated = computed(() => {
    const session = this.sessionSignal();
    return session !== null && !this.isExpired(session);
  });

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/login`, credentials)
      .pipe(tap((response) => this.store({ ...response, exp: decodeExp(response.token) })));
  }

  logout(): void {
    localStorage.removeItem(STORAGE_KEY);
    this.sessionSignal.set(null);
    this.router.navigate(['/login']);
  }

  token(): string | null {
    return this.sessionSignal()?.token ?? null;
  }

  private store(session: Session): void {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(session));
    this.sessionSignal.set(session);
  }

  private readStoredSession(): Session | null {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) {
      return null;
    }
    try {
      const session = JSON.parse(raw) as Session;
      return this.isExpired(session) ? null : session;
    } catch {
      return null;
    }
  }

  private isExpired(session: Session): boolean {
    return session.exp !== undefined && Date.now() >= session.exp * 1000;
  }
}

function decodeExp(token: string): number | undefined {
  try {
    const payload = token.split('.')[1];
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
    const json = JSON.parse(atob(base64)) as { exp?: number };
    return typeof json.exp === 'number' ? json.exp : undefined;
  } catch {
    return undefined;
  }
}
