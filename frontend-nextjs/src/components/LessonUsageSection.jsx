"use client";

import GermanListenButton from "@/components/GermanListenButton";

/**
 * Cách dùng + câu mẫu (tiếng Việt / Đức) — luôn hiển thị khung giữa từ vựng và bài tập.
 */
export default function LessonUsageSection({ usageNotes, examplePhrases, labels, emptyHint }) {
  const notes = usageNotes ?? [];
  const phrases = examplePhrases ?? [];
  const hasAny = notes.length > 0 || phrases.length > 0;

  return (
    <section
      className="rounded-md border border-emerald-200 border-l-4 border-l-emerald-600 bg-emerald-50/40 p-4"
      aria-label={labels.examplesTitle}
    >
      <p className="text-xs font-medium text-emerald-900">{labels.sectionIntro}</p>

      {!hasAny && (
        <p className="mt-2 text-sm text-gray-700">{emptyHint ?? ""}</p>
      )}

      {notes.length > 0 && (
        <div className="mt-3">
          <h2 className="text-base font-semibold text-gray-900">{labels.usageTitle}</h2>
          <ul className="mt-2 list-disc space-y-1 pl-5 text-sm text-gray-800">
            {notes.map((line, i) => (
              <li key={`u-${i}`}>{line}</li>
            ))}
          </ul>
        </div>
      )}

      {phrases.length > 0 && (
        <div className={notes.length > 0 ? "mt-4" : "mt-3"}>
          <h2 className="text-base font-semibold text-gray-900">{labels.examplesTitle}</h2>
          <ul className="mt-2 space-y-2">
            {phrases.map((row, i) => (
              <li
                key={`e-${i}`}
                className="rounded-md border border-emerald-100 bg-white px-3 py-2 text-sm shadow-sm"
              >
                <div className="flex items-start justify-between gap-2">
                  <div className="min-w-0 flex-1 font-medium leading-snug text-gray-900">{row.german}</div>
                  <GermanListenButton
                    text={row.german}
                    audioUrl={row.audioUrl}
                    ariaLabel={labels.listenGerman ?? "Play German"}
                    titleUnsupported={labels.listenUnsupported}
                  />
                </div>
                <div className="mt-0.5 text-gray-600">{row.vietnamese}</div>
              </li>
            ))}
          </ul>
        </div>
      )}
    </section>
  );
}
