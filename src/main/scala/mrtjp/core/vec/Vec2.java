package mrtjp.core.vec;

public class Vec2 {

    public double dx;
    public double dy;

    public Vec2(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vec2) {
            return this.dx == ((Vec2) obj).dx && this.dy == ((Vec2) obj).dy;
        }
        return false;
    }

    public Vec2 copy() {
        return new Vec2(dx, dy);
    }

    public Vec2 add(double dx0, double dy0) {
        return new Vec2(dx + dx0, dy + dy0);
    }
    public Vec2 subtract(double dx0, double dy0) {
        return add(-dx0, -dy0);
    }
    public Vec2 multiply(double i, double j) {
        return new Vec2(dx * i, dy * j);
    }
    public Vec2 divide(double i, double j) {
        return multiply(1 / i, 1 / j);
    }
    public double dot(double i, double j) {
        return dx * i + dy * j;
    }

    public Vec2 add(double d) {
        return add(d, d);
    }
    public Vec2 subtract(double d) {
        return subtract(d, d);
    }
    public Vec2 multiply(double k) {
        return multiply(k, k);
    }
    public Vec2 divide(double k) {
        return divide(k, k);
    }
    public double dot(double k) {
        return dot(k, k);
    }

    public Vec2 add(Vec2 that) {
        return add(that.dx, that.dy);
    }
    public Vec2 subtract(Vec2 that) {
        return subtract(that.dx, that.dy);
    }
    public Vec2 multiply(Vec2 that) {
        return multiply(that.dx, that.dy);
    }
    public Vec2 divide(Vec2 that) {
        return divide(that.dx, that.dy);
    }
    public double dot(Vec2 that) {
        return dot(that.dx, that.dy);
    }

    public double magSquared() {
        return dx * dx + dy * dy;
    }
    public double mag() {
        return Math.sqrt(magSquared());
    }

    public Vec2 normalize() {
        if (mag() == 0)
            return Vec2.zeroVec;
        return this.divide(mag());
    }
    public Vec2 negate() {
        return new Vec2(-dx, -dy);
    }
    public Vec2 invert() {
        return new Vec2(dy, dx);
    }

    public Vec2 project(Vec2 that) {
        return that.multiply(this.dot(that) / that.magSquared());
    }
    public Vec2 reject(Vec2 that) {
        return this.subtract(project(that));
    }
    public Vec2 axialProject() {
        if (Math.abs(dx) > Math.abs(dy))
            return new Vec2(dx, 0);
        return new Vec2(0, dy);
    }

    /* Screw operator overloading. Here's the replacements
    def unary_- = negate
    def unary_~ = invert

    def +(that: Vec2) = add(that)
    def -(that: Vec2) = subtract(that)
    def *(that: Vec2) = multiply(that)
    def /(that: Vec2) = divide(that)

    def +(that: Double) = add(that)
    def -(that: Double) = subtract(that)
    def *(that: Double) = multiply(that)
    def /(that: Double) = divide(that)
    */

    @Override
    public String toString() {
        return "Vec2 @[" + dx + " " + dy + "]";
    }

    public static final Vec2 zeroVec = new Vec2(0, 0);
    public static final Vec2 up = new Vec2(0, -1);
    public static final Vec2 right = new Vec2(1, 0);
    public static final Vec2 down = new Vec2(0, 1);
    public static final Vec2 left = new Vec2(-1, 0);
}
