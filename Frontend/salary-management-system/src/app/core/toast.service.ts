import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface Toast {
  id: number;
  message: string;
  kind: 'success' | 'error';
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private nextId = 1;
  private readonly messages = new BehaviorSubject<Toast[]>([]);
  readonly messages$ = this.messages.asObservable();

  success(message: string): void {
    this.push(message, 'success');
  }

  error(message: string): void {
    this.push(message, 'error');
  }

  private push(message: string, kind: Toast['kind']): void {
    const toast: Toast = { id: this.nextId++, message, kind };
    this.messages.next([...this.messages.value, toast]);
    setTimeout(() => this.dismiss(toast.id), 4000);
  }

  dismiss(id: number): void {
    this.messages.next(this.messages.value.filter((item) => item.id !== id));
  }
}
