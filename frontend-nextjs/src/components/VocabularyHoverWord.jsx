"use client";

/**
 * Hàng từ vựng: hover / focus để xem IPA và ghi chú cách dùng (dữ liệu từ API).
 * File .jsx để tách logic tooltip khỏi page.tsx.
 */
export default function VocabularyHoverWord({
  articleSlot,
  word,
  meaningVi,
  pronunciationIpa,
  usageNoteVi,
  labels
}) {
  const ipa = (pronunciationIpa ?? "").trim();
  const usage = (usageNoteVi ?? "").trim();
  const hasExtra = Boolean(ipa || usage);

  const row = (
    <div className="flex flex-wrap items-center gap-2">
      {articleSlot}
      <span
        className={
          hasExtra
            ? "font-semibold border-b border-dotted border-gray-500 decoration-gray-400"
            : "font-semibold"
        }
      >
        {word}
      </span>
    </div>
  );

  if (!hasExtra) {
    return row;
  }

  return (
    <div
      className="group relative w-fit max-w-full cursor-help rounded outline-none focus-visible:ring-2 focus-visible:ring-gray-500"
      tabIndex={0}
    >
      {row}
      {labels?.hoverHint && (
        <p className="mt-0.5 text-xs text-gray-500">{labels.hoverHint}</p>
      )}
      <div
        className="pointer-events-none invisible absolute bottom-full left-0 z-50 mb-1 w-[min(100vw-2rem,22rem)] rounded-lg border border-gray-200 bg-white p-3 text-left text-sm text-gray-800 shadow-lg ring-1 ring-black/5 opacity-0 transition-opacity duration-150 group-hover:visible group-hover:opacity-100 group-focus-visible:visible group-focus-visible:opacity-100"
        role="tooltip"
      >
        <div className="space-y-2">
          {meaningVi && (
            <div>
              <div className="text-xs font-semibold uppercase tracking-wide text-gray-500">
                {labels?.meaning ?? "Meaning"}
              </div>
              <div className="text-gray-900">{meaningVi}</div>
            </div>
          )}
          {ipa && (
            <div>
              <div className="text-xs font-semibold uppercase tracking-wide text-gray-500">
                {labels?.pronunciation ?? "IPA"}
              </div>
              <div className="font-mono text-gray-900">{ipa}</div>
            </div>
          )}
          {usage && (
            <div>
              <div className="text-xs font-semibold uppercase tracking-wide text-gray-500">
                {labels?.usage ?? "Usage"}
              </div>
              <div className="leading-snug text-gray-800">{usage}</div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
