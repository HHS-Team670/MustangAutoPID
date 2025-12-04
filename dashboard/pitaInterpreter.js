const fs = require("fs");

/**
 * Runs a custom script file with injected API functions.
 */

let context = Object.freeze({});

function preloadContext(ctx) {
  if (typeof ctx !== "object" || ctx === null) {
    throw new TypeError("Context must be a non-null object.");
  }

  const safeContext = {};
  const seen = new WeakSet();

  function cloneSafe(obj) {
    if (obj === null || typeof obj !== "object") return obj;
    if (seen.has(obj)) return undefined;
    seen.add(obj);

    if (typeof obj === "function") {
      return async (...args) => await obj(...args);
    }

    const copy = Array.isArray(obj) ? [] : {};
    for (const [k, v] of Object.entries(obj)) {
      if (typeof v === "object" && v !== null) {
        copy[k] = cloneSafe(v);
      } else if (typeof v === "function") {
        if (v.constructor.name == "AsyncFunction") {
          copy[k] = async (...args) => await v(...args);
        } else {
          copy[k] = (...args) => v(...args);
        }
      } else if (typeof v !== "symbol") {
        copy[k] = v;
      }
    }
    return copy;
  }

  Object.assign(safeContext, cloneSafe(ctx));

  context = Object.freeze(safeContext);
}

function getFileLines(filePath) {
  const lines = fs
    .readFileSync(filePath, "utf-8")
    .split("\n")
    .map((l) => l.trim())
    .filter(Boolean);
  return lines;
}

async function runScript(lines) {
  const print = console.log;
  const sandbox = { ...context, print };
  const sandboxKeys = Object.keys(sandbox);
  const sandboxValues = Object.values(sandbox);

  let continueScript = false;

  for (const line of lines) {
    if (line.startsWith("cond")) {
      // Match anything after `cond` up to `is` / `is not`
      const match = line.match(/^cond\s+(.+?)\s+is\s+(not\s+)?(.+)$/);
      if (match) {
        const expr = match[1].trim();
        const isNot = !!match[2];
        let expected = match[3].trim();

        // Remove quotes if present
        if (
          (expected.startsWith('"') && expected.endsWith('"')) ||
          (expected.startsWith("'") && expected.endsWith("'"))
        ) {
          expected = expected.slice(1, -1);
        }

        try {
          const func = new Function(...sandboxKeys, `return ${expr}`);
          const actual = await func(...sandboxValues); // await for async functions
          continueScript = (String(actual) === expected) === !isNot;
        } catch (err) {
          console.error(`[ERROR] Failed to evaluate cond: ${line}`, err);
          continueScript = false;
        }
      } else {
        continueScript = false;
      }
    } else if (line.startsWith("allways")) {
      continueScript = true;
    } else if (line.startsWith("endif") && continueScript) {
      return;
    } else if (line.startsWith("end")) {
      return;
    } else if (continueScript) {
      try {
        const func = new Function(...sandboxKeys, line);
        await func(...sandboxValues); // await in case line uses async
      } catch (err) {
        console.error(`[ERROR] Failed to execute line: ${line}`, err);
      }
    }
  }
}

module.exports = { runScript, getFileLines, preloadContext };
