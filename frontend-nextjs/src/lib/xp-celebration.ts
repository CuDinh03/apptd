import confetti from "canvas-confetti";

export type CelebrateXpOptions = {
  /** Có bonus XP (ví dụ 100% lần đầu) → hiệu ứng mạnh hơn */
  highImpact?: boolean;
};

/** Pháo giấy khi nhận XP (theo .cursorrules — feedback tích cực). */
export function celebrateXp(options?: CelebrateXpOptions): void {
  if (typeof window === "undefined") return;
  const base = options?.highImpact ? 110 : 65;
  void confetti({
    particleCount: base,
    spread: 72,
    startVelocity: options?.highImpact ? 38 : 28,
    origin: { y: 0.62 },
    ticks: 140,
    zIndex: 9999,
    scalar: options?.highImpact ? 1.05 : 1
  });
}
