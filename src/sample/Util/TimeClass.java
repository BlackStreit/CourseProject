package sample.Util;

import java.time.Instant;

public class TimeClass {
    public static Instant firstAttackTime;
    public static Instant lastUpdate = null;
    public static Instant lastDamageTime = Instant.now();
    public static Instant lastAttackTime = null;
}
