/**
 * Gợi ý ngắn đầu trang học viên (cách dùng tab / bước làm).
 */
export default function StudentPageHint({ children }) {
  return (
    <aside
      className="rounded-md border border-emerald-200/90 bg-emerald-50/90 px-3 py-2 text-sm leading-snug text-emerald-950"
      aria-label="Hint"
    >
      {children}
    </aside>
  );
}
