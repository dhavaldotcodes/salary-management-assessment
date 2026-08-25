import { Component, OnInit } from '@angular/core';
import { GroupStat, InsightResponse } from '../../core/models';
import { InsightApiService } from '../../core/insight-api.service';

@Component({
  selector: 'app-insights',
  templateUrl: './insights.component.html',
  styleUrls: ['./insights.component.scss']
})
export class InsightsComponent implements OnInit {
  insights: InsightResponse | null = null;
  error: string | null = null;
  loading = true;

  constructor(private insightApi: InsightApiService) {}

  ngOnInit(): void {
    this.insightApi.load().subscribe({
      next: (data) => {
        this.insights = data;
        this.loading = false;
      },
      error: () => {
        this.error = 'Could not load pay insights. Is the API running on port 8080?';
        this.loading = false;
      }
    });
  }

  barWidth(row: GroupStat, rows: GroupStat[]): string {
    const max = Math.max(...rows.map((item) => item.payrollUsd), 1);
    return `${Math.max((row.payrollUsd / max) * 100, 4)}%`;
  }

  money(value: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      maximumFractionDigits: 0
    }).format(value || 0);
  }
}
