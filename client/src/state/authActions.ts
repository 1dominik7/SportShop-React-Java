import {
  fetchUserProfileFailure,
  fetchUserProfileRequest,
  fetchUserProfileSuccess,
  forgotPasswordFailure,
  forgotPasswordRequest,
  forgotPasswordSuccess,
  resetPasswordFailure,
  resetPasswordRequest,
  resetPasswordSuccess,
  signInFailure,
  signInRequest,
  signInSuccess,
  signUpFailure,
  signUpRequest,
  signUpSuccess,
  verifyAccountFailure,
  verifyAccountRequest,
  verifyAccountSuccess,
} from "./authSlice";
import { api } from "../config/api";
import { SignInPayload, SignUpPayload } from "../types/userTypes";
import {
  Dispatch,
  ThunkDispatch,
  UnknownAction,
} from "@reduxjs/toolkit";
import { NavigateFunction } from "react-router";
import { toast } from "react-toastify";
import { RootState } from "./store";

export const signUp =
  (payload: SignUpPayload, navigate: NavigateFunction) =>
  async (dispatch: Dispatch) => {
    const { email, firstname, lastname, password } = payload;
    dispatch(signUpRequest());
    try {
      await api.post("/api/v1/auth/register", {
        email,
        firstname,
        lastname,
        password,
      });
      dispatch(signUpSuccess());
      navigate("/verify-account", { state: { email } });
    } catch (error: any) {
      console.log(error.response);
      dispatch(signUpFailure(error.response?.data?.error || "Sign-up failed"));
    }
  };

export const verifyAccount =
  (tokenCode: string, navigate: NavigateFunction) =>
  async (dispatch: ThunkDispatch<RootState, unknown, UnknownAction>) => {
    dispatch(verifyAccountRequest());
    try {
      const res = await api.get(
        `/api/v1/auth/activate-account?token=${tokenCode}`
      );
      const { token } = res.data;
      dispatch(verifyAccountSuccess({ jwt: token }));
      await dispatch(fetchUserProfile(token));
      navigate("/");
    } catch (error: any) {
      dispatch(
        verifyAccountFailure(
          error.response?.data?.error || "Account verification failed."
        )
      );
    }
  };

export const signIn =
  (payload: SignInPayload, navigate: NavigateFunction) =>
  async (dispatch: Dispatch) => {
    const { email, password } = payload;
    dispatch(signInRequest());
    try {
      const res = await api.post("/api/v1/auth/authenticate", {
        email,
        password,
      });
      const { token, user } = res.data;
      dispatch(signInSuccess({ jwt: token, user: user }));
      navigate("/");
    } catch (error: any) {
      if (error.response) {
        console.error("Error Response:", error.response.data);
        if (
          error.response.status === 500 &&
          error.response.data.error ===
            "Account is disabled. A new activation email has been sent."
        ) {
          dispatch(
            signInFailure(
              error.response?.data?.error ||
                "Your account is disabled. Please check your email for a new verification code."
            )
          );
          navigate("/verify-account");
        } else {
          dispatch(
            signInFailure(
              error.response?.data?.error ||
                "Something went wrong, please try again."
            )
          );
        }
      }
    }
  };

export const forgotPassword =
  (email: string, navigate: NavigateFunction) => async (dispatch: Dispatch) => {
    dispatch(forgotPasswordRequest());
    try {
      const res = await api.post("/api/v1/auth/forgot-password", { email });
      const token = res.data.token;
      dispatch(forgotPasswordSuccess());
      navigate(`/reset-password/${token}`, { state: { email } });
    } catch (error: any) {
      console.log(error);
      dispatch(
        forgotPasswordFailure(
          error?.response?.data?.error || "Password reset request failed"
        )
      );
    }
  };

export const resetPassword =
  (
    {
      token,
      newPassword,
      confirmPassword,
    }: { token: string; newPassword: string; confirmPassword: string },
    navigate: NavigateFunction
  ) =>
  async (dispatch: Dispatch) => {
    dispatch(resetPasswordRequest());
    try {
      await api.post("/api/v1/auth/reset-password", {
        token,
        newPassword,
        confirmPassword,
      });
      dispatch(resetPasswordSuccess());
      navigate("/login");
      toast.success("Password reset successfully!");
    } catch (error: any) {
      dispatch(
        resetPasswordFailure(
          error.response?.data?.error || "Password reset failed"
        )
      );
    }
  };

export const fetchUserProfile = (jwt: string) => async (dispatch: Dispatch) => {
  dispatch(fetchUserProfileRequest());
  try {
    const res = await api.get("/api/v1/users/profile", {
      headers: {
        Authorization: `Bearer ${jwt}`,
      },
    });
    const { user } = res.data;
    dispatch(fetchUserProfileSuccess(user));
  } catch (error: any) {
    dispatch(
      fetchUserProfileFailure(
        error.response?.data?.validationErrors || "Fetching profile failed"
      )
    );
  }
};
