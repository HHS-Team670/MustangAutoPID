package frc.team670.libs.health;

import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.team670.libs.logging.MustangNotifier;
import java.util.HashMap;
import java.util.Map;

public final class HealthChecker {

  private HealthChecker() {}

  public static Map<Subsystem, Health> healthStates = new HashMap<>();

  public static void reportHealth(Subsystem subsytem, Health state) {
    if (healthStates.get(subsytem) == null) {
      MustangNotifier.warning(
          subsytem, " Attempted to report health while not registered\n Reported Health: " + state);
    } else {
      healthStates.put(subsytem, state);
    }
  }

  public static Void register(Subsystem subsystem) {
    healthStates.put(subsystem, Health.UNKNOWN);
    return null;
  }

  public static void periodic() {
    healthStates.forEach(
        (Subsystem s, Health h) -> {
          if (h == Health.RED) {
            CommandScheduler.getInstance().unregisterSubsystem(s);
          }
          MustangNotifier.log(s.getName() + "/Health", h);
        });
  }
}
