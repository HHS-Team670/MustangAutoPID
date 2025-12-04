package frc.team670.libs.motors;

import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import frc.team670.libs.health.Health;

public class TalonMotor extends MustangMotor<TalonFX> {

  public TalonMotor(TalonFX motor) {
    super(motor);
  }

  @Override
  public void setTarget(double rotations, double feedforward) {
    motor.setControl(
        new MotionMagicVoltage(0).withPosition(rotations).withFeedForward(feedforward));
  }

  @Override
  public void setSpeed(double speed) {
    motor.set(speed);
  }

  @Override
  public void setEncoderPosition(double rotations) {
    motor.setPosition(rotations);
  }

  @Override
  public double getRotations() {
    return motor.getPosition().getValueAsDouble();
  }

  @Override
  public double getSpeed() {
    return motor.get();
  }

  @Override
  public double getCurrent() {
    return motor.getStatorCurrent().getValueAsDouble();
  }

  @Override
  public Health checkHealth() {
    if (motor.isAlive()) {
      return Health.GREEN;
    }
    return Health.RED;
  }

  @Override
  public void toggleIdleMode() {
    MotorOutputConfigs out = new MotorOutputConfigs();
    motor.getConfigurator().refresh(out);
    if (out.NeutralMode == NeutralModeValue.Brake) {
      motor.setNeutralMode(NeutralModeValue.Brake);
    } else {
      motor.setNeutralMode(NeutralModeValue.Coast);
    }
  }

  @Override
  public double getVelocity() {
    return motor.getVelocity().getValueAsDouble();
  }

  @Override
  public void setPID(double p, double i, double d) {
    TalonFXConfiguration config = MotorUtils.getConfig(motor);
    config.Slot0.kP = p;
    config.Slot0.kI = i;
    config.Slot0.kD = d;
    MotorUtils.applyConfig(motor, config);
  }

  @Override
  public void setFF(double s, double v, double a, double g) {
    TalonFXConfiguration config = MotorUtils.getConfig(motor);
    config.Slot0.kS = s;
    config.Slot0.kV = v;
    config.Slot0.kA = a;
    config.Slot0.kG = g;
    MotorUtils.applyConfig(motor, config);
  }
}
