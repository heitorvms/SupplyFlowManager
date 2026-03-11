import type { AppLocale } from "@/plugins/i18n";

const INTL_LOCALE_BY_APP_LOCALE: Record<AppLocale, string> = {
  en: "en-US",
  pt: "pt-BR",
  es: "es-ES",
};

function getIntlLocale(locale: AppLocale): string {
  return INTL_LOCALE_BY_APP_LOCALE[locale] ?? "en-US";
}

export function formatNumber(
  value: number,
  locale: AppLocale,
  options: Intl.NumberFormatOptions = {},
) {
  const safeValue = Number.isFinite(value) ? value : 0;

  return new Intl.NumberFormat(getIntlLocale(locale), options).format(safeValue);
}

export function formatCurrency(
  value: number,
  locale: AppLocale,
  currency = "BRL",
) {
  return formatNumber(value, locale, {
    style: "currency",
    currency,
  });
}
