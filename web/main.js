// Loads the Pointfix reporter in development only; `vite build` output contains none of it.
if (import.meta.env.DEV) {
  const { mount } = await import('@pointfix/web');
  mount(); // bridge defaults to http://127.0.0.1:4747 (`pointfix start` at the repo root)
}
