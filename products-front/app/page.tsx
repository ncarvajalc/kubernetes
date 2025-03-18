import { Button } from "@/components/ui/button";
import Link from "next/link";

export default function Home() {
  return (
    <div className="flex flex-col items-center justify-center min-h-screen gap-4">
      <h1 className="text-6xl font-bold">Welcome to the products dashboard</h1>
      <Button asChild size="lg">
        <Link href="/products">Go to products</Link>
      </Button>
    </div>
  );
}
