import React from "react";
import {
  Table,
  TableBody,
  TableCaption,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import { Pencil, Trash2 } from "lucide-react";
import { Product } from "@/types/product";

interface ProductsTableProps {
  products: Product[];
  sortBy: string;
  sortDir: string;
  onSort: (column: string) => void;
  onEdit: (product: Product) => void;
  onDelete: (product: Product) => void;
}

export default function ProductsTable({
  products,
  sortBy,
  sortDir,
  onSort,
  onEdit,
  onDelete,
}: ProductsTableProps) {
  const getSortIndicator = (column: string) => {
    if (sortBy === column) {
      return sortDir === "asc" ? " ↑" : " ↓";
    }
    return "";
  };

  const getHeaderClass = (column: string) => {
    return sortBy === column ? "cursor-pointer bg-muted/50" : "cursor-pointer";
  };

  return (
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead
            className={getHeaderClass("id")}
            onClick={() => onSort("id")}
          >
            ID{getSortIndicator("id")}
          </TableHead>
          <TableHead
            className={getHeaderClass("name")}
            onClick={() => onSort("name")}
          >
            Name{getSortIndicator("name")}
          </TableHead>
          <TableHead className="text-left">Description</TableHead>
          <TableHead
            className={getHeaderClass("price")}
            onClick={() => onSort("price")}
          >
            Price{getSortIndicator("price")}
          </TableHead>
          <TableHead className="text-right">Actions</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {products.map((product) => (
          <TableRow key={product.id}>
            <TableCell>{product.id}</TableCell>
            <TableCell className="font-medium">{product.name}</TableCell>
            <TableCell>
              {product.description.length > 10
                ? `${product.description.slice(0, 10)}...`
                : product.description}
            </TableCell>
            <TableCell>${product.price}</TableCell>

            <TableCell className="text-right">
              <div className="flex justify-end gap-2">
                <Button
                  variant="outline"
                  size="icon"
                  onClick={() => onEdit(product)}
                >
                  <Pencil className="h-4 w-4" />
                </Button>
                <Button
                  variant="outline"
                  size="icon"
                  className="text-destructive"
                  onClick={() => onDelete(product)}
                >
                  <Trash2 className="h-4 w-4" />
                </Button>
              </div>
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
}
