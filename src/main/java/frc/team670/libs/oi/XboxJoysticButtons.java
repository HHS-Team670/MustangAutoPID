package frc.team670.libs.oi;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class XboxJoysticButtons {

  private XboxJoysticButtons() {}

  private static final XboxController driver = new XboxController(0);
  private static final XboxController operator = new XboxController(1);

  public static final ControllerUtils driverUtils = new ControllerUtils(driver);
  public static final ControllerUtils operatorUtils = new ControllerUtils(operator);

  public static final JoysticIO Driver_ButtonA = new JoysticIO(driver, XboxButtons.A);
  public static final JoysticIO Driver_ButtonB = new JoysticIO(driver, XboxButtons.B);
  public static final JoysticIO Driver_ButtonX = new JoysticIO(driver, XboxButtons.X);
  public static final JoysticIO Driver_ButtonY = new JoysticIO(driver, XboxButtons.Y);
  public static final JoysticIO Driver_LeftBumper = new JoysticIO(driver, XboxButtons.LEFT_BUMPER);
  public static final JoysticIO Driver_RightBumper =
      new JoysticIO(driver, XboxButtons.RIGHT_BUMPER);
  public static final JoysticIO Driver_ButtonBack = new JoysticIO(driver, XboxButtons.BACK);
  public static final JoysticIO Driver_ButtonStart = new JoysticIO(driver, XboxButtons.START);
  public static final JoysticIO Driver_LeftJoystickPress =
      new JoysticIO(driver, XboxButtons.LEFT_JOYSTICK_BUTTON);
  public static final JoysticIO Driver_RightJoystickPress =
      new JoysticIO(driver, XboxButtons.RIGHT_JOYSTICK_BUTTON);
  public static final JoysticIO Driver_LeftTrigger =
      new JoysticIO(driver.leftTrigger(CommandScheduler.getInstance().getDefaultButtonLoop()));
  public static final JoysticIO Driver_RightTrigger =
      new JoysticIO(driver.rightTrigger(CommandScheduler.getInstance().getDefaultButtonLoop()));
  public static final JoysticIO Driver_Dpad_North = new JoysticIO(driver, 0.0);
  public static final JoysticIO Driver_Dpad_NorthEast = new JoysticIO(driver, 45.0);
  public static final JoysticIO Driver_Dpad_East = new JoysticIO(driver, 90.0);
  public static final JoysticIO Driver_Dpad_SouthEast = new JoysticIO(driver, 135.0);
  public static final JoysticIO Driver_Dpad_South = new JoysticIO(driver, 180.0);
  public static final JoysticIO Driver_Dpad_SouthWest = new JoysticIO(driver, 225.0);
  public static final JoysticIO Driver_Dpad_West = new JoysticIO(driver, 270.0);
  public static final JoysticIO Driver_Dpad_NorthWest = new JoysticIO(driver, 315.0);

  public static final JoysticIO Operator_ButtonA = new JoysticIO(operator, XboxButtons.A);
  public static final JoysticIO Operator_ButtonB = new JoysticIO(operator, XboxButtons.B);
  public static final JoysticIO Operator_ButtonX = new JoysticIO(operator, XboxButtons.X);
  public static final JoysticIO Operator_ButtonY = new JoysticIO(operator, XboxButtons.Y);
  public static final JoysticIO Operator_LeftBumper =
      new JoysticIO(operator, XboxButtons.LEFT_BUMPER);
  public static final JoysticIO Operator_RightBumper =
      new JoysticIO(operator, XboxButtons.RIGHT_BUMPER);
  public static final JoysticIO Operator_ButtonBack = new JoysticIO(operator, XboxButtons.BACK);
  public static final JoysticIO Operator_ButtonStart = new JoysticIO(operator, XboxButtons.START);
  public static final JoysticIO Operator_LeftJoysticPress =
      new JoysticIO(operator, XboxButtons.LEFT_JOYSTICK_BUTTON);
  public static final JoysticIO Operator_RightJoysticPress =
      new JoysticIO(operator, XboxButtons.RIGHT_JOYSTICK_BUTTON);
  public static final JoysticIO Operator_LeftTrigger =
      new JoysticIO(operator.leftTrigger(CommandScheduler.getInstance().getDefaultButtonLoop()));
  public static final JoysticIO Operator_RightTrigger =
      new JoysticIO(operator.rightTrigger(CommandScheduler.getInstance().getDefaultButtonLoop()));
  public static final JoysticIO Operator_Dpad_North = new JoysticIO(operator, 0.0);
  public static final JoysticIO Operator_Dpad_NorthEast = new JoysticIO(operator, 45.0);
  public static final JoysticIO Operator_Dpad_East = new JoysticIO(operator, 90.0);
  public static final JoysticIO Operator_Dpad_SouthEast = new JoysticIO(operator, 135.0);
  public static final JoysticIO Operator_Dpad_South = new JoysticIO(operator, 180.0);
  public static final JoysticIO Operator_Dpad_SouthWest = new JoysticIO(operator, 225.0);
  public static final JoysticIO Operator_Dpad_West = new JoysticIO(operator, 270.0);
  public static final JoysticIO Operator_Dpad_NorthWest = new JoysticIO(operator, 315.0);

  public static XboxController getDriverController() {
    return driver;
  }

  public static XboxController getOperatorController() {
    return operator;
  }
}
