import type { Metadata } from "next";
import "./globals.css";
import Link from "next/link";
import { QueryProvider } from "@/providers/QueryProvider";

export const metadata: Metadata = {
  title: "Products Dashboard",
  description: "Dashboard for managing products",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className="font-sans antialiased bg-gray-100 text-gray-900">
        <nav className="bg-white shadow-md p-4 flex justify-center space-x-6">
          <Link
            href="/"
            className="text-lg font-medium hover:text-blue-600 transition"
          >
            Home
          </Link>
          <Link
            href="/products"
            className="text-lg font-medium hover:text-blue-600 transition"
          >
            Products
          </Link>
        </nav>
        <main className="p-6 max-w-4xl m-auto">
          <QueryProvider>{children}</QueryProvider>
        </main>
      </body>
    </html>
  );
}
