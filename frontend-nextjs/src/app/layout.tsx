import "./globals.css";
import type { Metadata } from "next";

import AppProviders from "@/components/AppProviders";
import RootChrome from "@/components/RootChrome";

export const metadata: Metadata = {
  title: "German Learning",
  description: "Website học tiếng Đức (A1-B2)"
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="vi" suppressHydrationWarning>
      <body className="min-h-screen antialiased">
        <AppProviders>
          <RootChrome>{children}</RootChrome>
        </AppProviders>
      </body>
    </html>
  );
}
