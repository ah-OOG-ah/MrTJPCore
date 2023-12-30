package mrtjp.core.math;

import codechicken.lib.vec.BlockCoord;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class JMathLib {

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
}
