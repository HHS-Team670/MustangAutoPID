package frc.team670.libs.logging;

import edu.wpi.first.wpilibj.DriverStation;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements a logging system for the robot. Orginal code from MustangLibs edits made to remove
 * unnessasary code and better functionallity with driverstion
 */
public class ConsoleLogger {

  /**
   * Logging class for use by other classes to log though this custom logging scheme. All logging
   * should be done by calls to methods on this class instance or with the convenience methods of
   * the Logging class.
   */
  public static final Logger LOGGER = Logger.getGlobal();

  // Private constructor means this class cannot be instantiated. All access is
  // static.
  private ConsoleLogger() {}

  private static String currentMethod(Integer level) {
    StackTraceElement stackTrace[];

    stackTrace = new Throwable().getStackTrace();

    // This scheme depends on having one level in the package name between
    // team670 and the class name, ie: team670.robot.Logging.method. New levels
    // will require rewrite.

    try {
      String method = stackTrace[level].toString().split("670.")[1];

      int startPos = method.indexOf(".") + 1;

      return method.substring(startPos);
    } catch (Throwable e) {
      return "method not found";
    }
  }

  private static String appendMatchTime(String str) {
    return String.format(
        "robot: MatchTime:%s: %s: %s", DriverStation.getMatchTime(), currentMethod(2), str);
  }

  /**
   * Write an log to the driverstaion console
   *
   * @param message Message to log
   */
  public static void consoleLog(String message) {
    // logs to the console as well as our log file on RR disk.
    LOGGER.log(Level.INFO, appendMatchTime(message));
  }

  /**
   * Write an error to the driverstaion console
   *
   * @param message Message to use for debugging error
   */
  public static void consoleError(String message) {
    DriverStation.reportError(appendMatchTime(message), true);
  }

  /**
   * Write a warrning to the driverstaion console
   *
   * @param message Message to warn.
   */
  public static void consoleWarning(String message) {
    DriverStation.reportWarning(appendMatchTime(message), true);
  }
}
