package frc.team670.libs.subsystems;

import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.team670.libs.health.Health;
import frc.team670.libs.health.HealthChecker;
import frc.team670.libs.logging.MustangNotifier;
import frc.team670.libs.motors.MotorControl;
import frc.team670.libs.motors.MustangMotor;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class MotorizedSubsystem implements DebugSubsystem, HealthySubsystem, Subsystem {

  private List<MustangMotor<?>> motors = new ArrayList<>();
  private List<MotorControl> motorControls = new ArrayList<>();
  private boolean useDefaultLogs = true;

  /**
   * a getter for the motors array
   *
   * @return List of motors registered to this subsystem
   */
  public List<MustangMotor<?>> getMotors() {
    return motors;
  }

  /**
   * a getter for the first motor in the motors array
   *
   * @return gets the first motor that was registered with this subsytem
   */
  public MustangMotor<?> getMotor() {
    return getMotor(0);
  }

  /**
   * the resulting motor is the one that was registered in order starting at 0
   *
   * @param index the index of the motor to get
   * @return the motor at the specified index
   */
  public MustangMotor<?> getMotor(int index) {
    MustangMotor<?> motor = motors.get(index);
    if (motor == null) {
      MustangNotifier.error(
          "Motor at index "
              + index
              + " is not registered. for subsytem: "
              + getName()
              + ". Motor array size = "
              + motors.size());
      throw new IndexOutOfBoundsException();
    }
    return motor;
  }

  /**
   * Registers motors to this subsystem. Must be called in the constructor of the subsytem.
   *
   * @param motor the motor(s) to register
   */
  protected void registerMotors(MustangMotor<?>... motor) {
    for (MustangMotor<?> m : motor) {
      motors.add(m);
      motorControls.add(new MotorControl());
    }
  }

  /** Enables default logging for this subsystem see {@link logDefaults()} */
  public void useDefaultLogs() {
    useDefaultLogs = true;
  }

  /** Disables default logging for this subsystem see {@link logDefaults()} */
  public void removeDefaultLogs() {
    useDefaultLogs = false;
  }

  /** Sets all motors to their setpoints */
  public void moveAllMotorsToSetpoints() {
    int numberOfMotors = motors.size();
    for (int i = 0; i < numberOfMotors; i++) {
      setMotorTargetToSetpoint(i);
    }
  }

  /**
   * @return the position of the first motor in rotations
   */
  public double getMotorPosition() {
    return getMotorPosition(0);
  }

  /**
   * @param index the index of the motor to get the position of
   * @return the position of the motor at the specified index in rotations
   */
  public double getMotorPosition(int index) {
    return getMotor(index).getRotations();
  }

  /**
   * @return the speed of the first motor between -1 and 1
   */
  public double getMotorSpeed() {
    return getMotorSpeed(0);
  }

  /**
   * @param index the index of the motor to get the speed of
   * @return the speed of the motor at the specified index between -1 and 1
   */
  public double getMotorSpeed(int index) {
    return getMotor(index).getSpeed();
  }
  ;

  /**
   * @return the current of the first motor in amps
   */
  public double getMotorCurrent() {
    return getMotorCurrent(0);
  }

  /**
   * @param index the index of the motor to get the current of
   * @return the current of the motor at the specified index in amps
   */
  public double getMotorCurrent(int index) {
    return getMotor(index).getCurrent();
  }
  ;

  /**
   * @return the raw setpoint of the first motor in rotations
   */
  public double getRawSetpoint() {
    return getRawSetpoint(0);
  }

  /**
   * @param index the index of the motor to get the raw setpoint ofs
   * @return the raw setpoint of the motor at the specified index in rotations
   */
  public double getRawSetpoint(int index) {
    return motorControls.get(index).getSetpoint();
  }

  /**
   * @param rawSetpoint the desired raw setpoint for the first motor in rotations
   */
  public void setRawSetpoint(double rawSetpoint) {
    setRawSetpoint(0, rawSetpoint);
  }

  /**
   * @param index the index of the motor to set the raw setpoint ofs
   * @param rawSetpoint the desired raw setpoint for the motor at the specified index
   */
  public void setRawSetpoint(int index, double rawSetpoint) {
    motorControls.get(index).setSetpoint(rawSetpoint);
  }

  /**
   * @return the subsystem offset of the first motor in rotationss
   */
  public double getSubsystemOffset() {
    return getSubsystemOffset(0);
  }

  /**
   * @param index the index of the motor to get the subsystem offset ofs
   * @return the subsystem offset of the motor at the specified index in rotations
   */
  public double getSubsystemOffset(int index) {
    return motorControls.get(index).getSubsystemOffsets();
  }

  /**
   * @param subsystemOffset the desired subsystem offset for the first motor in rotations
   */
  public void setSubsystemOffset(double subsystemOffset) {
    setSubsystemOffset(0, subsystemOffset);
  }

  /**
   * @param index the index of the motor to set the subsystem offset ofs
   * @param subsystemOffset the desired subsystem offset for the motor at the specified index in
   *     rotations
   */
  public void setSubsystemOffset(int index, double subsystemOffset) {
    motorControls.get(index).setSubsystemOffsets(subsystemOffset);
  }

  /**
   * @param offsetIncrement the desired increment to add to the subsystem offset for the first motor
   *     in rotations
   */
  public void addSubsystemOffset(double offsetIncrement) {
    addSubsystemOffset(0, offsetIncrement);
  }

  /**
   * @param index the index of the motor to add the subsystem offset to
   * @param offsetIncrement the desired increment to add to the subsystem offset for the motor
   */
  public void addSubsystemOffset(int index, double offsetIncrement) {
    motorControls
        .get(index)
        .setSubsystemOffsets(motorControls.get(index).getSubsystemOffsets() + offsetIncrement);
  }

  /**
   * @return the position offset of the first motor in rotations
   */
  protected double getPositionOffset() {
    return getPositionOffset(0);
  }

  /**
   * @param index the index of the motor to get the position offset to
   * @return the position offset of the motor at the specified index in rotations
   */
  public double getPositionOffset(int index) {
    return motorControls.get(index).getPositionOffsets().get();
  }

  /**
   * @param positionOffset the desired position offset supplier for the first motor in rotations
   */
  protected void setPositionOffset(Supplier<Double> positionOffset) {
    setPositionOffset(0, positionOffset);
  }

  /**
   * @param index the index of the motor to set the position offset to
   * @param positionOffset the desired position offset supplier for the motor at the
   */
  public void setPositionOffset(int index, Supplier<Double> positionOffset) {
    motorControls.get(index).setPositionOffsets(positionOffset);
  }

  /**
   * @return the total setpoint of the first motor in rotations
   */
  protected double getTotalSetpoint() {
    return getTotalSetpoint(0);
  }

  /**
   * @param index the index of the motor to get the total setpoint to
   * @return the total setpoint of the motor at the specified index in rotations
   */
  protected double getTotalSetpoint(int index) {
    return getRawSetpoint(index) + getSubsystemOffset(index) + getPositionOffset(index);
  }

  /**
   * @param speed the desired speed for the first motor between -1 and 1
   */
  protected void setMotorSpeed(double speed) {
    setMotorSpeed(0, speed);
  }

  /**
   * @param index the index of the motor to set the speed to
   * @param speed the desired speed for the motor at the specified index between -1 and 1
   */
  protected void setMotorSpeed(int index, double speed) {
    getMotor(index).setSpeed(speed);
  }

  /**
   * @param rotations the desired target rotations for the first motor
   */
  protected void setMotorTargetRotations(double rotations) {
    setMotorTargetRotations(0, rotations);
  }

  /**
   * @param index the index of the motor to set the target rotations to
   * @param rotations the desired target rotations for the motor at the specified index
   */
  protected void setMotorTargetRotations(int index, double rotations) {
    getMotor(index).setTarget(rotations, 0);
  }

  /** Sets the motor at index to its setpoint */
  protected void setMotorTargetToSetpoint() {
    setMotorTargetToSetpoint(0);
  }

  /**
   * @param index the index of the motor to set to its setpoint
   */
  protected void setMotorTargetToSetpoint(int index) {
    getMotor(index).setTarget(getTotalSetpoint(index), 0);
  }
  ;

  /**
   * @param feedForward the desired feedforward for the first motor
   */
  protected void setMotorTargetToSetpointWithFeedForward(double feedForward) {
    setMotorTargetToSetpointWithFeedForward(0, feedForward);
  }

  /**
   * @param index the index of the motor to set to its setpoint
   * @param feedForward the desired feedforward for the motor at the specified index
   */
  protected void setMotorTargetToSetpointWithFeedForward(int index, double feedForward) {
    getMotor(index).setTarget(getTotalSetpoint(index), feedForward);
  }
  ;

  /** Toggles the idle mode of all motors in the subsystem */
  public void toggleIdleMode() {
    for (MustangMotor<?> motor : motors) {
      motor.toggleIdleMode();
    }
  }

  /*
   * DO NOT OVERRIDE!!!!! Use subsystemLoop instead
   */
  @Override
  public final void periodic() {
    subsystemLoop();
    manageMotorMovement();
    MustangNotifier.setContext(this);
    logDefaults();
    debugSubsystem();
    MustangNotifier.clearContext();
    HealthChecker.reportHealth(this, checkHealth());
  }

  private void logDefaults() {
    if (!useDefaultLogs) {
      return;
    }
    for (int i = 0; i < motorControls.size(); i++) {
      String indexLog = (i == 0) ? "" : ("" + i);
      MustangNotifier.log("rawSetpointRotation" + indexLog, getRawSetpoint(i));
      MustangNotifier.log("currentRotations" + indexLog, getMotorPosition(i));
      MustangNotifier.log("totalSetpointRotations" + indexLog, getTotalSetpoint(i));
      MustangNotifier.log("subsystemOffsetRotations" + indexLog, getSubsystemOffset(i));
      MustangNotifier.log("current" + indexLog, getMotorCurrent(i));
    }
  }

  @Override
  /**
   * @return the health status of the subsystem
   */
  public Health checkHealth() {
    for (MustangMotor<?> motor : motors) {
      if (motor.checkHealth() != Health.GREEN) {
        return motor.checkHealth();
      }
    }
    return Health.GREEN;
  }

  protected abstract void subsystemLoop();

  protected abstract void manageMotorMovement();
}
