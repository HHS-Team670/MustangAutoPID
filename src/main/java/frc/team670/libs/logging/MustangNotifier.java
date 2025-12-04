package frc.team670.libs.logging;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.util.function.FloatSupplier;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.LongSupplier;
import org.littletonrobotics.junction.Logger;

public final class MustangNotifier {

  private MustangNotifier() {}

  private static final Map<Class<?>, BiConsumer<String, Object>> typeDispatchers = new HashMap<>();

  private static Subsystem ctx;

  public static void setContext(Subsystem mSubsystem) {
    ctx = mSubsystem;
  }

  public static void clearContext() {
    ctx = null;
  }

  static {
    // byte types
    typeDispatchers.put(byte[].class, (key, val) -> Logger.recordOutput(key, (byte[]) val));
    typeDispatchers.put(byte[][].class, (key, val) -> Logger.recordOutput(key, (byte[][]) val));

    // boolean types
    typeDispatchers.put(boolean.class, (key, val) -> Logger.recordOutput(key, (boolean) val));
    typeDispatchers.put(
        BooleanSupplier.class, (key, val) -> Logger.recordOutput(key, (BooleanSupplier) val));
    typeDispatchers.put(boolean[].class, (key, val) -> Logger.recordOutput(key, (boolean[]) val));
    typeDispatchers.put(
        boolean[][].class, (key, val) -> Logger.recordOutput(key, (boolean[][]) val));

    // int types
    typeDispatchers.put(int.class, (key, val) -> Logger.recordOutput(key, (int) val));
    typeDispatchers.put(int[].class, (key, val) -> Logger.recordOutput(key, (int[]) val));
    typeDispatchers.put(int[][].class, (key, val) -> Logger.recordOutput(key, (int[][]) val));

    // float types
    typeDispatchers.put(float.class, (key, val) -> Logger.recordOutput(key, (float) val));
    typeDispatchers.put(
        FloatSupplier.class,
        (key, val) -> Logger.recordOutput(key, ((FloatSupplier) val).getAsFloat()));
    typeDispatchers.put(float[].class, (key, val) -> Logger.recordOutput(key, (float[]) val));
    typeDispatchers.put(float[][].class, (key, val) -> Logger.recordOutput(key, (float[][]) val));

    // long types
    typeDispatchers.put(long.class, (key, val) -> Logger.recordOutput(key, (long) val));
    typeDispatchers.put(
        LongSupplier.class, (key, val) -> Logger.recordOutput(key, (LongSupplier) val));
    typeDispatchers.put(long[].class, (key, val) -> Logger.recordOutput(key, (long[]) val));
    typeDispatchers.put(long[][].class, (key, val) -> Logger.recordOutput(key, (long[][]) val));

    // double types
    typeDispatchers.put(double.class, (key, val) -> Logger.recordOutput(key, (double) val));
    typeDispatchers.put(
        DoubleSupplier.class, (key, val) -> Logger.recordOutput(key, (DoubleSupplier) val));
    typeDispatchers.put(double[].class, (key, val) -> Logger.recordOutput(key, (double[]) val));
    typeDispatchers.put(double[][].class, (key, val) -> Logger.recordOutput(key, (double[][]) val));

    // String types
    typeDispatchers.put(String.class, (key, val) -> Logger.recordOutput(key, (String) val));
    typeDispatchers.put(String[].class, (key, val) -> Logger.recordOutput(key, (String[]) val));
    typeDispatchers.put(String[][].class, (key, val) -> Logger.recordOutput(key, (String[][]) val));

    typeDispatchers.put(
        Pose2d.class,
        (key, val) -> {
          Logger.recordOutput(key, (Pose2d) val);
        });

    typeDispatchers.put(
        Pose3d.class,
        (key, val) -> {
          Logger.recordOutput(key, (Pose3d) val);
        });
  }

  public static void error(String error) {
    String str = error + "\n\n";
    for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
      str += e.toString() + "\n";
    }
    str += "\n";
    ConsoleLogger.consoleError(str);
  }

  public static void error(Exception exception) {
    String str = "\n";
    for (StackTraceElement e : exception.getStackTrace()) {
      str += e.toString() + "\n";
    }
    str += exception.getMessage() + "\n";
    Logger.recordOutput("Console", str);
    ConsoleLogger.consoleError(str);
  }

  public static void error(Subsystem erroredSubsystem, String error) {
    MustangNotifier.error(error);
    Logger.recordOutput(erroredSubsystem.getClass().getSimpleName() + "/error", error);
  }

  public static void warning(String warning) {
    ConsoleLogger.consoleWarning(warning);
  }

  public static void warning(Subsystem warnedSubsytem, String warning) {
    ConsoleLogger.consoleWarning(warning);
    Logger.recordOutput(warnedSubsytem.getClass().getSimpleName() + "/warning", warning);
  }

  public static void message(String msg) {
    ConsoleLogger.consoleLog(msg);
    Logger.recordOutput("console", msg);
  }

  public static void log(Subsystem mSubsystem, String key, Object value) {
    setContext(mSubsystem);
    log(key, value);
    clearContext();
  }

  public static void log(String key, Object value) {

    if (ctx != null) {
      key = ctx.getName() + "/" + key;
    }

    if (value == null) {
      Logger.recordOutput(key, "null");
      return;
    }

    Class<?> clazz = value.getClass();

    // Handle boxed primitives mapping to primitive keys
    if (clazz == Integer.class) clazz = int.class;
    else if (clazz == Boolean.class) clazz = boolean.class;
    else if (clazz == Float.class) clazz = float.class;
    else if (clazz == Long.class) clazz = long.class;
    else if (clazz == Double.class) clazz = double.class;

    final BiConsumer<String, Object> dispatcher = typeDispatchers.get(clazz);

    if (dispatcher != null) {
      dispatcher.accept(key, value);
    } else {
      Logger.recordOutput(key, value.toString());
      warning(
          "Unsupported logger type: "
              + clazz.getSimpleName()
              + "\n At "
              + key
              + " value: "
              + value);
    }
  }

  public static <E extends Enum<E>> void log(Subsystem mSubsystem, String key, E value) {

    key = mSubsystem.getName() + "/" + key;

    Logger.recordOutput(key, value);
  }

  public static <E extends Enum<E>> void log(Subsystem mSubsystem, String key, E[] value) {
    key = mSubsystem.getName() + "/" + key;

    Logger.recordOutput(key, value);
  }

  public static <E extends Enum<E>> void log(Subsystem mSubsystem, String key, E[][] value) {
    key = mSubsystem.getName() + "/" + key;

    Logger.recordOutput(key, value);
  }

  public static <E extends Enum<E>> void log(String key, E value) {

    if (ctx != null) {
      key = ctx.getName() + "/" + key;
    }

    Logger.recordOutput(key, value);
  }

  public static <E extends Enum<E>> void log(String key, E[] value) {
    if (ctx != null) {
      key = ctx.getName() + "/" + key;
    }

    Logger.recordOutput(key, value);
  }

  public static <E extends Enum<E>> void log(String key, E[][] value) {
    if (ctx != null) {
      key = ctx.getName() + "/" + key;
    }

    Logger.recordOutput(key, value);
  }
}
