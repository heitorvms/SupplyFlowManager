import { computed } from "vue";
import { useI18n } from "vue-i18n";
import {
  LOCALE_STORAGE_KEY,
  SUPPORTED_LOCALES,
  type AppLocale,
} from "@/plugins/i18n";

type LanguageOption = {
  code: AppLocale;
  flag: string;
  labelKey: string;
};

const languageOptions: LanguageOption[] = [
  { code: "en", flag: "🇺🇸", labelKey: "languages.en" },
  { code: "pt", flag: "🇧🇷", labelKey: "languages.pt" },
  { code: "es", flag: "🇪🇸", labelKey: "languages.es" },
];

export function useLanguageSwitcher() {
  const { locale } = useI18n();

  const currentLocale = computed(() => locale.value as AppLocale);

  function setLocale(newLocale: AppLocale) {
    if (!SUPPORTED_LOCALES.includes(newLocale)) {
      return;
    }

    locale.value = newLocale;
    localStorage.setItem(LOCALE_STORAGE_KEY, newLocale);
    document.documentElement.lang = newLocale;
  }

  return {
    languageOptions,
    currentLocale,
    setLocale,
  };
}
