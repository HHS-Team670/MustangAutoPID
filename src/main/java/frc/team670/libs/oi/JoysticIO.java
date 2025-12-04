package frc.team670.libs.oi;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.event.BooleanEvent;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class JoysticIO {

  private final Trigger io;

  public JoysticIO(Trigger io) {
    this.io = io;
  }

  public JoysticIO(XboxController controller, int button) {
    io = new JoystickButton(controller, button);
  }

  public JoysticIO(BooleanEvent event) {
    io = new Trigger(event);
  }

  public JoysticIO(XboxController controller, double angle) {
    io = new POVButton(controller, (int) angle);
  }

  public JoysticIO onTrue(Command command) {
    io.onTrue(command);
    return this;
  }

  public JoysticIO onTrue(Runnable command) {
    io.onTrue(new InstantCommand(command));
    return this;
  }

  public JoysticIO onFalse(Command command) {
    io.onFalse(command);
    return this;
  }

  public JoysticIO onFalse(Runnable command) {
    io.onFalse(new InstantCommand(command));
    return this;
  }
}
