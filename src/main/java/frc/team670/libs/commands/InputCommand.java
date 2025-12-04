package frc.team670.libs.commands;

import edu.wpi.first.wpilibj2.command.Command;

public abstract class InputCommand extends Command {

  protected Command command;
  protected boolean firstFinished = false;

  public InputCommand() {}

  public InputCommand(Command mCommand) {
    setCommand(mCommand);
  }

  public void setCommand(Command command) {
    this.command = command;
    addRequirements(command.getRequirements());
  }

  public abstract Command mSupplier();

  @Override
  public void initialize() {
    command.initialize();
  }

  @Override
  public void execute() {
    if (!firstFinished && command.isFinished()) {
      Command newCommand = mSupplier();
      if (newCommand != null) {
        command.end(false);
        newCommand.initialize();
        addRequirements(newCommand.getRequirements());
        command = newCommand;
        firstFinished = true;
      }
    }
    if (!command.isFinished()) {
      command.execute();
    }
  }

  @Override
  public boolean isFinished() {
    return command.isFinished() && firstFinished;
  }

  @Override
  public void end(boolean interrupted) {
    command.end(interrupted);
  }
}
