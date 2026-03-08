import axios from "axios";
import type { UnitOfMeasure } from "@/types/RawMaterial";

const ENUM_API_URL = "http://localhost:8080/enum";
const UNIT_OF_MEASURE_STORAGE_KEY = "supplyflow:unit-of-measure-enums:v1";
const VALID_UNITS = new Set<string>([
  "UNIT",
  "GRAM",
  "KILOGRAM",
  "MILLIGRAM",
  "TON",
  "MILLILITER",
  "LITER",
  "METER",
  "CENTIMETER",
  "MILLIMETER",
  "SQUARE_METER",
  "CUBIC_METER",
  "PACKAGE",
  "BOX",
  "BAG",
  "PALLET",
]);

type UnitOptionDto = {
  code?: unknown
  name?: unknown
  value?: unknown
  title?: unknown
};

function normalizeUnit(item: unknown): UnitOfMeasure | null {
  if (typeof item === "string") {
    return VALID_UNITS.has(item) ? item as UnitOfMeasure : null;
  }

  if (item && typeof item === "object") {
    const option = item as UnitOptionDto;
    const rawCode = [option.code, option.name, option.value, option.title].find(
      (value) => typeof value === "string",
    );

    if (typeof rawCode === "string" && VALID_UNITS.has(rawCode)) {
      return rawCode as UnitOfMeasure;
    }
  }

  return null;
}

function parseUnitList(value: unknown): UnitOfMeasure[] {
  if (!Array.isArray(value) || value.length === 0) {
    return [];
  }

  const uniqueUnits = new Set<UnitOfMeasure>();

  for (const item of value) {
    const normalized = normalizeUnit(item);
    if (normalized) {
      uniqueUnits.add(normalized);
    }
  }

  return Array.from(uniqueUnits);
}

export async function getUnitOfMeasureEnumsCached(): Promise<UnitOfMeasure[]> {
  const cached = localStorage.getItem(UNIT_OF_MEASURE_STORAGE_KEY);

  if (cached) {
    try {
      const parsed: unknown = JSON.parse(cached);
      const parsedUnits = parseUnitList(parsed);
      if (parsedUnits.length > 0) {
        return parsedUnits;
      }
    } catch {
      localStorage.removeItem(UNIT_OF_MEASURE_STORAGE_KEY);
    }
  }

  const response = await axios.get<unknown>(ENUM_API_URL);
  const enums = parseUnitList(response.data);

  if (enums.length > 0) {
    localStorage.setItem(UNIT_OF_MEASURE_STORAGE_KEY, JSON.stringify(enums));
    return enums;
  }

  throw new Error("Invalid enum response from /enum");
}
