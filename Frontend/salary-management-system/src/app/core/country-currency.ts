export const COUNTRY_CURRENCY: { [country: string]: string } = {
  US: 'USD',
  IN: 'INR',
  GB: 'GBP',
  DE: 'EUR',
  FR: 'EUR',
  NL: 'EUR',
  IE: 'EUR',
  SG: 'SGD',
  AU: 'AUD',
  CA: 'CAD',
  JP: 'JPY',
  BR: 'BRL',
  CH: 'CHF'
};

export function currencyForCountry(country: string): string | null {
  if (!country) {
    return null;
  }
  return COUNTRY_CURRENCY[country.toUpperCase()] || null;
}
