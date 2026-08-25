export function formatMoney(value: number, currency: string): string {
  try {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency,
      maximumFractionDigits: 0
    }).format(value || 0);
  } catch {
    return String(value || 0);
  }
}

export function formatUsd(value: number): string {
  return formatMoney(value, 'USD');
}
