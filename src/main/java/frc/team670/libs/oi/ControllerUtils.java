package frc.team670.libs.oi;

import edu.wpi.first.wpilibj.XboxController;
import java.util.ArrayList;
import java.util.List;

public class ControllerUtils {

  public static List<ControllerUtils> instances = new ArrayList<>();

  int index;

  XboxController mController;

  public ControllerUtils(XboxController controller) {
    this.mController = controller;
    instances.add(this);
  }

  /**
   * Up = negative | Down = positive
   *
   * @return The function returns the value of the left stick's X-axis position on an Xbox
   *     controller.
   */
  public double getLeftStickX() {
    return mController.getRawAxis(XboxButtons.LEFT_STICK_X);
  }

  /**
   * Up = negative | Down = positive
   *
   * @return The function returns the value of the left stick's Y-axis position on an Xbox
   *     controller.
   */
  public double getLeftStickY() {
    return mController.getRawAxis(XboxButtons.LEFT_STICK_Y);
  }

  /**
   * Up = negative | Down = positive
   *
   * @return The function returns the value of the right stick's X-axis position on an Xbox
   *     controller.
   */
  public double getRightStickX() {
    return mController.getRawAxis(XboxButtons.RIGHT_STICK_X);
  }

  /**
   * Up = negative | Down = positive
   *
   * @return The function returns the value of the right stick's Y-axis position on an Xbox
   *     controller.
   */
  public double getRightStickY() {
    return mController.getRawAxis(XboxButtons.RIGHT_STICK_Y);
  }

  /**
   * @return If there is any movment of the joystics left/right x/y gives a 2% wiggle room for stick
   *     drift defaults to driver controller assuming that driver controllers index is 0 within the
   *     instances array of this class
   */

  /**
   * @return If there is any movment of the joystics left/right x/y gives a 2% wiggle room for stick
   */
  public boolean joysticMoved() {
    return joysticMoved(index);
  }

  /**
   * @return If there is any movment of the joystics left/right x/y gives a 2% wiggle room for stick
   *     drift
   * @param controller this is the index of the controller in the instances array of this class
   *     driver = 0 operator = 1
   */
  public static boolean joysticMoved(int controller) {
    ControllerUtils targetController = instances.get(controller);
    return Math.abs(targetController.getLeftStickX()) > 0.02
        || Math.abs(targetController.getLeftStickY()) > 0.02
        || Math.abs(targetController.getRightStickX()) > 0.02
        || Math.abs(targetController.getRightStickY()) > 0.02;
  }
}
