package frc.team670.libs.utils;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class MustangMath {
  private MustangMath() {}

  public static double getRotationsFromDegrees(double theta, double gearRatio) {
    return (theta / 360) * gearRatio;
  }

  public static double getDegreesFromRotations(double rotations, double gearRatio) {
    return (rotations * 360) / gearRatio;
  }

  public static boolean doublesEqual(double double1, double double2) {
    return doublesEqual(double1, double2, 0.0001);
  }

  public static boolean doublesEqual(double double1, double double2, double errorMargin) {
    return Math.abs(double1 - double2) <= errorMargin;
  }

  public static double getMetersFromRotations(
      double rotations, double gearRatio, double circumferenceSprocket) {
    return (rotations / gearRatio) * circumferenceSprocket;
  }

  public static double getRotationsFromMeters(
      double meters, double gearRatio, double circumferenceSprocket) {
    return (meters / circumferenceSprocket) * gearRatio;
  }

  /**
   * Field Centric to Robot Centric Calculations:
   * https://www.canva.com/design/DAGgRZjmWXI/TYRSSrswz4FQHHXcytOO3g/edit
   *
   * @param pose1 origin to use for calculations
   * @param pose2 the position to find relative to the origin
   * @return the relative change from pose1 to pose2
   * @author Aarush Joglekar
   */
  public Pose2d relativeTo(Pose2d pose1, Pose2d pose2) {

    double xFieldCentric = pose2.getX() - pose1.getX();
    double yFieldCentric = pose2.getY() - pose1.getY();
    Rotation2d rotationDifference = pose2.getRotation().minus(pose1.getRotation());

    double distance = Math.sqrt(Math.pow(xFieldCentric, 2) + Math.pow(yFieldCentric, 2));

    // Avoid divide by 0 error in calculating fieldCentricAngle
    if (yFieldCentric == 0) {
      yFieldCentric = 0.00000000000000001;
    }

    double rotationOffset = pose2.getRotation().getRadians() * -1;
    double fieldCentricAngle = Math.atan(xFieldCentric / yFieldCentric);

    double fieldToRobotCentricAngle = rotationOffset + fieldCentricAngle;

    double xRobotCentric = Math.cos(fieldToRobotCentricAngle) * distance;
    double yRobotCentric = Math.sin(fieldToRobotCentricAngle) * distance;

    if (yFieldCentric < 0) {
      xRobotCentric *= -1;
      yRobotCentric *= -1;
    }

    return new Pose2d(xRobotCentric, yRobotCentric, rotationDifference);
  }
}
