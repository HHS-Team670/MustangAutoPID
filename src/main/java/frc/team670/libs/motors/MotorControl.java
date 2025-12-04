package frc.team670.libs.motors;

import java.util.function.Supplier;

public class MotorControl {
  private double setpoint;
  private double subsystemOffsets;
  private Supplier<Double> positionOffsets;

  public MotorControl() {
    this.setpoint = 0.0;
    this.subsystemOffsets = 0.0;
    this.positionOffsets = () -> 0.0;
  }

  public double getSetpoint() {
    return setpoint;
  }

  /**
   * used for the raw positon of the target without any extra offsets
   *
   * @param setpoint the desired position setpoint for the motor
   */
  public void setSetpoint(double setpoint) {
    this.setpoint = setpoint;
  }

  public double getSubsystemOffsets() {
    return subsystemOffsets;
  }

  /**
   * used for compensating for any possible issues that may occur mid match for example: an Arm's
   * chain skipping would cause the arm to be off by a few degrees for all positions so we can use
   * this to adjust all setpoints by a constant value
   *
   * @param subsystemOffsets the desired subsytem offset for the motor
   */
  public void setSubsystemOffsets(double subsystemOffsets) {
    this.subsystemOffsets = subsystemOffsets;
  }

  public Supplier<Double> getPositionOffsets() {
    return positionOffsets;
  }

  /**
   * used for offsetting at specific positions during tunning for example when we get to a comp we
   * can use calibration time to go to the feild and test all of your positions and see if any need
   * small adjustments we can use this to supply those adjustments without changing the setpoints
   * until we get to a desired value then we can adjust the setpoint accordingly
   *
   * @param positionOffsets the desired position offset supplier for the motor
   */
  public void setPositionOffsets(Supplier<Double> positionOffsets) {
    this.positionOffsets = positionOffsets;
  }
}
