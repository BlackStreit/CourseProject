package sample.Util;

import java.time.Instant;

public class TimeClass {
    //Время первой атаки
    public static Instant firstAttackTime;
    //Время поледнего врага
    public static Instant lastUpdate = null;
    //Время последнего получения урона
    public static Instant lastDamageTime = Instant.now();
    //Время последней атаки
    public static Instant lastAttackTime = null;
}
