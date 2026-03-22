/**
 * Phát âm / file âm thanh cho từ và câu tiếng Đức (luyện nghe).
 * Dùng Web Speech API khi không có URL; dừng bản đang phát khi bắt đầu bản mới.
 */

let lastHtmlAudio: HTMLAudioElement | null = null;

export function stopLessonPlayback(): void {
  if (typeof window === "undefined") return;
  window.speechSynthesis?.cancel();
  if (lastHtmlAudio) {
    lastHtmlAudio.pause();
    lastHtmlAudio.currentTime = 0;
    lastHtmlAudio = null;
  }
}

export function canUseSpeechSynthesis(): boolean {
  return typeof window !== "undefined" && typeof window.speechSynthesis !== "undefined";
}

/**
 * Ưu tiên giọng de-* nếu trình duyệt đã nạp danh sách voices.
 */
function pickGermanVoice(): SpeechSynthesisVoice | null {
  const voices = window.speechSynthesis?.getVoices() ?? [];
  const exact = voices.find((v) => v.lang === "de-DE" || v.lang === "de_DE");
  if (exact) return exact;
  return voices.find((v) => v.lang.toLowerCase().startsWith("de")) ?? null;
}

export function speakGerman(text: string): void {
  if (typeof window === "undefined") return;
  const trimmed = text.trim();
  if (!trimmed) return;

  const synth = window.speechSynthesis;
  if (!synth) return;

  stopLessonPlayback();

  const utter = new SpeechSynthesisUtterance(trimmed);
  utter.lang = "de-DE";
  utter.rate = 0.9;
  const voice = pickGermanVoice();
  if (voice) utter.voice = voice;
  synth.speak(utter);
}

/** Phát file MP3/OGG…; reject nếu play() thất bại (để UI fallback TTS). */
export function playGermanAudioUrl(url: string): Promise<void> {
  if (typeof window === "undefined") return Promise.resolve();

  stopLessonPlayback();

  const a = new Audio(url);
  lastHtmlAudio = a;

  const done = new Promise<void>((resolve, reject) => {
    a.onended = () => {
      if (lastHtmlAudio === a) lastHtmlAudio = null;
      resolve();
    };
    a.onerror = () => {
      if (lastHtmlAudio === a) lastHtmlAudio = null;
      reject(new Error("AUDIO_PLAY_FAILED"));
    };
  });

  return a.play().then(() => done).catch((e) => {
    if (lastHtmlAudio === a) lastHtmlAudio = null;
    return Promise.reject(e);
  });
}
