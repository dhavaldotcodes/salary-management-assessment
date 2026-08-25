import { formatMoney, formatUsd } from './format-money';

describe('formatMoney', () => {
  it('formats USD without cents', () => {
    expect(formatUsd(125000)).toContain('125,000');
  });

  it('does not throw on a bad currency code', () => {
    expect(formatMoney(10, 'NOTACODE')).toBe('10');
  });
});
