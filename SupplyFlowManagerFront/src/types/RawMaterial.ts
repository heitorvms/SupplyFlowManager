export type UnitOfMeasure =
  | "UNIT"
  | "GRAM"
  | "KILOGRAM"
  | "MILLIGRAM"
  | "TON"
  | "MILLILITER"
  | "LITER"
  | "METER"
  | "CENTIMETER"
  | "MILLIMETER"
  | "SQUARE_METER"
  | "CUBIC_METER"
  | "PACKAGE"
  | "BOX"
  | "BAG"
  | "PALLET"

export interface RawMaterial {
  code?: number
  name: string
  stockQuantity: number
  unitOfMeasure: UnitOfMeasure
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}
