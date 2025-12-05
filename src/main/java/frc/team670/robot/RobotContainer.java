// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.team670.robot;

import frc.team670.libs.logging.MustangNotifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RobotContainer {

  private static List<TestMotor> motors = new ArrayList<>();
  private List<AutoPID<?>> tuners = new ArrayList<>();

  public RobotContainer() {
    AutoPID<TestMotor> tuner = AutoPID.tuneTest(25);
    tuner.start(80, 0.1, 0.005, 0.005, 0.005);
    tuner.onFinish(this::handleFinish);
    tuners.add(tuner);
  }

  private void handleFinish(AutoPID<?> tuner) {
    MustangNotifier.log("AutoTuner/MotorId", tuner.getTunedValues());
    System.out.println(Arrays.toString(tuner.getTunedValues()));
  }

  public static void add(TestMotor motor) {
    motors.add(motor);
  }

  public void periodic() {

    for (TestMotor motor : motors) {
      motor.updateSimulation();
    }
    for (AutoPID<?> tuner : tuners) {

      tuner.loop();
      // System.out.println(Arrays.toString(tuner.getTunedValues()));
    }
  }
}
