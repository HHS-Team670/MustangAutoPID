package frc.team670.libs.commands;

import edu.wpi.first.wpilibj2.command.Command;

public class CopyCommand extends Command {

  protected Command command;

  protected void setCommand(Command command) {
    this.command = command;
    addRequirements(command.getRequirements());
  }

  @Override
  public void initialize() {
    command.initialize();
  }

  @Override
  public void execute() {
    command.execute();
  }

  @Override
  public boolean isFinished() {
    return command.isFinished();
  }

  @Override
  public void end(boolean interrupted) {
    command.end(interrupted);
  }
}
