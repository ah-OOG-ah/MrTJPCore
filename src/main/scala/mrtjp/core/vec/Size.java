package mrtjp.core.vec;

public class Size {

    public static final Size zeroSize = new Size(0, 0);
    public static final Size infiniteSize = new Size(Integer.MAX_VALUE, Integer.MAX_VALUE);

    public int width;
    public int height;

    public Size(Point point) {
        this(point.x, point.y);
    }

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Size) {
            return this.width == ((Size) obj).width && this.height == ((Size) obj).height;
        }
        return false;
    }

    public Size negate() {
        return new Size(-width, -height);
    }
    public Size invert() {
        return new Size(height, width);
    }

    public Size add(int dw, int dh) {
        return new Size(width + dw, height + dh);
    }
    public Size subtract(int dw, int dh) {
        return add(-dw, -dh);
    }
    public Size multiply(int i, int j) {
        return new Size(width * i, height * j);
    }
    public Size divide(int i, int j) {
        return new Size(width / i, height / j);
    }

    public Size add(int d) {
        return add(d, d);
    }
    public Size subtract(int d) {
        return subtract(d, d);
    }
    public Size multiply(int k) {
        return multiply(k, k);
    }
    public Size divide(int k) {
        return divide(k, k);
    }

    public Size add(Size that) {
        return add(that.width, that.height);
    }
    public Size subtract(Size that) {
        return subtract(that.width, that.height);
    }
    public Size multiply(Size that) {
        return multiply(that.width, that.height);
    }
    public Size divide(Size that) {
        return divide(that.width, that.height);
    }

    /* Screw operator overloading. Here's the replacements
    def unary_- = negate
    def unary_~ = invert

    def +(that: Size) = add(that)
    def -(that: Size) = subtract(that)
    def *(that: Size) = multiply(that)
    def /(that: Size) = divide(that)

    def +(that: Int) = add(that)
    def -(that: Int) = subtract(that)
    def *(that: Int) = multiply(that)
    def /(that: Int) = divide(that)
     */

    public Vec2 vectorize() {
        return new Vec2(width, height);
    }

    @Override
    public String toString() {
        return  "Size @[" + width + " " + height + "]";
    }
}
