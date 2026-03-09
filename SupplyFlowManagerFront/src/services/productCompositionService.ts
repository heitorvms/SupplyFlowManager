import axios from "axios";
import type {
  ProductCompositionItem,
  SaveProductCompositionPayload,
} from "@/types/ProductComposition";
import type { UnitOfMeasure } from "@/types/RawMaterial";

const API_BASE_URLS = [
  "http://localhost:8080/product-compositions",
  "http://localhost:8080/product-composition",
];
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
const UNIT_ALIASES: Record<string, UnitOfMeasure> = {
  UN: "UNIT",
  U: "UNIT",
  G: "GRAM",
  KG: "KILOGRAM",
  MG: "MILLIGRAM",
  T: "TON",
  ML: "MILLILITER",
  M: "METER",
  CM: "CENTIMETER",
  MM: "MILLIMETER",
  M2: "SQUARE_METER",
  M3: "CUBIC_METER",
  CX: "BOX",
};

type ProductCompositionDto = {
  id?: number;
  code?: number;
  productId?: number | null;
  rawMaterialId?: number | null;
  rawMaterialName?: string;
  quantityRequired?: number;
  unitOfMeasure?: unknown;
  product?: {
    id?: number | null
    code?: number | null
  } | null
  rawMaterial?: {
    id?: number | null
    code?: number | null
    name?: string | null
    unitOfMeasure?: unknown
  } | null
};

function normalizeUnitOfMeasure(value: unknown): UnitOfMeasure {
  if (typeof value === "string" && VALID_UNITS.has(value)) {
    return value as UnitOfMeasure;
  }

  if (typeof value === "string") {
    const normalized = value.trim().toUpperCase();
    const aliasMatch = UNIT_ALIASES[normalized];
    if (aliasMatch) {
      return aliasMatch;
    }
  }

  if (value && typeof value === "object") {
    const unitValue = value as {
      code?: unknown
      name?: unknown
      value?: unknown
      title?: unknown
    };

    const rawCode = [unitValue.code, unitValue.name, unitValue.value, unitValue.title].find(
      (item) => typeof item === "string",
    );

    if (typeof rawCode === "string") {
      const normalized = rawCode.trim().toUpperCase();

      if (VALID_UNITS.has(normalized)) {
        return normalized as UnitOfMeasure;
      }

      const aliasMatch = UNIT_ALIASES[normalized];
      if (aliasMatch) {
        return aliasMatch;
      }
    }
  }

  return "UNIT";
}

function toDomain(dto: ProductCompositionDto): ProductCompositionItem {
  const nestedProductId = dto.product?.id ?? dto.product?.code ?? null;
  const nestedRawMaterialId = dto.rawMaterial?.id ?? dto.rawMaterial?.code ?? null;
  const nestedRawMaterialName = dto.rawMaterial?.name ?? null;
  const nestedUnit = dto.rawMaterial?.unitOfMeasure;

  return {
    id: dto.id ?? dto.code,
    productId: dto.productId ?? nestedProductId ?? 0,
    rawMaterialId: dto.rawMaterialId ?? nestedRawMaterialId ?? 0,
    rawMaterialName: dto.rawMaterialName ?? nestedRawMaterialName ?? "Raw Material",
    quantityRequired: dto.quantityRequired ?? 0,
    unitOfMeasure: normalizeUnitOfMeasure(dto.unitOfMeasure ?? nestedUnit),
  };
}

async function tryAcrossBaseUrls<T>(
  request: (baseUrl: string) => Promise<T>,
): Promise<T> {
  let lastError: unknown;

  for (const baseUrl of API_BASE_URLS) {
    try {
      return await request(baseUrl);
    } catch (error) {
      lastError = error;
    }
  }

  throw lastError;
}

export async function getProductComposition(productId: number) {
  const response = await tryAcrossBaseUrls((baseUrl) =>
    axios.get<ProductCompositionDto[]>(
      `${baseUrl}/product/${productId}`,
    ),
  );

  return response.data.map(toDomain);
}

export async function postProductComposition(payload: SaveProductCompositionPayload) {
  const response = await tryAcrossBaseUrls((baseUrl) =>
    axios.post<ProductCompositionDto>(baseUrl, payload),
  );

  return toDomain(response.data);
}

export async function deleteProductComposition(compositionId: number) {
  const response = await tryAcrossBaseUrls((baseUrl) =>
    axios.delete(`${baseUrl}/${compositionId}`),
  );

  return response.data;
}
