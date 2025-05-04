import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { User } from "../types/userTypes";

export interface AuthState {
  jwt: string | null;
  user: User | null;
  isLoggedIn: boolean;
  sentCode: boolean;
  loading: boolean;
  error: Record<string, string> | null;
  isVerified: boolean;
  resetPasswordEmailSent: boolean;
}

const initialState: AuthState = {
  jwt: localStorage.getItem("jwt") || null,
  user: localStorage.getItem("user")
    ? JSON.parse(localStorage.getItem("user")!)
    : null,
  isLoggedIn: !!localStorage.getItem("jwt"),
  sentCode: false,
  loading: false,
  error: null,
  isVerified: false,
  resetPasswordEmailSent: false,
};

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    signUpRequest(state) {
      state.loading = true;
      state.error = null;
    },
    signUpSuccess(state) {
      state.loading = false;
    },
    signUpFailure(state, action: PayloadAction<Record<string, string>>) {
      state.loading = false;
      state.error = action.payload;
    },
    sendRegisterCodeRequest(state) {
      state.loading = true;
      state.error = null;
    },
    sendRegisterCodeSuccess(state) {
      state.loading = false;
      state.sentCode = true;
    },
    sendRegisterCodeFailure(
      state,
      action: PayloadAction<Record<string, string>>
    ) {
      state.loading = false;
      state.error = action.payload;
    },
    verifyAccountRequest(state) {
      state.loading = true;
      state.error = null;
    },
    verifyAccountSuccess(state, action: PayloadAction<{ jwt: string, user?:User }>) {
      state.loading = false;
      state.isVerified = true;
      state.isLoggedIn = true;
      state.jwt = action.payload.jwt;
      if (action.payload.user) {
        state.user = action.payload.user;
        localStorage.setItem('user', JSON.stringify(action.payload.user));
      }
      localStorage.setItem("jwt", action.payload.jwt);
    },
    verifyAccountFailure(state, action: PayloadAction<Record<string, string>>) {
      state.loading = false;
      state.error = action.payload;
    },
    signInRequest(state) {
      state.loading = true;
      state.error = null;
    },
    signInSuccess(state, action: PayloadAction<{ jwt: string; user: any }>) {
      state.jwt = action.payload.jwt;
      state.user = action.payload.user;
      state.isLoggedIn = true;
      state.loading = false;
      localStorage.setItem("jwt", action.payload.jwt);
      localStorage.setItem("user", JSON.stringify(action.payload.user));
    },
    signInFailure(state, action: PayloadAction<Record<string, string>>) {
      state.loading = false;
      state.error = action.payload;
    },
    fetchUserProfileRequest(state) {
      state.loading = true;
      state.error = null;
    },
    fetchUserProfileSuccess(state, action: PayloadAction<User>) {
      state.user = action.payload;
      state.loading = false;
      state.isLoggedIn = true;
    },
    fetchUserProfileFailure(
      state,
      action: PayloadAction<Record<string, string>>
    ) {
      state.loading = false;
      state.error = action.payload;
    },
    signOut(state) {
      state.jwt = null;
      state.user = null;
      state.isLoggedIn = false;
      localStorage.removeItem("jwt");
      localStorage.removeItem("user");
    },
    clearError(state) {
      state.error = null;
    },
    forgotPasswordRequest(state) {
      state.loading = true;
      state.error = null;
      state.resetPasswordEmailSent = false;
    },
    forgotPasswordSuccess(state) {
      state.loading = false;
      state.resetPasswordEmailSent = true;
    },
    forgotPasswordFailure(
      state,
      action: PayloadAction<Record<string, string>>
    ) {
      state.loading = false;
      state.error = action.payload;
    },
    resetPasswordRequest(state) {
      state.loading = true;
      state.error = null;
    },
    resetPasswordSuccess(state) {
      state.loading = false;
    },
    resetPasswordFailure(state, action: PayloadAction<Record<string, string>>) {
      state.loading = false;
      state.error = action.payload;
    },
  },
});

export const {
  signUpRequest,
  signUpSuccess,
  signUpFailure,
  sendRegisterCodeRequest,
  sendRegisterCodeSuccess,
  sendRegisterCodeFailure,
  verifyAccountRequest,
  verifyAccountSuccess,
  verifyAccountFailure,
  signInRequest,
  signInSuccess,
  signInFailure,
  fetchUserProfileRequest,
  fetchUserProfileSuccess,
  fetchUserProfileFailure,
  signOut,
  clearError,
  forgotPasswordRequest,
  forgotPasswordSuccess,
  forgotPasswordFailure,
  resetPasswordRequest,
  resetPasswordSuccess,
  resetPasswordFailure
} = authSlice.actions;

export default authSlice.reducer;
