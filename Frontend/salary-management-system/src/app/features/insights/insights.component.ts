import { Component, OnInit } from '@angular/core';
import { GroupStat, InsightResponse } from '../../core/models';
import { InsightApiService } from '../../core/insight-api.service';
import { formatUsd } from '../../core/format-money';

type InsightGroup = 'country' | 'department' | 'jobLevel';

@Component({
  selector: 'app-insights',
  templateUrl: './insights.component.html',
  styleUrls: ['./insights.component.scss']
})
export class InsightsComponent implements OnInit {
  insights: InsightResponse | null = null;
  error: string | null = null;
  loading = true;
  groupBy: InsightGroup = 'country';

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

  get rows(): GroupStat[] {
    if (!this.insights) {
      return [];
    }
    if (this.groupBy === 'department') {
      return this.insights.byDepartment;
    }
    if (this.groupBy === 'jobLevel') {
      return this.insights.byJobLevel;
    }
    return this.insights.byCountry;
  }

  get groupLabel(): string {
    if (this.groupBy === 'department') {
      return 'Department';
    }
    if (this.groupBy === 'jobLevel') {
      return 'Job level';
    }
    return 'Country';
  }

  barWidth(row: GroupStat): string {
    const max = Math.max(...this.rows.map((item) => item.payrollUsd), 1);
    return `${Math.max((row.payrollUsd / max) * 100, 4)}%`;
  }

  money(value: number): string {
    return formatUsd(value);
  }
}
