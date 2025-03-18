export interface Product {
  id?: number;
  name: string;
  description: string;
  price: number;
}

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface ProductsQueryParams {
  page: number;
  size: number;
  sortBy: string;
  sortDir: string;
}
