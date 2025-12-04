// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.team670.robot;

import java.util.ArrayList;
import java.util.List;

import frc.team670.libs.logging.MustangNotifier;
import frc.team670.robot.AutoPID.MechanismType;

public class RobotContainer {

  private static List<TestMotor> motors = new ArrayList<>();
  private List<AutoPID> tuners = new ArrayList<>();

  public RobotContainer() {
    AutoPID tuner = AutoPID.tuneTest(25);
    tuner.start(0, 500, 42, MechanismType.LINEAR);
    tuner.onFinish(this::handleFinish);
    tuners.add(tuner);
  }

  private void handleFinish(AutoPID tuner, int motorId) {
    MustangNotifier.log(String.format("AutoTuner/MotorId%d"), tuner.getTunedValues());
  }

  public static void add(TestMotor motor) {
    motors.add(motor);
  }

  public void periodic() {

    for (TestMotor motor : motors) {
      motor.updateSimulation();
    }
    for (AutoPID tuner : tuners) {
      if (tuner.isFinished()) {
        continue;
      }
      tuner.loop();
    }
  }
}
