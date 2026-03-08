import type { PageResponse, RawMaterial, UnitOfMeasure } from "@/types/RawMaterial";
import axios from "axios";

const API_URL = "http://localhost:8080/raw-materials";
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

interface RawMaterialApiDto {
  code?: number
  name: string
  quantity?: number
  unitOfMeasure?: UnitOfMeasure | { code?: string; name?: string; value?: string; title?: string }
  quantityInStock?: {
    quantity: number
    unitOfMeasure: UnitOfMeasure | { code?: string; name?: string; value?: string; title?: string }
  }
}

function normalizeUnitOfMeasure(
  value: RawMaterialApiDto["unitOfMeasure"],
): UnitOfMeasure | null {
  if (typeof value === "string") {
    return VALID_UNITS.has(value) ? value as UnitOfMeasure : null;
  }

  if (value && typeof value === "object") {
    const rawCode = [value.code, value.name, value.value, value.title].find(
      (item) => typeof item === "string",
    );

    if (typeof rawCode === "string" && VALID_UNITS.has(rawCode)) {
      return rawCode as UnitOfMeasure;
    }
  }

  return null;
}

function toDomain(dto: RawMaterialApiDto): RawMaterial {
  const normalizedNestedUnit = normalizeUnitOfMeasure(dto.quantityInStock?.unitOfMeasure);
  const normalizedRootUnit = normalizeUnitOfMeasure(dto.unitOfMeasure);

  return {
    code: dto.code,
    name: dto.name,
    stockQuantity: dto.quantityInStock?.quantity ?? dto.quantity ?? 0,
    unitOfMeasure: normalizedNestedUnit ?? normalizedRootUnit ?? "UNIT",
  };
}

function toPayload(rawMaterial: RawMaterial) {
  return {
    name: rawMaterial.name,
    quantity: rawMaterial.stockQuantity,
    unitOfMeasure: rawMaterial.unitOfMeasure,
  };
}

export async function getRawMaterials(page = 0, size = 10) {
  const response = await axios.get<PageResponse<RawMaterialApiDto>>(API_URL, {
    params: { page, size },
  });

  return {
    ...response.data,
    content: response.data.content.map(toDomain),
  };
}

export async function postRawMaterial(rawMaterial: RawMaterial) {
  const payload = toPayload(rawMaterial);

  const response = await axios.post<RawMaterialApiDto>(API_URL, payload);

  return toDomain(response.data);
}

export async function putRawMaterial(rawMaterial: RawMaterial) {
  if (!rawMaterial.code) {
    throw new Error("Raw material code is required to update.");
  }

  const payload = toPayload(rawMaterial);

  const response = await axios.put<RawMaterialApiDto>(
    `${API_URL}/${rawMaterial.code}`,
    payload,
  );

  return toDomain(response.data);
}

export async function deleteRawMaterial(code: number) {
  const response = await axios.delete(`${API_URL}/${code}`);

  return response.data;
}
