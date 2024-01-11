package mrtjp.core.vec;

public class Rect {

    public static final Rect zeroRect = new Rect(Point.zeroPoint, Size.zeroSize);
    public static final Rect infiniteRect = new Rect(Point.infinitePoint.negate().divide(2), Size.infiniteSize);

    public Point origin;
    public Size size;

    public Rect(int x, int y, int width, int height) {
        this(new Point(x, y), new Size(width, height));
    }

    public Rect(Point min, Point max) {
        this(min, new Size(max.x - min.x, max.y - min.y));
    }

    public Rect(Rect r) {
        this(r.origin, r.size);
    }

    public Rect(Point origin, Size size) {
        this.origin = origin;
        this.size = size;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Rect) {
            return this.origin == ((Rect) obj).origin && this.size == ((Rect) obj).size;
        }
        return false;
    }

    public Rect copy() {
        return new Rect(origin, size);
    }

    public int x() {
        return origin.x;
    }
    public int y() {
        return origin.y;
    }
    public int width() {
        return size.width;
    }
    public int height() {
        return size.height;
    }

    public int maxX() {
        return x() + width();
    }
    public int maxY() {
        return y() + height();
    }
    public Point maxPoint() {
        return new Point(maxX(), maxY());
    }

    public int midX() {
        return x() + width() / 2;
    }
    public int midY() {
        return y() + height() / 2;
    }
    public Point midPoint() {
        return new Point(midX(), midY());
    }

    public boolean contains(Point p) {
        return p.x >= x() && p.y >= y() && p.x <= maxX() && p.y <= maxY();
    }
    public boolean contains(Rect rect) {
        return contains(rect.origin) && contains(rect.maxPoint());
    }
    public boolean intersects(Rect r) {
        return contains(r.origin) || contains(r.maxPoint()) || r.contains(origin) || r
            .contains(maxPoint());
    }

    public Rect enclose(Point p) {
        return new Rect(
            new Point(Math.min(x(), p.x), Math.min(y(), p.y)),
            new Point(Math.max(maxX(), p.x), Math.max(maxY(), p.y))
        );
    }
    public Rect union(Rect r) {
        return new Rect(
            new Point(Math.min(x(), r.x()), Math.min(y(), r.y())),
            new Point(Math.max(maxX(), r.maxX()), Math.max(maxY(), r.maxY()))
        );
    }
}
