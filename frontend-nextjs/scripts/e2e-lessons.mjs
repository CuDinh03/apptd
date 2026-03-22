#!/usr/bin/env node
/**
 * Gọi API giống FE (`src/lib/api.ts` → fetchLessons) — BE trả ApiResponse<T>.
 */

const API_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api/v1";
const level = process.env.E2E_LEVEL ?? "A1";

const url = new URL(`${API_URL}/lessons`);
url.searchParams.set("level", level);

const res = await fetch(url.toString(), { method: "GET" });
const text = await res.text();

console.log("URL:", url.toString());
console.log("HTTP:", res.status, res.statusText);

let body;
try {
  body = JSON.parse(text);
} catch {
  console.error("Body (raw):", text.slice(0, 500));
  process.exit(1);
}

if (typeof body.success !== "boolean") {
  console.error("Kỳ vọng ApiResponse có field success");
  process.exit(1);
}

if (!body.success) {
  console.error("API lỗi:", body.errorCode, body.message);
  process.exit(1);
}

if (!Array.isArray(body.data)) {
  console.error("Kỳ vọng data là mảng bài học");
  process.exit(1);
}

console.log("OK: ApiResponse success, số bài học:", body.data.length);
if (body.data.length > 0) {
  console.log("Mẫu:", JSON.stringify(body.data[0], null, 2));
}
process.exit(res.ok ? 0 : 1);
