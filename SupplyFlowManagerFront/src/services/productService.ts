import type { PageResponse, Product } from "@/types/Product";
import axios from "axios";

// const API = "http://localhost:8080/products"

// export default {

//     async list() {
//         const response = await axios.get(API)
//         return response.data
//     },

//     async create(product:any) {
//         const response = await axios.post(API, product)
//         return response.data
//     }

// }


const API_URL = "http://localhost:8080/products"


export async function getProducts(page = 0, size = 10) {
  const response = await axios.get<PageResponse<Product>>(API_URL, {
    params: { page, size }
  })

  return response.data
}

export async function postProduct(product: Product) {
  const response = await axios.post(API_URL, product);

  return response.data
}

export async function putProduct(product: Product) {
  if (!product.code) {
    throw new Error("Product code is required to update.");
  }

  const payload = {
    name: product.name,
    price: product.price,
  };

  const response = await axios.put(`${API_URL}/${product.code}`, payload);

  return response.data
}

export async function deleteProduct(code: number) {
  const response = await axios.delete(`${API_URL}/${code}`);

  return response.data
}
