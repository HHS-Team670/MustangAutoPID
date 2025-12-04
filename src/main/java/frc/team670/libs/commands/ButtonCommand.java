package frc.team670.libs.commands;

import edu.wpi.first.wpilibj2.command.Command;
import java.util.function.BooleanSupplier;

public class ButtonCommand extends Command {
  Command command1;
  Command command2;
  Command finalCommand;
  BooleanSupplier isFirstCommand;

  public ButtonCommand(Command command1, Command command2, BooleanSupplier isFirstCommand) {
    this.command1 = command1;
    this.command2 = command2;
    this.isFirstCommand = isFirstCommand;
  }

  @Override
  public void initialize() {
    if (isFirstCommand.getAsBoolean()) {
      finalCommand = command1;
    } else {
      finalCommand = command2;
    }
    finalCommand.initialize();
  }

  @Override
  public void execute() {
    finalCommand.execute();
  }

  @Override
  public void end(boolean interrupted) {
    finalCommand.end(interrupted);
  }

  @Override
  public boolean isFinished() {
    return finalCommand.isFinished();
  }
}
