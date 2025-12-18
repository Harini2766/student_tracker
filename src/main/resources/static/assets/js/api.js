const api = async (url, method = "GET", body = null) => {
  const options = {
    method,
    headers: { "Content-Type": "application/json" },
    credentials: "include"
  };

  if (body) options.body = JSON.stringify(body);

  return await fetch(url, options);
};
