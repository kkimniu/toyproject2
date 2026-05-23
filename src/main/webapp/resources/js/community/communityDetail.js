import { apiRequest } from "../common/apiClient.js";
import { requireLogin } from "../common/authGuard.js";

const contextPath = window.contextPath || "";
const communityPostId = String(window.communityPostId || "").trim();
let currentPost = null;

document.addEventListener("DOMContentLoaded", async () => {
  bindActions();
  await loadPost();
  await loadComments();
});

function bindActions() {
  document.getElementById("btnReportCommunityPost")?.addEventListener("click", async () => {
    if (!requireLogin() || !currentPost) return;
    const reason = prompt("신고 사유를 입력하세요.");
    if (!reason || !reason.trim()) return;
    const res = await apiRequest(`${contextPath}/api/reports/community-posts/${encodeURIComponent(communityPostId)}`, {
      method: "POST",
      body: JSON.stringify({ reason: reason.trim() }),
    });
    if (!res.ok) {
      alert(`게시글을 신고하지 못했습니다. (${res.status})`);
      return;
    }
    alert("신고가 접수되었습니다.");
  });

  document.getElementById("btnDeleteCommunityPost")?.addEventListener("click", async () => {
    if (!currentPost || !confirm("게시글을 삭제하시겠습니까?")) return;
    const res = await apiRequest(`${contextPath}/api/community/posts/${encodeURIComponent(communityPostId)}`, {
      method: "DELETE",
    });
    if (!res.ok) {
      alert(`게시글을 삭제하지 못했습니다. (${res.status})`);
      return;
    }
    window.location.href = `${contextPath}/community`;
  });

  document.getElementById("communityCommentForm")?.addEventListener("submit", async (event) => {
    event.preventDefault();
    if (!requireLogin()) return;
    const form = event.currentTarget;
    const formData = new FormData(form);
    const content = String(formData.get("content") || "").trim();
    if (!content) return;
    const res = await apiRequest(`${contextPath}/api/community/posts/${encodeURIComponent(communityPostId)}/comments`, {
      method: "POST",
      body: JSON.stringify({ content }),
    });
    if (!res.ok) {
      alert(`댓글을 등록하지 못했습니다. (${res.status})`);
      return;
    }
    form.reset();
    await loadComments();
  });
}

async function loadPost() {
  const container = document.getElementById("communityPostDetail");
  if (!container || !communityPostId) return;
  const res = await apiRequest(`${contextPath}/api/community/posts/${encodeURIComponent(communityPostId)}?count_view=true`, { method: "GET" });
  if (!res.ok) {
    container.innerHTML = '<p class="community-empty">게시글을 불러오지 못했습니다.</p>';
    return;
  }
  currentPost = await res.json();
  container.innerHTML = renderPost(currentPost);
  renderOwnerActions(currentPost);
}

async function loadComments() {
  const list = document.getElementById("communityCommentList");
  const count = document.getElementById("communityCommentCount");
  if (!list || !communityPostId) return;
  const res = await apiRequest(`${contextPath}/api/community/posts/${encodeURIComponent(communityPostId)}/comments`, { method: "GET" });
  if (!res.ok) {
    list.innerHTML = '<p class="community-empty">댓글을 불러오지 못했습니다.</p>';
    if (count) count.textContent = "0개";
    return;
  }
  const comments = await res.json();
  if (count) count.textContent = `${comments.length.toLocaleString("ko-KR")}개`;
  if (!Array.isArray(comments) || comments.length === 0) {
    list.innerHTML = '<p class="community-empty">등록된 댓글이 없습니다.</p>';
    return;
  }
  list.innerHTML = renderComments(comments);
  bindCommentActions(list);
}

function renderPost(post) {
  return `
    <h1>${escapeHtml(post.title)}</h1>
    <div class="community-post-meta">
      <span>${escapeHtml(post.member_name || "알 수 없는 회원")}</span>
      <span>${escapeHtml(formatDate(post.created_at))}</span>
      <span>조회 ${Number(post.views || 0).toLocaleString("ko-KR")}</span>
    </div>
    <div class="community-detail-content">${escapeHtml(post.content || "")}</div>
  `;
}

function renderOwnerActions(post) {
  const edit = document.getElementById("btnEditCommunityPost");
  const remove = document.getElementById("btnDeleteCommunityPost");
  const report = document.getElementById("btnReportCommunityPost");
  if (report && !post.owner) {
    report.style.display = "inline-flex";
  }
  if (!edit || !remove || !post.owner) return;
  edit.href = `${contextPath}/community/${encodeURIComponent(post.community_post_id)}/edit`;
  edit.style.display = "inline-flex";
  remove.style.display = "inline-flex";
}

function renderComments(comments) {
  const repliesByParent = new Map();
  comments.filter((comment) => comment.parent_comment_id).forEach((reply) => {
    const key = String(reply.parent_comment_id);
    repliesByParent.set(key, [...(repliesByParent.get(key) || []), reply]);
  });
  return comments
    .filter((comment) => !comment.parent_comment_id)
    .map((comment) => renderComment(comment, repliesByParent.get(String(comment.community_comment_id)) || []))
    .join("");
}

function renderComment(comment, replies = []) {
  return `
    <article class="community-comment" data-comment-id="${escapeHtml(comment.community_comment_id)}">
      <div class="community-comment-meta">
        <strong>${escapeHtml(comment.member_name || "알 수 없는 회원")}</strong>
        <span>${escapeHtml(formatDate(comment.created_at))}</span>
      </div>
      <p>${escapeHtml(comment.content || "")}</p>
      <div class="community-comment-actions">
        <button type="button" data-reply-comment="${escapeHtml(comment.community_comment_id)}">답글</button>
        ${comment.owner ? `<button type="button" data-delete-comment="${escapeHtml(comment.community_comment_id)}">삭제</button>` : ""}
        ${!comment.owner ? `<button type="button" data-report-comment="${escapeHtml(comment.community_comment_id)}">신고</button>` : ""}
      </div>
      <div class="community-replies">
        ${replies.map(renderReply).join("")}
      </div>
      <form class="community-reply-form" data-reply-form="${escapeHtml(comment.community_comment_id)}" style="display:none;">
        <textarea name="content" rows="2" maxlength="1000" placeholder="답글을 입력하세요." required></textarea>
        <div>
          <button type="button" data-cancel-reply="${escapeHtml(comment.community_comment_id)}">취소</button>
          <button type="submit">등록</button>
        </div>
      </form>
    </article>
  `;
}

function bindCommentActions(container) {
  container.querySelectorAll("[data-reply-comment]").forEach((button) => {
    button.addEventListener("click", () => {
      if (!requireLogin()) return;
      const form = container.querySelector(`[data-reply-form="${CSS.escape(button.dataset.replyComment)}"]`);
      if (form) form.style.display = "grid";
    });
  });

  container.querySelectorAll("[data-cancel-reply]").forEach((button) => {
    button.addEventListener("click", () => {
      const form = container.querySelector(`[data-reply-form="${CSS.escape(button.dataset.cancelReply)}"]`);
      if (form) {
        form.reset();
        form.style.display = "none";
      }
    });
  });

  container.querySelectorAll(".community-reply-form").forEach((form) => {
    form.addEventListener("submit", async (event) => {
      event.preventDefault();
      if (!requireLogin()) return;
      const parentCommentId = form.dataset.replyForm;
      const formData = new FormData(form);
      const content = String(formData.get("content") || "").trim();
      if (!content) return;
      const res = await apiRequest(`${contextPath}/api/community/posts/${encodeURIComponent(communityPostId)}/comments`, {
        method: "POST",
        body: JSON.stringify({
          content,
          parent_comment_id: parentCommentId,
        }),
      });
      if (!res.ok) {
        alert(`답글을 등록하지 못했습니다. (${res.status})`);
        return;
      }
      await loadComments();
    });
  });

  container.querySelectorAll("[data-delete-comment]").forEach((button) => {
    button.addEventListener("click", async () => {
      if (!confirm("댓글을 삭제하시겠습니까?")) return;
      const res = await apiRequest(`${contextPath}/api/community/comments/${encodeURIComponent(button.dataset.deleteComment)}`, {
        method: "DELETE",
      });
      if (!res.ok) {
        alert(`댓글을 삭제하지 못했습니다. (${res.status})`);
        return;
      }
      await loadComments();
    });
  });

  container.querySelectorAll("[data-report-comment]").forEach((button) => {
    button.addEventListener("click", async () => {
      if (!requireLogin()) return;
      const reason = prompt("신고 사유를 입력하세요.");
      if (!reason || !reason.trim()) return;
      const res = await apiRequest(`${contextPath}/api/reports/community-comments/${encodeURIComponent(button.dataset.reportComment)}`, {
        method: "POST",
        body: JSON.stringify({ reason: reason.trim() }),
      });
      if (!res.ok) {
        alert(`댓글을 신고하지 못했습니다. (${res.status})`);
        return;
      }
      alert("신고가 접수되었습니다.");
    });
  });
}

function renderReply(reply) {
  return `
    <article class="community-comment community-comment-reply" data-comment-id="${escapeHtml(reply.community_comment_id)}">
      <div class="community-comment-meta">
        <strong>${escapeHtml(reply.member_name || "알 수 없는 회원")}</strong>
        <span>${escapeHtml(formatDate(reply.created_at))}</span>
      </div>
      <p>${escapeHtml(reply.content || "")}</p>
      <div class="community-comment-actions">
        ${reply.owner ? `<button type="button" data-delete-comment="${escapeHtml(reply.community_comment_id)}">삭제</button>` : ""}
        ${!reply.owner ? `<button type="button" data-report-comment="${escapeHtml(reply.community_comment_id)}">신고</button>` : ""}
      </div>
    </article>
  `;
}

function formatDate(value) {
  if (!value) return "-";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return String(value);
  return date.toLocaleString("ko-KR");
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}
