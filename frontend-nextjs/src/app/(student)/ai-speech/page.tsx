"use client";

import { useMemo, useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { analyzeSpeech, SpeechAnalysisResponse } from "@/lib/api";

import { useAuth } from "@/contexts/AuthContext";
import { useLanguage } from "@/contexts/LanguageContext";

const LEVELS = ["A1", "A2", "B1", "B2"] as const;

export default function AiSpeechPage() {
  const { t } = useLanguage();
  const { token } = useAuth();
  const [audio, setAudio] = useState<File | undefined>(undefined);
  const [text, setText] = useState<string>("");
  const [speakingTopicId, setSpeakingTopicId] = useState<number | undefined>(undefined);
  const [level, setLevel] = useState<(typeof LEVELS)[number]>("A1");

  const [localError, setLocalError] = useState<string>("");

  const mutation = useMutation({
    mutationFn: async (): Promise<SpeechAnalysisResponse> => {
      setLocalError("");
      if (!token) throw new Error(t("errors.noJwt"));
      if (!audio && !text.trim()) throw new Error(t("errors.needAudioOrText"));

      return analyzeSpeech(token, {
        audio,
        text: text.trim() ? text.trim() : undefined,
        speakingTopicId,
        level
      });
    }
  });

  const canAnalyze = useMemo(
    () => !!token && !mutation.isPending,
    [token, mutation.isPending]
  );

  return (
    <div className="space-y-4">
      <div className="rounded-md border border-gray-200 bg-white p-4">
        <h1 className="text-lg font-semibold">{t("ai.title")}</h1>
        <div className="mt-3 grid gap-3 sm:grid-cols-2">
          <label className="space-y-1 text-sm">
            <div className="font-medium">{t("ai.audioOptional")}</div>
            <input
              type="file"
              accept="audio/*"
              className="w-full text-sm"
              onChange={(e) => setAudio(e.target.files?.[0])}
            />
          </label>
          <label className="space-y-1 text-sm">
            <div className="font-medium">{t("ai.levelOptional")}</div>
            <select
              className="w-full rounded-md border border-gray-300 p-2 text-sm"
              value={level}
              onChange={(e) => setLevel(e.target.value as (typeof LEVELS)[number])}
            >
              {LEVELS.map((l) => (
                <option key={l} value={l}>
                  {l}
                </option>
              ))}
            </select>
          </label>
        </div>

        <label className="mt-3 block text-sm">
          <div className="font-medium">{t("ai.textOptional")}</div>
          <textarea
            className="mt-1 w-full resize-y rounded-md border border-gray-300 p-2 text-sm"
            rows={5}
            value={text}
            onChange={(e) => setText(e.target.value)}
            placeholder={t("ai.textPlaceholder")}
          />
        </label>

        <label className="mt-3 block text-sm">
          <div className="font-medium">{t("ai.topicIdOptional")}</div>
          <input
            type="number"
            className="mt-1 w-full rounded-md border border-gray-300 p-2 text-sm"
            value={speakingTopicId ?? ""}
            onChange={(e) => {
              const v = e.target.value;
              setSpeakingTopicId(v === "" ? undefined : Number(v));
            }}
            placeholder={t("ai.topicIdPlaceholder")}
          />
        </label>

        <button
          type="button"
          disabled={!canAnalyze}
          className="mt-4 rounded-md bg-gray-900 px-4 py-2 text-sm font-medium text-white disabled:opacity-60"
          onClick={() => mutation.mutate()}
        >
          {mutation.isPending ? t("ai.analyzing") : t("ai.analyze")}
        </button>

        {(mutation.isError || localError) && (
          <div className="mt-3 rounded-md border border-red-200 bg-red-50 p-3 text-sm text-red-800">
            {String((mutation.error as Error)?.message ?? localError)}
          </div>
        )}

        {mutation.data && (
          <div className="mt-4 space-y-3 rounded-md border border-gray-200 bg-gray-50 p-4">
            <div>
              {t("ai.score")}: <span className="font-semibold">{mutation.data.score}</span>/100
            </div>
            <div>
              <div className="font-semibold">{t("ai.feedback")}</div>
              <div className="mt-1 whitespace-pre-wrap text-sm text-gray-800">{mutation.data.feedback}</div>
            </div>
            <div>
              <div className="font-semibold">{t("ai.corrections")}</div>
              <div className="mt-1 whitespace-pre-wrap text-sm text-gray-800">{mutation.data.corrections}</div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

