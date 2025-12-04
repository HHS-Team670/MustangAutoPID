package frc.team670.libs.motors;

import frc.team670.libs.health.Health;

public abstract class MustangMotor<MotorType> {

  protected MotorType motor;

  public MustangMotor(MotorType motor) {
    this.motor = motor;
  }

  public MotorType getMotor() {
    return motor;
  }

  public abstract void setTarget(double rotations, double feedforward);

  public abstract void setSpeed(double speed);

  public abstract void setEncoderPosition(double rotations);

  public abstract void toggleIdleMode();

  public abstract double getRotations();

  public abstract double getVelocity();

  public abstract double getSpeed();

  public abstract double getCurrent();

  public abstract Health checkHealth();

  public abstract void setPID(double p, double i, double d);

  public abstract void setFF(double s, double v, double a, double g);
}
