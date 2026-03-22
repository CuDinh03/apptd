"use client";

import { useCallback, useEffect, useState } from "react";
import { Volume2 } from "lucide-react";
import { canUseSpeechSynthesis, playGermanAudioUrl, speakGerman } from "@/lib/german-speech";

/**
 * Nút nghe: có audioUrl thì phát file; không thì đọc bằng giọng tiếng Đức (trình duyệt).
 */
export default function GermanListenButton({
  text,
  audioUrl,
  ariaLabel,
  titleUnsupported,
  className = ""
}) {
  const [busy, setBusy] = useState(false);

  // Safari / một số trình duyệt nạp danh sách giọng trễ — warm-up khi mount.
  useEffect(() => {
    if (!canUseSpeechSynthesis()) return;
    const synth = window.speechSynthesis;
    const load = () => synth.getVoices();
    load();
    synth.addEventListener("voiceschanged", load);
    return () => synth.removeEventListener("voiceschanged", load);
  }, []);
  const trimmed = (text ?? "").trim();
  const hasUrl = Boolean((audioUrl ?? "").trim());
  const canTts = canUseSpeechSynthesis();
  const enabled = hasUrl || (canTts && trimmed.length > 0);
  const title = !enabled && titleUnsupported ? titleUnsupported : ariaLabel;

  const onClick = useCallback(() => {
    if (!trimmed && !hasUrl) return;

    setBusy(true);
    const finish = () => setBusy(false);

    if (hasUrl) {
      playGermanAudioUrl(audioUrl.trim())
        .catch(() => {
          if (trimmed && canTts) speakGerman(trimmed);
        })
        .finally(finish);
      return;
    }

    try {
      speakGerman(trimmed);
    } finally {
      // speechSynthesis không có callback kết thúc chuẩn — gỡ busy ngay sau khi enqueue.
      finish();
    }
  }, [audioUrl, hasUrl, trimmed, canTts]);

  return (
    <button
      type="button"
      disabled={!enabled || busy}
      onClick={onClick}
      title={title}
      aria-label={ariaLabel}
      className={`inline-flex shrink-0 items-center justify-center rounded-md border border-gray-300 bg-white p-2 text-gray-800 shadow-sm transition hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50 ${className}`}
    >
      <Volume2 className="h-4 w-4" aria-hidden />
    </button>
  );
}
