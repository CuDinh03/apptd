"use client";

import { useMutation } from "@tanstack/react-query";

import { loginRequest, registerRequest } from "@/lib/api";

export function useLoginMutation() {
  return useMutation({
    mutationFn: (vars: { username: string; password: string }) => loginRequest(vars.username, vars.password)
  });
}

export function useRegisterMutation() {
  return useMutation({
    mutationFn: (vars: { username: string; password: string }) => registerRequest(vars.username, vars.password)
  });
}
