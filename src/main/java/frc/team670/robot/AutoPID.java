package frc.team670.robot;

import java.util.function.BiConsumer;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj.Timer;
import frc.team670.libs.motors.MotorUtils;
import frc.team670.libs.motors.MustangMotor;

public class AutoPID {

  public enum MechanismType {
    LINEAR,
    ROTATIONAL
  }

  public static AutoPID tuneSparkMax(int id) {
    AutoPID a = new AutoPID();
    a.motor = MotorUtils.wrappedMotor(id, new SparkMaxConfig());
    a.motorId = id;
    return a;
  }

  public static AutoPID tuneTalonFx(int id) {
    AutoPID a = new AutoPID();
    a.motor = MotorUtils.wrappedMotor(id, new TalonFXConfiguration());
    a.motorId = id;
    return a;
  }

  public static AutoPID tuneTest(int id) {
    AutoPID a = new AutoPID();
    a.motor = new TestMotor(id);
    a.motorId = id;
    return a;
  }

  private MustangMotor<?> motor;
  private int motorId;
  private BiConsumer<AutoPID, Integer> finishCallback;
  private final double[] limits = new double[3];

  private MechanismType mechType = MechanismType.LINEAR;

  private enum Stage {
    P_SWEEP,
    I_SWEEP,
    D_SWEEP,
    FF_S_SWEEP,
    FF_V_SWEEP,
    FF_A_SWEEP,
    FF_G_SWEEP,
    MOVE_TO_END,
    FINISHED,
    TEST_WAIT
  }

  private Stage stage = Stage.P_SWEEP;
  private Stage pendingStageAfterTest = Stage.P_SWEEP;

  private final double[] pSweep = { 0.0005, 0.001, 0.002, 0.005, 0.01 };
  private final double[] iSweep = { 0.0, 0.00005, 0.0002, 0.0005 };
  private final double[] dSweep = { 0.0, 0.01, 0.05, 0.1 };

  private final double[] sSweep = { 0.0, 0.02, 0.05, 0.08, 0.12 };
  private final double[] vSweep = { 0.0, 0.2, 0.5, 1.0 };
  private final double[] aSweep = { 0.0, 0.05, 0.1, 0.2 };
  private final double[] gSweep = { 0.0, 0.05, 0.1, 0.2 };

  private int pIdx = 0, iIdx = 0, dIdx = 0;
  private int sIdx = 0, vIdx = 0, aIdx = 0, gIdx = 0;

  /** tuned values in order: {P, I, D, S, V, A, G} */
  private final double[] bestValues = new double[7];

  private double bestScore = Double.MAX_VALUE;

  private final Timer timer = new Timer();
  private double testTarget = 0.0;
  private double observedWorstError = 0.0;
  private final double testWindowSeconds = 0.35;

  private double positionTolerance = 0.02;
  private double velocityThreshold = 0.5;

  private boolean started = false;
  private boolean aborted = false;

  private AutoPID() {
  }

  public void start(
      double lowerLimit, double upperLimit, double endingPos, MechanismType mechType) {
    this.mechType = (mechType == null) ? MechanismType.LINEAR : mechType;

    limits[0] = lowerLimit;
    limits[1] = upperLimit;
    limits[2] = endingPos;

    pIdx = iIdx = dIdx = 0;
    sIdx = vIdx = aIdx = gIdx = 0;
    bestScore = Double.MAX_VALUE;

    stage = Stage.P_SWEEP;
    pendingStageAfterTest = Stage.P_SWEEP;

    testTarget = (limits[0] + limits[1]) / 2.0;

    timer.reset();
    timer.start();

    started = true;
    aborted = false;
    motor.setPID(0, 0, 0);
    motor.setFF(0, 0, 0, 0);
    motor.setTarget(testTarget, 0);
    stage = Stage.TEST_WAIT;
    pendingStageAfterTest = Stage.P_SWEEP;
    observedWorstError = 0.0;
  }

  public void onFinish(BiConsumer<AutoPID, Integer> callback) {
    this.finishCallback = callback;
  }

  public void loop() {
    if (!started || aborted || stage == Stage.FINISHED) {
      return;
    }

    double pos = motor.getRotations();
    if (pos < limits[0] || pos > limits[1]) {
      MotorUtils.holdMotor(motor);
      aborted = true;
      stage = Stage.FINISHED;
      if (finishCallback != null) {
        finishCallback.accept(this, motorId);
      }
      return;
    }

    if (stage == Stage.TEST_WAIT) {
      testWaitUpdate();
      return;
    }

    switch (stage) {
      case P_SWEEP:
        startPSweepTest();
        break;
      case I_SWEEP:
        startISweepTest();
        break;
      case D_SWEEP:
        startDSweepTest();
        break;
      case FF_S_SWEEP:
        startFSweepTest();
        break;
      case FF_V_SWEEP:
        startVSweepTest();
        break;
      case FF_A_SWEEP:
        startASweepTest();
        break;
      case FF_G_SWEEP:
        startGSweepTest();
        break;
      case MOVE_TO_END:
        moveToFinalPosition();
        break;
      default:
        break;
    }
  }

  private void testWaitUpdate() {
    double pos = motor.getRotations();
    double err = Math.abs(testTarget - pos);
    observedWorstError = Math.max(observedWorstError, err);

    if (pos < limits[0] || pos > limits[1]) {
      observedWorstError = 1e9;
    }

    if (timer.hasElapsed(testWindowSeconds)) {
      evaluatePendingTestAndAdvance();
    }
  }

  private void startPSweepTest() {
    if (pIdx >= pSweep.length) {
      stage = Stage.I_SWEEP;
      return;
    }
    double kp = pSweep[pIdx];
    motor.setPID(kp, bestValues[1], bestValues[2]);
    motor.setFF(bestValues[3], bestValues[4], bestValues[5], bestValues[6]);

    beginObservation(Stage.P_SWEEP);
  }

  private void startISweepTest() {
    if (iIdx >= iSweep.length) {
      stage = Stage.D_SWEEP;
      return;
    }
    double ki = iSweep[iIdx];
    motor.setPID(bestValues[0], ki, bestValues[2]);
    motor.setFF(bestValues[3], bestValues[4], bestValues[5], bestValues[6]);

    beginObservation(Stage.I_SWEEP);
  }

  private void startDSweepTest() {
    if (dIdx >= dSweep.length) {
      stage = Stage.FF_S_SWEEP;
      return;
    }
    double kd = dSweep[dIdx];
    motor.setPID(bestValues[0], bestValues[1], kd);
    motor.setFF(bestValues[3], bestValues[4], bestValues[5], bestValues[6]);

    beginObservation(Stage.D_SWEEP);
  }

  private void startFSweepTest() {
    if (sIdx >= sSweep.length) {
      stage = Stage.FF_V_SWEEP;
      return;
    }
    double ks = sSweep[sIdx];
    motor.setPID(bestValues[0], bestValues[1], bestValues[2]);
    motor.setFF(ks, bestValues[4], bestValues[5], bestValues[6]);

    beginObservation(Stage.FF_S_SWEEP);
  }

  private void startVSweepTest() {
    if (vIdx >= vSweep.length) {
      stage = Stage.FF_A_SWEEP;
      return;
    }
    double kv = vSweep[vIdx];
    motor.setPID(bestValues[0], bestValues[1], bestValues[2]);
    motor.setFF(bestValues[3], kv, bestValues[5], bestValues[6]);

    beginObservation(Stage.FF_V_SWEEP);
  }

  private void startASweepTest() {
    if (aIdx >= aSweep.length) {
      if (mechType == MechanismType.ROTATIONAL) {
        stage = Stage.FF_G_SWEEP;
      } else {
        applyBestAndMoveToEnd();
      }
      return;
    }
    double ka = aSweep[aIdx];
    motor.setPID(bestValues[0], bestValues[1], bestValues[2]);
    motor.setFF(bestValues[3], bestValues[4], ka, bestValues[6]);

    beginObservation(Stage.FF_A_SWEEP);
  }

  private void startGSweepTest() {
    if (gIdx >= gSweep.length) {
      applyBestAndMoveToEnd();
      return;
    }
    double kg = gSweep[gIdx];
    motor.setPID(bestValues[0], bestValues[1], bestValues[2]);
    motor.setFF(bestValues[3], bestValues[4], bestValues[5], kg);

    beginObservation(Stage.FF_G_SWEEP);
  }

  private void beginObservation(Stage testingStage) {
    testTarget = (limits[0] + limits[1]) / 2.0;
    motor.setTarget(testTarget, 0);

    observedWorstError = 0.0;
    pendingStageAfterTest = testingStage;
    timer.restart();
    stage = Stage.TEST_WAIT;
  }

  private void evaluatePendingTestAndAdvance() {
    double score = observedWorstError;

    if (score < bestScore) {
      bestScore = score;

      switch (pendingStageAfterTest) {
        case P_SWEEP:
          bestValues[0] = pSweep[pIdx];
          break;
        case I_SWEEP:
          bestValues[1] = iSweep[iIdx];
          break;
        case D_SWEEP:
          bestValues[2] = dSweep[dIdx];
          break;
        case FF_S_SWEEP:
          bestValues[3] = sSweep[sIdx];
          break;
        case FF_V_SWEEP:
          bestValues[4] = vSweep[vIdx];
          break;
        case FF_A_SWEEP:
          bestValues[5] = aSweep[aIdx];
          break;
        case FF_G_SWEEP:
          bestValues[6] = gSweep[gIdx];
          break;
        default:
          break;
      }
    }

    switch (pendingStageAfterTest) {
      case P_SWEEP:
        pIdx++;
        if (pIdx >= pSweep.length) {
          stage = Stage.I_SWEEP;
        } else {
          stage = Stage.P_SWEEP;
        }
        break;
      case I_SWEEP:
        iIdx++;
        if (iIdx >= iSweep.length) {
          stage = Stage.D_SWEEP;
        } else {
          stage = Stage.I_SWEEP;
        }
        break;
      case D_SWEEP:
        dIdx++;
        if (dIdx >= dSweep.length) {
          stage = Stage.FF_S_SWEEP;
        } else {
          stage = Stage.D_SWEEP;
        }
        break;
      case FF_S_SWEEP:
        sIdx++;
        if (sIdx >= sSweep.length) {
          stage = Stage.FF_V_SWEEP;
        } else {
          stage = Stage.FF_S_SWEEP;
        }
        break;
      case FF_V_SWEEP:
        vIdx++;
        if (vIdx >= vSweep.length) {
          stage = Stage.FF_A_SWEEP;
        } else {
          stage = Stage.FF_V_SWEEP;
        }
        break;
      case FF_A_SWEEP:
        aIdx++;
        if (aIdx >= aSweep.length) {
          if (mechType == MechanismType.ROTATIONAL) {
            stage = Stage.FF_G_SWEEP;
          } else {
            applyBestAndMoveToEnd();
            return;
          }
        } else {
          stage = Stage.FF_A_SWEEP;
        }
        break;
      case FF_G_SWEEP:
        gIdx++;
        if (gIdx >= gSweep.length) {
          applyBestAndMoveToEnd();
          return;
        } else {
          stage = Stage.FF_G_SWEEP;
        }
        break;
      default:
        stage = Stage.FINISHED;
        if (finishCallback != null) {
          finishCallback.accept(this, motorId);
        }
        break;
    }

    if (bestScore > 1e8 && stage == Stage.FINISHED) {
      aborted = true;
      MotorUtils.holdMotor(motor);
    }
  }

  private void applyBestAndMoveToEnd() {
    motor.setPID(bestValues[0], bestValues[1], bestValues[2]);
    motor.setFF(bestValues[3], bestValues[4], bestValues[5], bestValues[6]);

    stage = Stage.MOVE_TO_END;
  }

  private void moveToFinalPosition() {
    double target = limits[2];
    target = Math.max(limits[0], Math.min(limits[1], target));
    motor.setTarget(target, 0);

    double pos = motor.getRotations();
    double vel = motor.getVelocity();

    if (Math.abs(pos - target) < positionTolerance && Math.abs(vel) < velocityThreshold) {
      stage = Stage.FINISHED;
      motor.setPID(bestValues[0], bestValues[1], bestValues[2]);
      motor.setFF(bestValues[3], bestValues[4], bestValues[5], bestValues[6]);
      if (finishCallback != null) {
        finishCallback.accept(this, motorId);
      }
    }
  }

  public boolean isFinished() {
    return stage == Stage.FINISHED;
  }

  public boolean isAborted() {
    return aborted;
  }

  public double[] getTunedValues() {
    return bestValues.clone();
  }

  public String getStageName() {
    return stage.name();
  }
}
