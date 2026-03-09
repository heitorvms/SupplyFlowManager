import type { UnitOfMeasure } from "@/types/RawMaterial";

export interface ProductCompositionItem {
  id?: number;
  productId: number;
  rawMaterialId: number;
  rawMaterialName: string;
  quantityRequired: number;
  unitOfMeasure: UnitOfMeasure;
}

export interface SaveProductCompositionPayload {
  productId: number;
  rawMaterialId: number;
  quantityRequired: number;
}
