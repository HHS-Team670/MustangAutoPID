document.addEventListener("DOMContentLoaded", () => {
  const tabs = document.querySelectorAll(".tab");
  const contents = document.querySelectorAll(".tab-content");
  const stackcloser = document.getElementById("close-stack");
  const consoleLog = document.getElementById("console-log");
  const pauseBtn = document.getElementById("pause-scroll");
  const db = document.getElementById("dashboard-content");
  const elementModal = document.getElementById("elementModal");
  const closeElementModal = document.getElementById("closeElementModal");
  const createElementBtn = document.getElementById("createElementBtn");

  let autoScrollEnabled = true;
  let isEditable = false;
  let cache = {};
  let scripts = [];

  const vars = {};

  function kit(value) {
    return "/AdvantageKit/RealOutputs/" + value;
  }

  function special(type, location) {
    return `${type}|||${location}`;
  }

  function set(name, value) {
    vars[name] = value;
  }

  function get(name) {
    return vars[name];
  }

  function makeDraggable(element) {
    let offsetX = 0;
    let offsetY = 0;
    let isDragging = false;

    function onMouseMove(e) {
      if (!isDragging) return;
      e.preventDefault();
      element.style.left = e.clientX - offsetX + "px";
      element.style.top = e.clientY - offsetY + "px";
    }

    function onMouseUp() {
      if (isDragging) {
        isDragging = false;
        uiFrozen = false;
        element.style.cursor = "grab";
        document.removeEventListener("mousemove", onMouseMove);
        document.removeEventListener("mouseup", onMouseUp);
      }
    }

    element.addEventListener("mousedown", (e) => {
      if (!isEditable) {
        return;
      }
      isDragging = true;
      uiFrozen = true;
      offsetX = e.clientX - element.offsetLeft;
      offsetY = e.clientY - element.offsetTop;
      element.style.cursor = "grabbing";
      document.addEventListener("mousemove", onMouseMove);
      document.addEventListener("mouseup", onMouseUp);
    });

    element.style.cursor = "grab";
  }

  function makeResizable(element) {
    const handle = document.createElement("div");
    handle.classList.add("resize-handle");
    element.appendChild(handle);

    let isResizing = false;
    let startX, startY, startWidth, startHeight;

    handle.addEventListener("mousedown", (e) => {
      if (!isEditable) return;
      e.preventDefault();
      e.stopPropagation();
      isResizing = true;
      startX = e.clientX;
      startY = e.clientY;
      startWidth = parseInt(window.getComputedStyle(element).width, 10);
      startHeight = parseInt(window.getComputedStyle(element).height, 10);
      document.addEventListener("mousemove", resize);
      document.addEventListener("mouseup", stopResize);
    });

    function resize(e) {
      if (!isResizing) return;
      const newWidth = startWidth + (e.clientX - startX);
      const newHeight = startHeight + (e.clientY - startY);
      element.style.width = Math.max(newWidth, 50) + "px";
      element.style.height = Math.max(newHeight, 30) + "px";
    }

    function stopResize() {
      isResizing = false;
      document.removeEventListener("mousemove", resize);
      document.removeEventListener("mouseup", stopResize);
    }
  }

  function makeRemoveable(element) {
    const btn = document.createElement("div");
    btn.classList.add("remove-btn");
    btn.textContent = "×";
    element.appendChild(btn);

    btn.addEventListener("click", (e) => {
      e.stopPropagation();
      element.remove();
    });
  }

  function createElement(name, key, width, height) {
    let a = document.getElementById(name);
    if (!a) {
      a = document.createElement("div");
      a.id = name;
      a.key = key;
      a.classList.add("db-outer");
      makeDraggable(a);
      db.appendChild(a);

      const nameDisplay = document.createElement("a");
      nameDisplay.classList.add("db-name");
      nameDisplay.textContent = name;
      a.appendChild(nameDisplay);

      const internalContent = document.createElement("div");
      internalContent.classList.add("db-inner");
      if (key.startsWith("photon-vision|||")) {
        const port = key.split("|||")[1];
        const streamUrl = `http://photonvision.local:${port}/stream.mjpg`;
        const img = document.createElement("img");
        img.src = streamUrl;
        img.style.width = "100%";
        img.style.height = "100%";
        img.style.objectFit = "cover";
        internalContent.appendChild(img);
      } else {
        const txt = document.createElement("a");
        txt.classList.add("db-value");
        internalContent.appendChild(txt);
      }
      a.appendChild(internalContent);
      makeResizable(a);
      makeRemoveable(a);
    }

    a.style.width = width + "px";
    a.style.height = height + "px";
  }

  function setElementSize(name, width, height) {
    const a = document.getElementById(name);
    a.style.width = width + "px";
    a.style.height = height + "px";
  }

  function setElementPos(name, x, y) {
    const a = document.getElementById(name);
    a.style.left = x + "px";
    a.style.top = y + "px";
  }

  async function getElementValue(name) {
    return new String(await window.api.getValue(name));
  }

  async function updateValues() {
    let len = db.children.length;
    for (let i = 0; i < len; i++) {
      const item = db.children.item(i);
      let key = item.key;
      if (key.includes("|||")) {
        continue;
      }
      let preVal = cache[key] ?? null;
      const val =
        typeof preVal == "number" ? Math.round(preVal * 1000) / 1000 : preVal;

      if (new String(val) == "true") {
        item.children.item(1).style.backgroundColor = "rgb(0,255,0)";
        item.children.item(1).children.item(0).textContent = "";
      } else if (new String(val) == "false") {
        item.children.item(1).style.backgroundColor = "rgb(255,0,0)";
        item.children.item(1).children.item(0).textContent = "";
      } else if (
        item.children.item(1).children.item(0).textContent !== String(val)
      ) {
        item.children.item(1).children.item(0).textContent = String(val);
      }
    }
  }

  const context = {
    kit,
    special,
    getElementValue,
    createElement,
    setElementPos,
    setElementSize,
    set,
    get,
  };
  window.api.preloadContext(context);
  async function mainLoop() {
    try {
      for (const s of scripts) {
        await window.api.execute(s);
      }
      await updateValues();
    } catch (err) {
      console.error(err);
    }

    if (autoScrollEnabled && consoleLog.scrollTop !== consoleLog.scrollHeight) {
      consoleLog.scrollTop = consoleLog.scrollHeight;
    }

    setTimeout(mainLoop, 500);
  }
  mainLoop();

  pauseBtn.addEventListener("click", () => {
    autoScrollEnabled = !autoScrollEnabled;
    pauseBtn.classList.toggle("active", !autoScrollEnabled);
    pauseBtn.textContent = autoScrollEnabled ? "Pause" : "Resume";
  });

  tabs.forEach((tab) => {
    tab.addEventListener("click", () => {
      if (tab.id == "ignore") {
        return;
      }
      tabs.forEach((t) => t.classList.remove("active"));
      contents.forEach((c) => c.classList.remove("active"));
      tab.classList.add("active");
      currentTab = tab.dataset.tab;
      document.getElementById(tab.dataset.tab).classList.add("active");
    });
  });

  window.api.ready();

  const logsTab = document.querySelector('[data-tab="logs"]');
  if (logsTab) {
    logsTab.addEventListener("click", () => {
      showLoading();
      window.api.launchAdvantageKit();
    });
  }

  function showLoading() {
    document.getElementById("loading-overlay").classList.add("active");
  }

  function hideLoading() {
    document.getElementById("loading-overlay").classList.remove("active");
  }

  function closeStack() {
    document.getElementById("stackOverlay").classList.remove("active");
  }

  stackcloser.onclick = closeStack;

  function showCreateElementModal() {
    elementModal.classList.add("active");
  }

  function hideCreateElementModal() {
    elementModal.classList.remove("active");
  }

  closeElementModal.addEventListener("click", hideCreateElementModal);

  createElementBtn.addEventListener("click", () => {
    const name = document.getElementById("elementName").value.trim();
    const key = document.getElementById("elementKey").value.trim();

    if (name && key) {
      createElement(name, key, 100, 100);
      console.log(name);
      document.getElementById("elementName").value = "";
      document.getElementById("elementKey").value = "";
      hideCreateElementModal();
    } else {
      alert("Please fill out both Name and Key.");
    }
  });

  function addLog(log) {
    if (log.msgs.join("\n").trim() == "") {
      if (log.threatLvl == 4) {
      } else {
        return;
      }
    }

    const logDiv = document.createElement("div");
    logDiv.classList.add("log");
    if (
      log.msgs
        .join("\n")
        .includes("********** Robot program starting **********")
    ) {
      log.msgs = ["********** Robot program starting **********"];
      logDiv.classList.add("green");
    } else if (
      log.msgs
        .join("\n")
        .includes("********** Robot program startup complete **********")
    ) {
      log.msgs = ["********** Robot program startup complete **********"];
      logDiv.classList.add("green");
    } else if (log.threatLvl == 4) {
      logDiv.classList.add("red");
    }
    if (log.threatLvl == 4) {
      logDiv.onclick = () => {
        const stackLines = document.getElementById("stackLines");
        stackLines.replaceChildren();
        log.stack.forEach((s) => {
          const trace = document.createElement("p");
          trace.textContent = s;
          trace.classList.add("stack-line");
          trace.onclick = () => {
            window.api.openFileFromStack(s);
          };
          stackLines.appendChild(trace);
        });
        document.getElementById("stackOverlay").classList.add("active");
      };
    }
    consoleLog.appendChild(logDiv);
    const textConent = document.createElement("p");
    textConent.textContent = log.msgs.join("\n");
    logDiv.appendChild(textConent);
    if (autoScrollEnabled) {
      consoleLog.scrollTop = consoleLog.scrollHeight;
    }
  }

  consoleLog.addEventListener("scroll", () => {
    const nearBottom =
      consoleLog.scrollTop + consoleLog.clientHeight >=
      consoleLog.scrollHeight - 20;
    if (!nearBottom && autoScrollEnabled) {
      autoScrollEnabled = false;
      pauseBtn.classList.add("active");
      pauseBtn.textContent = "Resume";
    }
  });

  window.api.onDoneLoading((success) => {
    hideLoading();

    if (!success) {
      alert("Failed to launch AdvantageScope.");
    }
  });

  window.api.updateCache((key, value) => {
    cache[key] = value;
  });

  window.api.scriptUpdates((newScripts) => {
    scripts = newScripts;
  });

  window.api.loadNewLogs((log) => {
    consoleLog.replaceChildren();
    log.forEach((l) => {
      addLog(l);
    });
  });

  window.api.compileDashboard((isOverride) => {
    let output =
      'never\n\nyou should generate this file through the window\n\ncond get("initalized") is not true\n\n';
    let len = db.children.length;
    for (let i = 0; i < len; i++) {
      const item = db.children.item(i);
      let key = item.key;
      let keyOutput = key;
      if (key.includes("|||")) {
        keyOutput = `special("${key.split("|||")[0]}", "${
          key.split("|||")[1]
        }")`;
      } else if (key.startsWith("/AdvantageKit/RealOutputs/")) {
        keyOutput = `kit("${key.slice(26)}")`;
      }
      output += `createElement("${
        item.id
      }", ${keyOutput}, ${item.style.width.replace(
        "px",
        ""
      )}, ${item.style.height.replace("px", "")})\n`;
      output += `setElementPos("${item.id}", ${item.style.left.replace(
        "px",
        ""
      )}, ${item.style.top.replace("px", "")})\n`;
    }
    output += '\nset("initalized", true)';
    isEditable = false;
    const elements = document.querySelectorAll(".db-outer");
    elements.forEach((el) => {
      el.classList.remove("editable");
    });
    window.api.writeInit(isOverride, output);
  });

  window.api.updateEditable((e) => {
    isEditable = e;

    const elements = document.querySelectorAll(".db-outer");
    elements.forEach((el) => {
      if (isEditable) {
        el.classList.add("editable");
      } else {
        el.classList.remove("editable");
      }
    });
  });

  window.api.addItem(() => {
    showCreateElementModal();
  });
});
