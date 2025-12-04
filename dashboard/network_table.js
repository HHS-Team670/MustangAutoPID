import NT from "wpilib-nt-client";

class NetworkTable {
  /**
   *
   * @param {*} ip the ip of the robot localhost if sim 10.6.70.2 if not
   */
  constructor(ip) {
    this.valueCache = {};
    this.ip = ip;
    this.client = new NT.Client();
    this.connected = false;
  }

  /**
   *
   * @param {*} key the key from which you want to get the data from
   * @returns the value assosiated with that key
   */
  getValue(key) {
    return this.valueCache[key] ?? null;
  }

  /**
   *
   * @param {*} callback the function to run once the network tables have been * fully initalized
   */
  onConnect(callback) {
    this.onConnectCallback = callback;
  }

  onValueUpdate(callback) {
    this.valueUpdate = callback;
  }

  /**
   * Starts the network table connection
   */
  start() {
    this.client.start((connected) => {
      if (connected) {
        this.connected = true;
        const entries = this.client.getEntries();
        for (const [key, entry] of Object.entries(entries)) {
          this.valueCache[entry.name] = entry.val;
          this.valueUpdate(entry.name, entry.val);
        }
        if (this.onConnectCallback) {
          this.onConnectCallback();
        }
      }
    }, this.ip);

    this.client.addListener((key, val) => {
      this.valueCache[key] = val;
      if (this.valueUpdate) {
        this.valueUpdate(key, val);
      }
    });
  }

  /**
   * stop the network table connection
   */
  stop() {
    this.client.stop();
  }
}

export { NetworkTable };
