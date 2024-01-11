package mrtjp.core.vec;

import java.util.Arrays;
import java.util.List;

public class Point {

    public int x;
    public int y;

    public Point(Vec2 vec2) {
        this((int) vec2.dx, (int) vec2.dy);
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            if (((Point) obj).x == x && ((Point) obj).y == y) {
                return true;
            }
        }
        return false;
    }

    public Point copy() {
        return new Point(x, y);
    }

    public Point add(int dx, int dy) {
        return new Point(x + dx, y + dy);
    }
    public Point subtract(int dx, int dy) {
        return add(-dx, -dy);
    }
    public Point multiply(int i, int j) {
        return new Point(x * i, y * j);
    }
    public Point divide(int i, int j) {
        return new Point(x / i, y / j);
    }

    public Point add(int d) {
        return add(d, d);
    }
    public Point subtract(int d) {
        return subtract(d, d);
    }
    public Point multiply(int k) {
        return multiply(k, k);
    }
    public Point divide(int k) {
        return divide(k, k);
    }

    public Point add(Point that) {
        return add(that.x, that.y);
    }
    public Point subtract(Point that) {
        return subtract(that.x, that.y);
    }
    public Point multiply(Point that) {
        return multiply(that.x, that.y);
    }
    public Point divide(Point that) {
        return divide(that.x, that.y);
    }

    public Point negate() {
        return new Point(-x, -y);
    }
    public Point invert() {
        return new Point(y, x);
    }
    public Vec2 vectorize() {
        return new Vec2(x, y);
    }

    public Point max(Point that) {
        return new Point(Math.max(x, that.x), Math.max(y, that.y));
    }
    public Point min(Point that) {
        return new Point(Math.min(x, that.x), Math.min(y, that.y));
    }

    public Point clamp(Rect rect) {
        return this.min(rect.maxPoint()).max(rect.origin);
    }
    public Point clamp(Size size) {
        return this.min(Point.apply(size)).max(Point.zeroPoint);
    }

    public Point offset(int r) {
        return offset(r, 1);
    }
    public Point offset(int r, int amount) {
        return this.add(Point.dirOffsets.get(r).multiply(amount));
    }

    /* Screw operator overloading. Here's the replacements
    def unary_- = negate
    def unary_~ = invert

    +(Point that) = add(that)
    -(Point that) = subtract(that)
    *(Point that) = multiply(that)
    /(Point that) = divide(that)

    +(int that) = add(that)
    -(int that) = subtract(that)
    *(int that) = multiply(that)
    /(int that) = divide(that)
    */

    @Override
    public String toString() {
        return "Point @[" + x + " " + y + "]";
    }

    public static final Point infinitePoint = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
    public static final Point zeroPoint = new Point(0, 0);

    public static Point apply(Size size) {
        return new Point(size.width, size.height);
    }

    private static final List<Point> dirOffsets = Arrays.asList(
        new Point(0, -1),
        new Point(1, 0),
        new Point(0, 1),
        new Point(-1, 0)
    );
}
