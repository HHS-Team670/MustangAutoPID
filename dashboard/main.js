const {
  app,
  BrowserWindow,
  ipcMain,
  screen,
  shell,
  Menu,
} = require("electron");
const path = require("path");
const fs = require("fs");
const os = require("os");
const { getFileLines } = require("./pitaInterpreter.js");
const { exec } = require("child_process");
const { parseEvent } = require("./eventParser.js");
const { NetworkTable } = require("./network_table.js");

const config = JSON.parse(fs.readFileSync("config.json", "utf-8"));
let ip = config.envType == "test" ? "localhost" : "10.6.70.2";
const NT = new NetworkTable(ip);

const menuTemplate = [
  {
    label: "Options",
    submenu: [
      {
        label: "Save Layout",
        click: () => sendToRenderer("compile-dashboard", true),
      },
      {
        label: "Save New Layout",
        click: () => sendToRenderer("compile-dashboard", false),
      },
      {
        label: "Edit",
        click: () => sendToRenderer("change-edit-mode", true),
      },
      {
        label: "Stop Editing",
        click: () => sendToRenderer("change-edit-mode", false),
      },
      {
        label: "Add",
        click: () => sendToRenderer("add-item", ""),
      },
    ],
  },
];

function openSwitchWindow() {
  if (!switchWindow) {
    switchWindow = new BrowserWindow({
      width: 200,
      height: 90,
      x: w - 400,
      y: 0,
      alwaysOnTop: true,
      frame: false,
      resizable: false,
      webPreferences: { nodeIntegration: true, contextIsolation: false },
    });

    switchWindow.loadFile("switch.html");

    switchWindow.on("closed", () => {
      switchWindow = null;
    });
  }
}

function extractFilePath(stack) {
  const match = stack.match(/at\s+(.+)\((.+):(\d+)\)/);
  if (!match) {
    console.log("No match:", stack);
    return;
  }

  const [_, classPath, fileName, lineNum] = match;

  const classParts = classPath.split(".");
  classParts.pop();
  classParts.pop();
  const projectFile = path.join(
    __dirname,
    "..",
    "src/main/java",
    ...classParts,
    fileName
  );

  return { projectFile, lineNum };
}

let lastTimeStamp = undefined;

function getNewestDSEventFile() {
  if (config.envType != "dev") {
    return "./test.txt";
  }
  const dirPath = "C:\\Users\\Public\\Documents\\FRC\\Log Files\\DSLogs\\";
  const files = fs
    .readdirSync(dirPath)
    .filter((file) => file.endsWith(".dsevents"))
    .map((file) => ({
      name: file,
      time: fs.statSync(path.join(dirPath, file)).mtime.getTime(),
    }))
    .sort((a, b) => b.time - a.time);

  if (files.length === 0) {
    return null;
  }

  return path.join(dirPath, files[0].name);
}

let lastLen = 0;

function logLoop() {
  setInterval(() => {
    const events = parseEvent(
      getNewestDSEventFile(),
      lastTimeStamp,
      config.useFilters
        ? config.filters.map((f) => new RegExp(f, "i"))
        : undefined
    );
    if (lastLen < events.length) {
      sendToRenderer("new-logs", events);
      lastLen = events.length;
    }
  }, 1000);
}

const platform = os.platform();
let mainWindow;
let switchWindow;
let w;
let h;

function sendToRenderer(channel, data) {
  if (mainWindow && mainWindow.webContents) {
    mainWindow.webContents.send(channel, data);
  }
}

function createMainWindow() {
  mainWindow = new BrowserWindow({
    width: w,
    height: h,
    webPreferences: {
      preload: path.join(__dirname, "preload.js"),
      nodeIntegration: true,
      contextIsolation: false,
    },
  });

  mainWindow.loadFile("index.html");

  mainWindow.webContents.openDevTools();

  mainWindow.on("closed", () => {
    mainWindow = null;
  });
}

function waitForAdvantageScope(callback) {
  let checkCmd;
  if (platform == "win32") {
    checkCmd = `powershell -NoProfile -Command "(Get-Process | Where-Object { $_.MainWindowTitle -like '*AdvantageScope*' -and $_.MainWindowHandle -ne 0 }).MainWindowHandle"`;
  } else if (platform == "darwin") {
    callback();
    return;
  }

  const interval = setInterval(() => {
    exec(checkCmd, (err, stdout) => {
      if (!err && stdout.trim() !== "") {
        clearInterval(interval);
        callback();
      }
    });
  }, 1000);
}

function launchAdvantageKit() {
  let exe;
  if (platform == "darwin") {
    const userInfo = os.userInfo();
    exe =
      'open -a "/Users/' +
      userInfo.username +
      '/wpilib/2025/advantagescope/AdvantageScope (WPILib).app"';
  } else if (platform == "win32") {
    exe = "winlogsfinder.bat";
  }
  exec(exe, (error, stdout, stderr) => {
    if (error) {
      console.error("Failed to launch AdvantageScope:", error);
      sendToRenderer("loading-done", true);
      return;
    }
  });

  waitForAdvantageScope(() => {
    sendToRenderer("loading-done", true);
    openSwitchWindow();
  });
}

app.whenReady().then(async () => {
  const { width, height } = screen.getPrimaryDisplay().workAreaSize;
  w = width;
  h = height;

  const menu = Menu.getApplicationMenu();
  const customMenu = Menu.buildFromTemplate(menuTemplate);

  const combined = Menu.buildFromTemplate([
    ...(menu ? menu.items.map((m) => m) : []),
    ...customMenu.items,
  ]);

  Menu.setApplicationMenu(combined);

  createMainWindow();

  ipcMain.handle("launch-advantagekit", () => {
    launchAdvantageKit();
  });

  ipcMain.handle("switch-to-main", () => {
    if (mainWindow) {
      mainWindow.show();
      mainWindow.focus();
    }

    if (switchWindow) {
      switchWindow.close();
      switchWindow = null;
    }
  });

  app.on("activate", () => {
    if (BrowserWindow.getAllWindows().length === 0) createMainWindow();
  });

  ipcMain.handle("open-stack", (_event, line) => {
    const { projectFile, lineNum } = extractFilePath(line);

    if (fs.existsSync(projectFile)) {
      const fileUri = `vscode://file/${projectFile}:${lineNum}`;
      shell.openExternal(fileUri);
      openSwitchWindow();
    }
  });

  ipcMain.handle("renderer-ready", () => {
    if (config.envType == "dev" && platform != "win32") {
      return;
    }
    NT.onValueUpdate((key, val) => {
      if (!val) {
        val = NT.getValue(key);
      }
      let v = { itemKey: key, itemValue: val };
      if (typeof val == "string") {
        v.itemValue = String(val);
      }
      sendToRenderer("nt-value-update", v);
    });
    NT.onConnect(() => {
      console.log(NT.getValue("/AdvantageKit/Timestamp"));
      console.log(NT.getValue("/AdvantageKit/RealOutputs/OI/RobotPos"));
    });
    NT.start();
    logLoop();
  });

  ipcMain.handle("get-element-value", (_event, name) => {
    const result = NT.getValue(name);
    return result;
  });

  ipcMain.handle("writeInit", (_event, isOverride, data) => {
    fs.writeFileSync(
      "./scripts/init" + (isOverride ? "" : Date.now()) + ".pita",
      (isOverride ? "" : "end\n\n") + data
    );
  });

  setInterval(() => {
    const files = fs
      .readdirSync("./scripts")
      .filter((file) => file.endsWith(".pita"));

    const lines = files.map((v) => {
      return getFileLines("./scripts/" + v);
    });
    sendToRenderer("update-scripts", lines);
  }, 1000);
});

app.on("window-all-closed", () => {
  app.quit();
});
