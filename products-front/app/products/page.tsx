"use client";
import React, { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "react-query";
import { Card, CardContent, CardFooter } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Label } from "@/components/ui/label";
import { Loader2, Plus } from "lucide-react";
import { toast } from "sonner";

import { productService } from "@/services/productService";
import { Product, ProductsQueryParams } from "@/types/product";
import ProductsTable from "@/components/products/ProductTable";
import ProductForm from "@/components/products/ProductForm";

export default function ProductsPage() {
  const queryClient = useQueryClient();

  // State management
  const [queryParams, setQueryParams] = useState<ProductsQueryParams>({
    page: 1,
    size: 10,
    sortBy: "name",
    sortDir: "asc",
  });
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [formDialogOpen, setFormDialogOpen] = useState(false);

  // Queries and mutations
  const { data, isLoading, isError } = useQuery({
    queryKey: ["products", queryParams],
    queryFn: () => productService.getProducts(queryParams),
  });

  const createMutation = useMutation({
    mutationFn: productService.createProduct,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["products"] });
      setFormDialogOpen(false);
      toast("Product created", {
        description: "The product has been successfully created.",
      });
    },
    onError: (error: Error) => {
      toast.error("Error", {
        description: `Failed to create product: ${error.message}`,
      });
    },
  });

  const updateMutation = useMutation({
    mutationFn: productService.updateProduct,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["products"] });
      setFormDialogOpen(false);
      setSelectedProduct(null);
      toast("Product updated", {
        description: "The product has been successfully updated.",
      });
    },
    onError: (error: Error) => {
      toast.error("Error", {
        description: `Failed to update product: ${error.message}`,
      });
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => productService.deleteProduct(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["products"] });
      setDeleteDialogOpen(false);
      setSelectedProduct(null);
      toast("Product deleted", {
        description: "The product has been successfully deleted.",
      });
    },
    onError: (error: Error) => {
      toast.error("Error", {
        description: `Failed to delete product: ${error.message}`,
      });
    },
  });

  // Event handlers
  const handleSort = (column: string) => {
    setQueryParams((prev) => ({
      ...prev,
      sortBy: column,
      sortDir:
        prev.sortBy === column && prev.sortDir === "asc" ? "desc" : "asc",
    }));
  };

  const handlePageChange = (newPage: number) => {
    setQueryParams((prev) => ({
      ...prev,
      page: newPage,
    }));
  };

  const handlePageSizeChange = (newSize: string) => {
    setQueryParams((prev) => ({
      ...prev,
      page: 1,
      size: parseInt(newSize, 10),
    }));
  };

  const handleCreateProduct = () => {
    setSelectedProduct(null);
    setFormDialogOpen(true);
  };

  const handleEditProduct = (product: Product) => {
    setSelectedProduct(product);
    setFormDialogOpen(true);
  };

  const handleDeleteProduct = (product: Product) => {
    setSelectedProduct(product);
    setDeleteDialogOpen(true);
  };

  const handleSubmit = (formData: Product) => {
    if (selectedProduct?.id) {
      updateMutation.mutate(formData);
    } else {
      createMutation.mutate(formData);
    }
  };

  const handleConfirmDelete = () => {
    if (selectedProduct?.id) {
      deleteMutation.mutate(selectedProduct.id);
    }
  };

  // Pagination renderer
  const renderPagination = () => {
    if (!data) return null;

    const totalPages = data.totalPages;
    const currentPage = queryParams.page;
    const maxPageButtons = 5;

    let startPage = Math.max(1, currentPage - Math.floor(maxPageButtons / 2));
    let endPage = Math.min(totalPages, startPage + maxPageButtons - 1);

    if (endPage - startPage + 1 < maxPageButtons) {
      startPage = Math.max(1, endPage - maxPageButtons + 1);
    }

    const pageNumbers = [];
    for (let i = startPage; i <= endPage; i++) {
      pageNumbers.push(i);
    }

    return (
      <Pagination>
        <PaginationContent>
          {currentPage > 1 && (
            <PaginationItem>
              <PaginationPrevious
                onClick={() => handlePageChange(currentPage - 1)}
              />
            </PaginationItem>
          )}

          {pageNumbers.map((pageNumber) => (
            <PaginationItem key={pageNumber}>
              <PaginationLink
                isActive={currentPage === pageNumber}
                onClick={() => handlePageChange(pageNumber)}
              >
                {pageNumber}
              </PaginationLink>
            </PaginationItem>
          ))}

          {currentPage < totalPages && totalPages > 0 && (
            <PaginationItem>
              <PaginationNext
                onClick={() => handlePageChange(currentPage + 1)}
              />
            </PaginationItem>
          )}
        </PaginationContent>
      </Pagination>
    );
  };

  const renderContent = () => {
    if (isLoading) {
      return (
        <div className="flex justify-center items-center h-64">
          <Loader2 className="h-8 w-8 animate-spin text-primary" />
        </div>
      );
    }

    if (isError) {
      return (
        <Card className="mt-4">
          <CardContent className="pt-6">
            <div className="text-center text-destructive">
              <p>Failed to load products. Please try again later.</p>
            </div>
          </CardContent>
        </Card>
      );
    }

    if (!data?.content || data.content.length === 0) {
      return (
        <Card className="border-0 rounded-b-none">
          <CardContent className="pt-6">
            <div className="text-center text-muted-foreground">
              <p>
                No products found. Create your first product to get started.
              </p>
            </div>
          </CardContent>
        </Card>
      );
    }

    return (
      <ProductsTable
        products={data.content}
        sortBy={queryParams.sortBy}
        sortDir={queryParams.sortDir}
        onSort={handleSort}
        onEdit={handleEditProduct}
        onDelete={handleDeleteProduct}
      />
    );
  };

  return (
    <>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-4xl font-bold">Products</h1>
        <Button
          onClick={handleCreateProduct}
          className="flex items-center gap-1"
        >
          <Plus className="h-4 w-4" /> Add Product
        </Button>
      </div>

      <div className="flex flex-wrap justify-between items-center gap-4 mb-4">
        <div className="flex items-center gap-2">
          <Label htmlFor="pageSize">Show:</Label>
          <Select
            value={queryParams.size.toString()}
            onValueChange={handlePageSizeChange}
          >
            <SelectTrigger className="w-20">
              <SelectValue placeholder={queryParams.size.toString()} />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="5">5</SelectItem>
              <SelectItem value="10">10</SelectItem>
              <SelectItem value="25">25</SelectItem>
              <SelectItem value="50">50</SelectItem>
            </SelectContent>
          </Select>
          <span className="text-sm text-muted-foreground">
            {data && `Total: ${data.totalElements} products`}
          </span>
        </div>
      </div>

      <Card>
        <CardContent className="p-0 overflow-auto">
          {renderContent()}
        </CardContent>
        {data && data.totalPages > 1 && (
          <CardFooter className="flex justify-center py-4">
            {renderPagination()}
          </CardFooter>
        )}
      </Card>

      <Dialog open={formDialogOpen} onOpenChange={setFormDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>
              {selectedProduct ? "Edit product" : "Create new product"}
            </DialogTitle>
          </DialogHeader>
          <ProductForm
            product={selectedProduct}
            onSubmit={handleSubmit}
            isSubmitting={createMutation.isLoading || updateMutation.isLoading}
          />
        </DialogContent>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Confirm Deletion</DialogTitle>
          </DialogHeader>
          <div className="py-4">
            Are you sure you want to delete {selectedProduct?.name}? This action
            cannot be undone.
          </div>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setDeleteDialogOpen(false)}
            >
              Cancel
            </Button>
            <Button
              variant="destructive"
              onClick={handleConfirmDelete}
              disabled={deleteMutation.isLoading}
            >
              {deleteMutation.isLoading && (
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              )}
              Delete
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
}
