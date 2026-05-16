import { apiRequest } from "../common/apiClient.js";

document.addEventListener("DOMContentLoaded", async () => {
  try {
    const response = await apiRequest("/api/members/me", { method: "GET" });

    if (!response.ok) {
      window.location.href = `${window.contextPath || ""}/main`;
      return;
    }

    const member = await response.json();
    if (member.member_role_enum !== "ADMIN") {
      window.location.href = `${window.contextPath || ""}/main`;
    }
  } catch (error) {
    console.warn("admin guard failed:", error);
    window.location.href = `${window.contextPath || ""}/main`;
  }
});
