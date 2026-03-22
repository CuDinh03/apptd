/** Khóa TanStack Query thống nhất — dễ invalidate theo nhóm. */
export const queryKeys = {
  lessons: (level?: string) => ["lessons", level ?? "all"] as const,
  lessonContent: (id: number) => ["lessonContent", id] as const,
  /** userId tránh lẫn cache khi đổi tài khoản; không đưa JWT vào queryKey. */
  myProgress: (userId: number | null) => ["myProgress", userId ?? "guest"] as const,
  leaderboard: (limit: number) => ["leaderboard", limit] as const
};
