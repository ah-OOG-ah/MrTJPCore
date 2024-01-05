package mrtjp.core.world;

import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Vector3;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class WorldGenCaveReformer extends TWorldGenerator {
    public Set<Pair<Pair<Block, Integer>, Integer>> cluster = new HashSet<>();
    public Set<Pair<Block, Integer>> material = new HashSet<>();
    public int clusterSize = 1;
    public int depth = 5;
    public int searchRadius = 16;
    private final Node$ NODE$ = new Node$();

    @Override
    public boolean generate(World w, Random rand, int x, int y, int z) {
        if (!w.isAirBlock(x, y, z)) return false;

        int dy = 0;
        while (dy < searchRadius && !canSetBlock(w, x, dy + y, z, material)) ++dy;
        if (!canSetBlock(w, x, dy + y, z, material)) return false;

        final Node start = NODE$.apply(new BlockCoord(x, dy + y, z));
        start.depth = depth;
        return iterate(w, new LinkedList<>(Collections.singletonList(start)));
    }

    private boolean iterate(World w, List<Node> open) {
        return iterate(w, open, null, null);
    }

    // WARNING: This is a recursive method that had @tailrec!
    private boolean iterate(
        World w,
        List<Node> open,
        List<Node> closed,
        Boolean generated
    ) {
        if (closed == null) closed = new ArrayList<>();
        if (generated == null) generated = false;

        if (open == null || open.isEmpty()) {
            return generated;
        }

        Node next = open.get(0);

        if (closed.size() > clusterSize) return generated;
        final boolean g2 = setBlock(w, next.bc.x, next.bc.y, next.bc.z, cluster, material);
        final List<Node> upNext = new ArrayList<>();
        if (next.depth > 0) {
            for (int s = 0; s < 6; ++s) {
                final Node to = next.dashdashgt(s);
                if (!open.contains(to) && !closed.contains(to)) {
                    to.depth = (WorldLib.isBlockTouchingAir(w, to.bc)) ? depth : next.depth - 1;
                    upNext.add(to);
                }
            }
        }

        List<Node> ret = new ArrayList<>(open);
        ret.remove(0);
        ret.addAll(upNext);
        closed.add(next);
        return iterate(w, ret, closed, generated || g2);
    }

    public class Node$ {
        public Node apply(BlockCoord bc, int dir) {
            return new Node(bc.copy().offset(dir), 1);
        }
        public Node apply(BlockCoord bc) {
            return new Node(bc, 0);
        }
    }

    public class Node implements Comparable<Node> {
        int depth = 0;
        final BlockCoord bc;
        final int dist;

        Node (BlockCoord bc, int dist) {
            this.bc = bc;
            this.dist = dist;
        }

        public Node dashdashgt(int toDir, int distAway) {
            return new Node(this.bc.copy().offset(toDir), dist + distAway);
        }
        public Node dashdashgt(int toDir) {
            return this.dashdashgt(toDir, 1);
        }

        @Override
        public int compareTo(Node that) {
            return dist - that.dist;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof Node) {
                return bc == ((Node) other).bc;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return bc.hashCode();
        }

        @Override
        public String toString() {
            return  "@" + bc.toString();
        }
    }
}
