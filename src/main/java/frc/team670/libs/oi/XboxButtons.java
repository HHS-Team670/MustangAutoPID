package frc.team670.libs.oi;

public class XboxButtons {
  private XboxButtons() {}

  public static final int A = 1;
  public static final int B = 2;
  public static final int X = 3;
  public static final int Y = 4;
  public static final int RIGHT_BUMPER = 6;
  public static final int LEFT_BUMPER = 5;
  public static final int BACK = 7;
  public static final int START = 8;
  public static final int LEFT_JOYSTICK_BUTTON = 9;
  public static final int RIGHT_JOYSTICK_BUTTON = 10;

  // Controller Axes
  /** Left = Negative, Right = Positive [-1, 1] */
  public static final int LEFT_STICK_X = 0;

  /** Up = Negative, Down = Positive [-1, 1] */
  public static final int LEFT_STICK_Y = 1;

  /** Left = Negative, Right = Positive [-1, 1] */
  public static final int RIGHT_STICK_X = 4;

  /** Up = Negative, Down = Positive [-1, 1] */
  public static final int RIGHT_STICK_Y = 5;
}
