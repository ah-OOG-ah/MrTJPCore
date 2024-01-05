package mrtjp.core.math;

import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Vector3;
import mrtjp.core.handler.MrTJPCoreMod;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static net.minecraft.realms.Tezzelator.t;

public class MathLib {

    public static Pair<Integer, Integer> normal(BlockCoord bc, int dir) {
        return normal(bc.x, bc.y, bc.z, dir);
    }

    public static Pair<Integer, Integer> normal(int x, int y, int z, int dir) {
        switch (dir) {
            case 0:
            case 1:
                return new ImmutablePair<>(x, z);
            case 2:
            case 3:
                return new ImmutablePair<>(x, y);
            case 4:
            case 5:
                return new ImmutablePair<>(y, z);
            default: throw new RuntimeException("Invalid direction!");
        }
    }

    public static List<Pair<Integer, Integer>> splitLine(List<Integer> xs, int shift) {
        if (xs.isEmpty()) return new ArrayList<>();
        else {
            int start = 0;
            List<Pair<Integer, Integer>> ret = new ArrayList<>();
            for (int i = 0; i < xs.size(); ++i) {
                int x = xs.get(i);
                if (i > 0 && x != xs.get(i - 1) + shift) {
                    int size = i - start;
                    start = i;
                    ret.add(new ImmutablePair<>(xs.get(i - 1), size));
                }
            }
            ret.add(new ImmutablePair<>(xs.get(xs.size() - 1), xs.size() - start));
            return ret;
        }
    }

    public static float clamp(float min, float max, float v) {
        return Math.min(max, Math.max(min, v));
    }

    public static int basis(BlockCoord bc, int dir) {
        return basis(bc.x, bc.y, bc.z, dir);
    }
    public static int basis(int x, int y, int z, int dir) {
        switch(dir) {
            case 0:
            case 1:
                return y;
            case 2:
            case 3:
                return z;
            case 4:
            case 5:
                return x;
            default: throw new RuntimeException("Invalid direction!");
        }
    }

    public static int shift(int dir) {
        return ((dir & 1) == 1) ? 1 : -1;
    }

    public static BlockCoord rhrAxis(int dir, Pair<Integer, Integer> normal, int basis) {
        switch (dir) {
            case 0:
            case 1:
                return new BlockCoord(normal.getLeft(), basis, normal.getRight());
            case 2:
            case 3:
                return new BlockCoord(normal.getLeft(), normal.getRight(), basis);
            case 4:
            case 5:
                return new BlockCoord(basis, normal.getLeft(), normal.getRight());
            default: throw new RuntimeException("Invalid direction!");
        }
    }

    private static Vector3 calcNewVector(float scaler, Vector3 base) {
        return base.copy().multiply(scaler);
    }

    public static Vector3 bezier(
        Vector3 s,
        Vector3 c1,
        Vector3 c2,
        Vector3 e,
        float t
    ) {
        if ((t < 0.0f) || (t > 1.0f)) return s;
        final float one_minus_t = 1.0f - t;
        final Vector3 retValue = new Vector3(0.0d, 0.0d, 0.0d);
        final Vector3[] terms = new Vector3[4];

        terms[0] = calcNewVector(one_minus_t * one_minus_t * one_minus_t, s);
        terms[1] = calcNewVector(3.0f * one_minus_t * one_minus_t * t, c1);
        terms[2] = calcNewVector(3.0f * one_minus_t * t * t, c2);
        terms[3] = calcNewVector(t * t * t, e);

        for (Vector3 v : terms) { retValue.add(v); }
        return retValue;
    }

    private static final Random random = new Random();

    /**
     * Returns a number in [a, z]
     */
    public static int randomFromToIntRange(int a, int z) {
        return randomFromIntRange(a, z, random);
    }

    /**
     * Returns a number in [a, z)
     */
    public static int randomFromUntilIntRange(int a, int z) {
        return randomFromIntRange(a, z - 1, random);
    }

    /**
     * Returns a number in [a, z]
     */
    public static int randomFromIntRange(int a, int z, Random rand) {

        return a + rand.nextInt(z - a);
    }

    public static int leastSignificant(int mask) {
        int bit = 0;
        int m = mask;
        // Hopefully Scala doesn't change what <<= does to ints
        while ((m & 1) == 0 && m != 0) { bit += 1; m <<= 1; }
        return bit;
    }

    public static int mostSignificant(int mask) {
        if (mask == 0) return 0;
        return  31 - Integer.numberOfLeadingZeros(mask);
    }

    public static <T> T weightedRandom(Collection<Pair<T, Integer>> xs) {
        return weightedRandom(xs, random);
    }

    public static <T> T weightedRandom(Collection<Pair<T, Integer>> xs, Random rand) {

        if (xs.size() == 1) return xs.stream().findAny().get().getLeft();
        int weight = rand.nextInt(xs.stream().map(Pair::getRight).reduce(Integer::sum).get());
        for (Pair<T, Integer> x : xs) {
            weight -= x.getRight();
            if (weight < 0) return x.getLeft();
        }

        // I'm allowed to throw here because Scala would too
        MrTJPCoreMod.log.error("If you got here, it's probably because something modified xs while this was iterating");
        throw new RuntimeException("This should be unreachable :concern:");
    }
}
