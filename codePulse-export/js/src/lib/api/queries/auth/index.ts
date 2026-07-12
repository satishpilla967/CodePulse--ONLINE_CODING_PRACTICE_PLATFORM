import { ApiURL } from "@/lib/api/common/apiURL";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

/**
 * Fetch the user's authentication state from the server.
 */
export const useAuthQuery = () => {
  const apiURL = ApiURL.create("/api/auth/validate", {
    method: "GET",
  });
  const { queryKey } = apiURL;

  return useQuery({
    queryKey,
    queryFn: () => validateAuthentication(apiURL),
  });
};

async function validateAuthentication({
  url,
  method,
  res,
}: ApiURL<"/api/auth/validate", "get">) {
  const response = await fetch(url, {
    method,
  });

  const json = res(await response.json());

  if (json.success) {
    return {
      session: json.payload.session,
      user: json.payload.user,
      isAdmin: json.payload.user.admin,
    };
  }

  return { session: undefined, user: undefined, isAdmin: false };
}

/**
 * Whether the one-time "become admin at sign-up" checkbox should still be shown. Once any admin
 * account exists, this permanently returns false.
 */
export const useAdminBootstrapAvailableQuery = () => {
  const apiURL = ApiURL.create("/api/auth/admin-bootstrap-available", {
    method: "GET",
  });
  const { queryKey } = apiURL;

  return useQuery({
    queryKey,
    queryFn: () => adminBootstrapAvailable(apiURL),
  });
};

async function adminBootstrapAvailable({
  url,
  method,
  res,
}: ApiURL<"/api/auth/admin-bootstrap-available", "get">) {
  const response = await fetch(url, { method });
  const json = res(await response.json());
  return json.success ? json.payload : false;
}

/**
 * Register a new account with email + password. On success, the server sets the session cookie,
 * effectively logging the user in.
 */
export const useRegisterMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: register,
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: ApiURL.prefix("/api/auth") });
    },
  });
};

async function register({
  email,
  password,
  nickname,
  becomeAdmin,
}: {
  email: string;
  password: string;
  nickname?: string;
  becomeAdmin?: boolean;
}) {
  const { url, method, req, res } = ApiURL.create("/api/auth/register", {
    method: "POST",
  });

  const response = await fetch(url, {
    method,
    headers: {
      "Content-Type": "application/json",
    },
    body: req({
      email,
      password,
      nickname,
      becomeAdmin,
    }),
  });

  return res(await response.json());
}

/**
 * Log in with email + password. On success, the server sets the session cookie.
 */
export const useLoginMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: login,
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: ApiURL.prefix("/api/auth") });
    },
  });
};

async function login({ email, password }: { email: string; password: string }) {
  const { url, method, req, res } = ApiURL.create("/api/auth/login", {
    method: "POST",
  });

  const response = await fetch(url, {
    method,
    headers: {
      "Content-Type": "application/json",
    },
    body: req({
      email,
      password,
    }),
  });

  return res(await response.json());
}

/**
 * Request a password reset link. Always resolves with a generic success message, regardless of
 * whether the email is on file, so this cannot be used to enumerate registered accounts. This is
 * also the entrypoint for legacy (pre-email/password) accounts to claim/set a password for the
 * first time.
 */
export const usePasswordResetRequestMutation = () => {
  return useMutation({
    mutationFn: passwordResetRequest,
  });
};

async function passwordResetRequest({ email }: { email: string }) {
  const { url, method, req, res } = ApiURL.create(
    "/api/auth/password-reset/request",
    {
      method: "POST",
    },
  );

  const response = await fetch(url, {
    method,
    headers: {
      "Content-Type": "application/json",
    },
    body: req({
      email,
    }),
  });

  return res(await response.json());
}

/**
 * Confirm a password reset with the token from the emailed link, setting a new password.
 */
export const usePasswordResetConfirmMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: passwordResetConfirm,
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: ApiURL.prefix("/api/auth") });
    },
  });
};

async function passwordResetConfirm({
  token,
  newPassword,
}: {
  token: string;
  newPassword: string;
}) {
  const { url, method, req, res } = ApiURL.create(
    "/api/auth/password-reset/confirm",
    {
      method: "POST",
    },
  );

  const response = await fetch(url, {
    method,
    headers: {
      "Content-Type": "application/json",
    },
    body: req({
      token,
      newPassword,
    }),
  });

  return res(await response.json());
}
