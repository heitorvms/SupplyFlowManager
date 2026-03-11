import axios from "axios";

type TranslateFn = (key: string, params?: Record<string, unknown>) => string;

type ApiErrorOptions = {
  defaultKey: string;
};

function hasTranslation(t: TranslateFn, key: string): boolean {
  return t(key) !== key;
}

function normalizeCode(value: string): string {
  return value
    .trim()
    .replace(/[^a-zA-Z0-9]+/g, "_")
    .replace(/([a-z0-9])([A-Z])/g, "$1_$2")
    .toLowerCase();
}

function extractBackendCode(data: unknown): string | null {
  if (!data || typeof data !== "object") {
    return null;
  }

  const source = data as Record<string, unknown>;
  const rawCode = source.code ?? source.errorCode ?? source.error ?? source.type;

  return typeof rawCode === "string" && rawCode.trim() ? normalizeCode(rawCode) : null;
}

function extractBackendMessage(data: unknown): string | null {
  if (typeof data === "string" && data.trim()) {
    return data;
  }

  if (!data || typeof data !== "object") {
    return null;
  }

  const source = data as Record<string, unknown>;
  const rawMessage = source.message ?? source.detail ?? source.error_description;

  return typeof rawMessage === "string" && rawMessage.trim() ? rawMessage : null;
}

function getStatusFallbackKey(status: number): string {
  if (status === 400) return "apiErrors.badRequest";
  if (status === 401) return "apiErrors.unauthorized";
  if (status === 403) return "apiErrors.forbidden";
  if (status === 404) return "apiErrors.notFound";
  if (status === 409) return "apiErrors.conflict";
  if (status === 422) return "apiErrors.validation";
  if (status === 429) return "apiErrors.tooManyRequests";
  if (status >= 500) return "apiErrors.server";

  return "apiErrors.unknown";
}

export function resolveApiErrorMessage(
  error: unknown,
  t: TranslateFn,
  options: ApiErrorOptions,
): string {
  if (!axios.isAxiosError(error)) {
    return t(options.defaultKey);
  }

  if (error.code === "ECONNABORTED") {
    return t("apiErrors.timeout");
  }

  if (!error.response) {
    return t("apiErrors.network");
  }

  const backendCode = extractBackendCode(error.response.data);
  if (backendCode) {
    const codeTranslationKey = `apiErrors.codes.${backendCode}`;
    if (hasTranslation(t, codeTranslationKey)) {
      return t(codeTranslationKey);
    }
  }

  const statusKey = getStatusFallbackKey(error.response.status);
  if (hasTranslation(t, statusKey)) {
    return t(statusKey);
  }

  const backendMessage = extractBackendMessage(error.response.data);
  if (backendMessage) {
    return backendMessage;
  }

  return t(options.defaultKey);
}
