import axios from "axios";
import type { ProductionOptimizationItem } from "@/types/ProductionOptimization";

const API_BASE_URL = "http://localhost:8080/production";

type ProductionOptimizationDto = {
  productName?: unknown
  units?: unknown
  totalValue?: unknown
  quantity?: unknown
  producibleUnits?: unknown
  maxUnits?: unknown
  value?: unknown
};

function toNumber(value: unknown): number {
  if (typeof value === "number") {
    return Number.isFinite(value) ? value : 0;
  }

  if (typeof value === "string") {
    const normalized = value.trim().replace(",", ".");
    const parsed = Number(normalized);
    return Number.isFinite(parsed) ? parsed : 0;
  }

  return 0;
}

function toDomain(item: ProductionOptimizationDto): ProductionOptimizationItem {
  return {
    productName: typeof item.productName === "string" ? item.productName : "Produto",
    units: toNumber(item.units ?? item.quantity ?? item.producibleUnits ?? item.maxUnits),
    totalValue: toNumber(item.totalValue ?? item.value),
  };
}

export async function getProductionOptimization() {
  const response = await axios.get<ProductionOptimizationDto[]>(
    `${API_BASE_URL}/optimize`,
  );

  return response.data.map(toDomain);
}
