function toggleTheme() {
  document.body.classList.toggle("light");
  
  // Save preference
  const mode = document.body.classList.contains("light") ? "light" : "dark";
  localStorage.setItem("theme", mode);
}

// Load saved theme on page load
window.onload = () => {
  const saved = localStorage.getItem("theme");
  if (saved === "light") document.body.classList.add("light");
};
