import { Component } from '@angular/core';
import { ToastService } from '../core/toast.service';

@Component({
  selector: 'app-shell',
  templateUrl: './shell.component.html',
  styleUrls: ['./shell.component.scss']
})
export class ShellComponent {
  readonly persona = 'HR Manager';

  constructor(readonly toasts: ToastService) {}
}
