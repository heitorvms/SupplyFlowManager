import axios from "axios"


export interface Product {
  code?: number 
  name: string
  price: number
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}