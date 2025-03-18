import React, { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { DialogFooter, DialogClose } from "@/components/ui/dialog";
import { Loader2 } from "lucide-react";
import { Product } from "@/types/product";

interface ProductFormProps {
  product?: Product | null;
  onSubmit: (product: Product) => void;
  isSubmitting: boolean;
}

export default function ProductForm({
  product,
  onSubmit,
  isSubmitting,
}: ProductFormProps) {
  const [formData, setFormData] = useState<Product>({
    name: "",
    description: "",
    price: 0,
  });

  useEffect(() => {
    if (product) {
      setFormData(product);
    } else {
      setFormData({
        name: "",
        description: "",
        price: 0,
      });
    }
  }, [product]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value, type } = e.target;

    if (type === "number") {
      setFormData((prev) => ({
        ...prev,
        [name]: value === "" ? "" : parseFloat(value),
      }));
    } else {
      setFormData((prev) => ({ ...prev, [name]: value }));
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit({
      ...formData,
      ...(product?.id && { id: product.id }),
    });
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="grid gap-2">
        <Label htmlFor="name">Name</Label>
        <Input
          id="name"
          name="name"
          value={formData.name}
          onChange={handleChange}
          required
        />
      </div>

      <div className="grid gap-2">
        <Label htmlFor="description">Description</Label>
        <Input
          id="description"
          name="description"
          value={formData.description}
          onChange={handleChange}
          required
        />
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div className="grid gap-2">
          <Label htmlFor="price">Price</Label>
          <Input
            id="price"
            name="price"
            type="number"
            step="0.01"
            min="0"
            value={formData.price}
            onChange={handleChange}
            onFocus={(e) => e.target.select()}
            required
          />
        </div>
      </div>

      <DialogFooter>
        <DialogClose asChild>
          <Button variant="outline" type="button">
            Cancel
          </Button>
        </DialogClose>
        <Button type="submit" disabled={isSubmitting}>
          {isSubmitting && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
          {product?.id ? "Update" : "Create"}
        </Button>
      </DialogFooter>
    </form>
  );
}
