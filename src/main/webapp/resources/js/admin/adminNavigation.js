const DEFAULT_PANEL = "dashboard";

document.addEventListener("DOMContentLoaded", () => {
  const buttons = Array.from(document.querySelectorAll("[data-admin-target]"));
  const panels = Array.from(document.querySelectorAll("[data-admin-panel]"));
  if (buttons.length === 0 || panels.length === 0) return;

  buttons.forEach((button) => {
    button.addEventListener("click", () => {
      activatePanel(button.dataset.adminTarget || DEFAULT_PANEL, true);
    });
  });

  window.addEventListener("hashchange", () => {
    activatePanel(getPanelFromHash(), false);
  });

  activatePanel(getPanelFromHash(), false);
});

function getPanelFromHash() {
  const value = window.location.hash.replace(/^#/, "");
  return value || DEFAULT_PANEL;
}

function activatePanel(target, updateHash) {
  const panels = Array.from(document.querySelectorAll("[data-admin-panel]"));
  const panelNames = panels.map((panel) => panel.dataset.adminPanel);
  const nextPanel = panelNames.includes(target) ? target : DEFAULT_PANEL;

  document.querySelectorAll("[data-admin-target]").forEach((button) => {
    const active = button.dataset.adminTarget === nextPanel;
    button.classList.toggle("is-active", active);
    button.setAttribute("aria-current", active ? "page" : "false");
  });

  panels.forEach((panel) => {
    const active = panel.dataset.adminPanel === nextPanel;
    panel.classList.toggle("is-active", active);
    panel.hidden = !active;
  });

  if (updateHash && window.location.hash !== `#${nextPanel}`) {
    window.history.replaceState(null, "", `#${nextPanel}`);
  }
}
