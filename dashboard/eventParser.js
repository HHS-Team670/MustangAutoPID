import fs from "fs";

class DsEvent {
  constructor() {
    this.msgs = [];
    this.stack = [];
    this.parsingType = undefined;
    this.threatLvl = 0;
    this.isDone = false;
  }

  /**
   * updates the event with the given tag
   */
  update(tag) {
    if (tag.name == "time") {
      this.time = tag.value;
      return false;
    }

    if (!this.parsingType) {
      if (tag.name == "Code") {
        this.parsingType = 1;
        this.threatLvl = 0;
      } else if (tag.name == "message") {
        if (isStack(tag.value)) {
          this.stack.push(tag.value);
          this.parsingType = 2;
          this.threatLvl = 4;
        } else {
          this.msgs.push(tag.value);
          this.parsingType = 3;
          if (tag.value.toLowerCase().includes("error at")) {
            this.threatLvl = 4;
            this.parsingType = 4;
          } else if (!tag.value.toLowerCase().includes("at")) {
            this.threatLvl = 0;
            this.isDone = true;
            return true;
          } else if (tag.value.includes("[AdvantageKit] Logging to")) {
            this.isDone = true;
            this.parsingType = 19;
            return true;
          }
        }
      }
    } else {
      if (tag.name == "init" || tag.name == "clinit") {
        let lastMsg = this.msgs.pop();
        lastMsg += "<" + tag.name + ">" + tag.value;
        this.stack.push(lastMsg);
      } else if (tag.name == "location") {
        this.stack.push(tag.value);
      } else if (tag.name == "details") {
        this.msgs.push(tag.value);
      } else if (tag.name == "stack") {
        tag.value.forEach((element) => {
          this.stack.push(element);
        });
        this.isDone = true;
        return true;
      } else if (tag.name == "message") {
        if (isStack(tag.value)) {
          this.stack.push(tag.value);
        } else {
          if (tag.value.includes("[AdvantageKit] Logging to")) {
            this.isDone = true;
            this.parsingType = 19;
            return true;
          }
          this.msgs.push(tag.value);
          this.isDone = !tag.value.toLowerCase().includes("at");
          return !tag.value.toLowerCase().includes("at");
        }
      }
    }

    return false;
  }
}

let tagBuffer = [];

function consumeNextTag(lines) {
  let tag = lines.pop();
  tag = tag.split(">");
  tag.reverse();
  let name = tag.pop();
  tag.reverse();
  let value = tag.join(">");
  if (name == "message") {
    let values = value.split("\n");
    for (let i = 0; i < values.length; i++) {
      tagBuffer.push({ name: name, value: values[i] });
    }
  }
  if (tagBuffer.length > 0) {
    tagBuffer.reverse();
    let v = tagBuffer.pop();
    tagBuffer.reverse();
    tagBuffer.push({ name: name, value: value, skip: false });
    return v;
  }
  return { name: name, value: value, skip: false };
}

function sanitize(tag) {
  if (tag.name == "time") {
    let str = tag.value.trim();

    const negative = str.startsWith("-");
    const s = negative ? str.slice(1) : str;

    if (!s.includes(":")) {
      tag.skip = true;
      return;
    }
    const [minStr, secStr] = s.split(":");
    const minutes = parseInt(minStr, 10);

    const [secondsStr, millisStr] = secStr.split(".");
    const seconds = parseInt(secondsStr, 10);
    const millis = parseInt(millisStr, 10);

    let totalMillis = (minutes * 60 + seconds) * 1000 + millis;
    if (negative) totalMillis = -totalMillis;

    tag.value = totalMillis;
  } else if (tag.name == "message") {
    tag.value = tag.value.trim();
    if (tag.value.replace(/\s+/g, "") === "") {
      tag.skip = true;
    }
  } else if (tag.name == "stack") {
    tag.value = tag.value.trim();
    tag.value = tag.value.split("\n");
  } else if (tag.name == "clinit" || tag.name == "init") {
  } else if (tag.name == "details") {
    tag.value = tag.value.trim();
  } else if (tag.name == "location") {
    tag.value = tag.value.trim();
  } else {
    tag.skip = true;
  }
}

function isStack(value) {
  const stackTraceRegex =
    /^\s*at\s+[a-zA-Z0-9_$.\/]+\.[a-zA-Z0-9_$<>]+\([^)]*\)$/;

  return stackTraceRegex.test(value);
}

function parseEvent(file, lastTimeStamp = undefined, filters) {
  let content = fs.readFileSync(file, "utf-8");
  content = content.replace(/[^\x09\x0A\x0D\x20-\x7E]/g, "");
  content = content.replace(/�[^\s]*|[^\s]*�/g, "");

  const lines = content.split("<");

  lines.reverse();

  const events = [];
  let currentEvent = new DsEvent();

  while (lines.length > 0) {
    let tag = consumeNextTag(lines);
    sanitize(tag);
    if (currentEvent.isDone) {
      events.push(currentEvent);
      currentEvent = new DsEvent();
    }
    if (!tag.skip) {
      let isDone = currentEvent.update(tag);
      if (isDone) {
        events.push(currentEvent);
        currentEvent = new DsEvent();
        if (events[events.length - 1].parsingType == 19) {
          currentEvent.update(tag);
        }
      }
    }
  }

  function filter(ev) {
    let filteredEvents = ev;
    if (lastTimeStamp) {
      filteredEvents = filteredEvents.filter(
        (e) => e.timestamp > lastTimeStamp
      );
    }
    if (filters) {
      filteredEvents = filteredEvents.filter((e) => {
        for (const regex of filters) {
          if (regex.test(e.msgs.join("\n"))) {
            return false;
          }
        }
        return true;
      });
    }
    return filteredEvents;
  }

  return filter(events);
}

export { DsEvent, parseEvent };
