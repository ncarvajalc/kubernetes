import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import Link from "next/link";
import { QueryClient } from "react-query";
import { QueryProvider } from "@/providers/QueryProvider";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "Products Dashboard",
  description: "Dashboard for managing products",
};

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 60 * 1000,
      refetchOnWindowFocus: false,
    },
  },
});

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
