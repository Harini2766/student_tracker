async function logout() {
  await api("/auth/logout", "POST");
  window.location.href = "/auth/login.html";
}
