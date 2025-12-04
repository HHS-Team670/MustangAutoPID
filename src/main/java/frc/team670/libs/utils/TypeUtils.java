package frc.team670.libs.utils;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Transform3d;
import frc.team670.libs.logging.MustangNotifier;

public class TypeUtils {

  private TypeUtils() {}

  public static <T> T unimplemented() {
    final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

    // Index 0 is getStackTrace, 1 is unimplemented(), 2 is the caller
    if (stackTrace.length > 2) {
      final StackTraceElement caller = stackTrace[2];
      final String info =
          String.format(
              "Warning: attempted to use unimplemented method at %s.%s(%s:%d)",
              caller.getClassName(),
              caller.getMethodName(),
              caller.getFileName(),
              caller.getLineNumber());
      MustangNotifier.warning(info);
    }
    return null;
  }

  public static Pose2d transform3ToPose2(Transform3d transform) {
    return new Pose2d(transform.getX(), transform.getY(), transform.getRotation().toRotation2d());
  }
}
