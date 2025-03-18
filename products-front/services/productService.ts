import axios from "axios";
import { Product, PagedResponse, ProductsQueryParams } from "@/types/product";

const API_URL = process.env.NEXT_PUBLIC_API_URL;
const PRODUCTS_ENDPOINT = `${API_URL}/products`;

export const productService = {
  async getProducts(
    params: ProductsQueryParams
  ): Promise<PagedResponse<Product>> {
    const response = await axios.get<PagedResponse<Product>>(
      PRODUCTS_ENDPOINT,
      {
        params,
      }
    );
    return response.data;
  },

  async getProductById(id: number): Promise<Product> {
    const response = await axios.get<Product>(`${PRODUCTS_ENDPOINT}/${id}`);
    return response.data;
  },

  async createProduct(product: Product): Promise<Product> {
    const response = await axios.post<Product>(PRODUCTS_ENDPOINT, product);
    return response.data;
  },

  async updateProduct(product: Product): Promise<Product> {
    const response = await axios.put<Product>(
      `${PRODUCTS_ENDPOINT}/${product.id}`,
      product
    );
    return response.data;
  },

  async deleteProduct(id: number): Promise<void> {
    await axios.delete(`${PRODUCTS_ENDPOINT}/${id}`);
  },
};
