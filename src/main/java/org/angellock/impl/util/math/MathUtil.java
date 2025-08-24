package org.angellock.impl.util.math;

public class MathUtil {
    public static double getDistance(double x1, double y1, double z1, double x2, double y2, double z2){
        return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) + (z1-z2)*(z1-z2));
    }

    public static double getVelocity(double x1, double y1, double z1){
        return Math.sqrt(x1*x1 + y1*y1 + z1*z1);
    }
}
