import { currencyForCountry } from './country-currency';

describe('currencyForCountry', () => {
  it('maps India to INR and Germany to EUR', () => {
    expect(currencyForCountry('IN')).toBe('INR');
    expect(currencyForCountry('de')).toBe('EUR');
  });

  it('returns null for an unknown country', () => {
    expect(currencyForCountry('XX')).toBeNull();
    expect(currencyForCountry('')).toBeNull();
  });
});
