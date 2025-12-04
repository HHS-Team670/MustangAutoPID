package frc.team670.libs.motors;

import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.Faults;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import frc.team670.libs.health.Health;
import frc.team670.libs.logging.MustangNotifier;

public class SparkMotor extends MustangMotor<SparkMax> {

  public SparkMotor(SparkMax motor) {
    super(motor);
  }

  @Override
  public void setTarget(double rotations, double feedforward) {
    motor
        .getClosedLoopController()
        .setReference(rotations, ControlType.kPosition, ClosedLoopSlot.kSlot0, feedforward);
  }

  @Override
  public void setSpeed(double speed) {
    motor.set(speed);
  }

  @Override
  public double getRotations() {
    return motor.getEncoder().getPosition();
  }

  @Override
  public void setEncoderPosition(double rotations) {
    motor.getEncoder().setPosition(rotations);
  }

  @Override
  public double getSpeed() {
    return motor.get();
  }

  @Override
  public double getCurrent() {
    return motor.getOutputCurrent();
  }

  @Override
  public Health checkHealth() {
    Faults faults = motor.getFaults();
    if (faults.escEeprom || faults.can || faults.gateDriver || faults.sensor) {
      return Health.RED;
    } else if (faults.temperature || faults.other) {
      return Health.YELLOW;
    } else if (faults.firmware) {
      MustangNotifier.message("Spark Motor Firmware Out of Date");
    }
    return Health.GREEN;
  }

  @Override
  public void toggleIdleMode() {
    IdleMode currentMode = motor.configAccessor.getIdleMode();
    if (currentMode == IdleMode.kCoast) {
      motor.configure(
          new SparkMaxConfig().idleMode(IdleMode.kBrake),
          ResetMode.kNoResetSafeParameters,
          PersistMode.kNoPersistParameters);
    } else {
      motor.configure(
          new SparkMaxConfig().idleMode(IdleMode.kCoast),
          ResetMode.kNoResetSafeParameters,
          PersistMode.kNoPersistParameters);
    }
  }

  @Override
  public double getVelocity() {
    return motor.getEncoder().getVelocity();
  }

  @Override
  public void setPID(double p, double i, double d) {
    SparkMaxConfig config = new SparkMaxConfig();
    config.closedLoop.p(p).i(i).d(d);
    motor.configure(config, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);
  }

  @Override
  public void setFF(double s, double v, double a, double g) {
    double ff = s * Math.signum(getVelocity()) + v * getVelocity() + g;
    SparkMaxConfig config = new SparkMaxConfig();
    config.closedLoop.velocityFF(ff);
    motor.configure(config, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);
  }
}
