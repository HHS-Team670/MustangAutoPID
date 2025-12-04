const { ipcRenderer } = require("electron");
const { runScript, preloadContext } = require("./pitaInterpreter.js");

window.api = {
  launchAdvantageKit: () => ipcRenderer.invoke("launch-advantagekit"),
  onDoneLoading: (callback) =>
    ipcRenderer.on("loading-done", (_event, success) => callback(success)),
  loadNewLogs: (callback) =>
    ipcRenderer.on("new-logs", (_event, log) => {
      callback(log);
    }),
  updateCache: (callback) => {
    ipcRenderer.on("nt-value-update", (_event, update) => {
      callback(update.itemKey, update.itemValue);
    });
  },
  scriptUpdates: (callback) => {
    ipcRenderer.on("update-scripts", (_event, newScripts) => {
      callback(newScripts);
    });
  },
  ready: () => ipcRenderer.invoke("renderer-ready"),
  openFileFromStack: (stack) => ipcRenderer.invoke("open-stack", stack),
  getValue: async (key) => {
    const result = await ipcRenderer.invoke("get-element-value", key);
    return result;
  },
  execute: async (name) => await runScript(name),
  preloadContext,
  writeInit: (isOverride, data) =>
    ipcRenderer.invoke("writeInit", isOverride, data),
  compileDashboard: (callback) => {
    ipcRenderer.on("compile-dashboard", (_event, isOverride) => {
      callback(isOverride);
    });
  },
  updateEditable: (callback) => {
    ipcRenderer.on("change-edit-mode", (_event, e) => {
      callback(e);
    });
  },
  addItem: (callback) => {
    ipcRenderer.on("add-item", () => {
      callback();
    });
  },
};
