import { apiRequest } from "../common/apiClient.js";
import { requireLogin } from "../common/authGuard.js";

const contextPath = window.contextPath || "";
const communityPostId = String(window.communityPostId || "").trim();

document.addEventListener("DOMContentLoaded", async () => {
  if (!requireLogin()) return;
  bindForm();
  if (communityPostId) {
    document.getElementById("communityFormTitle").textContent = "게시글 수정";
    await loadPost();
  }
});

function bindForm() {
  document.getElementById("communityPostForm")?.addEventListener("submit", async (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    const formData = new FormData(form);
    const body = {
      title: String(formData.get("title") || "").trim(),
      content: String(formData.get("content") || "").trim(),
    };
    if (!body.title || !body.content) return;
    const url = communityPostId
      ? `${contextPath}/api/community/posts/${encodeURIComponent(communityPostId)}`
      : `${contextPath}/api/community/posts`;
    const res = await apiRequest(url, {
      method: communityPostId ? "PATCH" : "POST",
      body: JSON.stringify(body),
    });
    if (!res.ok) {
      alert(`게시글을 저장하지 못했습니다. (${res.status})`);
      return;
    }
    const data = await res.json();
    window.location.href = `${contextPath}/community/${encodeURIComponent(data.community_post_id)}`;
  });
}

async function loadPost() {
  const res = await apiRequest(`${contextPath}/api/community/posts/${encodeURIComponent(communityPostId)}`, { method: "GET" });
  if (!res.ok) {
    alert("게시글을 불러오지 못했습니다.");
    window.location.href = `${contextPath}/community`;
    return;
  }
  const post = await res.json();
  if (!post.owner) {
    alert("작성자만 수정할 수 있습니다.");
    window.location.href = `${contextPath}/community/${encodeURIComponent(communityPostId)}`;
    return;
  }
  const form = document.getElementById("communityPostForm");
  if (!form) return;
  form.elements.title.value = post.title || "";
  form.elements.content.value = post.content || "";
}
