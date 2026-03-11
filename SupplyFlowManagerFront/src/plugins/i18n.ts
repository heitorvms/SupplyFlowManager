import { createI18n } from "vue-i18n";
import en from "@/locales/en";
import pt from "@/locales/pt";
import es from "@/locales/es";

export const LOCALE_STORAGE_KEY = "app-locale";
export const SUPPORTED_LOCALES = ["en", "pt", "es"] as const;

export type AppLocale = (typeof SUPPORTED_LOCALES)[number];

const messages = { en, pt, es };

function resolveInitialLocale(): AppLocale {
  const storedLocale = localStorage.getItem(LOCALE_STORAGE_KEY);

  if (storedLocale && SUPPORTED_LOCALES.includes(storedLocale as AppLocale)) {
    return storedLocale as AppLocale;
  }

  return "en";
}

const initialLocale = resolveInitialLocale();
document.documentElement.lang = initialLocale;

export default createI18n({
  legacy: false,
  globalInjection: true,
  locale: initialLocale,
  fallbackLocale: "en",
  messages,
});
