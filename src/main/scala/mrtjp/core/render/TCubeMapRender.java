package mrtjp.core.render;

import codechicken.lib.render.BlockRenderer;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.TextureUtils;
import codechicken.lib.render.uv.IconTransformation;
import codechicken.lib.render.uv.UVTransformation;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import mrtjp.core.block.BlockCore;
import mrtjp.core.block.TInstancedBlockRender;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface TCubeMapRender extends TInstancedBlockRender {

    List<List<CCModel>> models = ((Supplier<List<List<CCModel>>>) () -> {
        List<List<CCModel>> array = Stream.generate(() -> new ArrayList<CCModel>()).limit(6)
            .collect(Collectors.toList());
        CCModel box = CCModel.quadModel(24).generateBlock(0, Cuboid6.full);
        for (int s = 0; s < 6; ++s) {
            for (int r = 0; r < 4; ++r) {
                CCModel m = box.copy().apply(Rotation.sideOrientation(s, r).at(Vector3.center));
                m.computeNormals();
                array.get(s).set(r, m);
            }
        }
        return array;
    }).get();

    Translation invTranslation = new Translation(-0.5, -0.5, -0.5);

    @Override
    default void renderWorldBlock(
        RenderBlocks r,
        IBlockAccess w,
        int x,
        int y,
        int z,
        int meta
    ) {
        Triple<Integer, Integer, UVTransformation> data = getData(w, x, y, z);
        int s = data.getLeft();
        int rot = data.getMiddle();
        UVTransformation icon = data.getRight();

        TextureUtils.bindAtlas(0);
        CCRenderState.reset();
        CCRenderState.lightMatrix.locate(w, x, y, z);
        models.get(s).get(rot).render(
            new Translation(x, y, z),
            icon,
            CCRenderState.lightMatrix
        );
    }

    @Override
    default void renderBreaking(
        IBlockAccess w,
        int x,
        int y,
        int z,
        IIcon icon
    ) {
        Block b = w.getBlock(x, y, z);
        CCRenderState.reset();
        CCRenderState.setPipeline(
            new Translation(x, y, z),
            new IconTransformation(icon)
        );
        BlockRenderer.renderCuboid(
            new Cuboid6(
                b.getBlockBoundsMinX(),
                b.getBlockBoundsMinY(),
                b.getBlockBoundsMinZ(),
                b.getBlockBoundsMaxX(),
                b.getBlockBoundsMaxY(),
                b.getBlockBoundsMaxZ()
            ),
            0
        );
    }

    @Override
    default void renderInvBlock(RenderBlocks r, int meta) {
        Triple<Integer, Integer, UVTransformation> tmp = getInvData();
        int s = tmp.getLeft();
        int rot = tmp.getMiddle();
        UVTransformation icon = tmp.getRight();


        TextureUtils.bindAtlas(0);
        CCRenderState.reset();
        CCRenderState.setDynamic();
        CCRenderState.pullLightmap();
        CCRenderState.startDrawing();
        models.get(s).get(rot).render(invTranslation, icon);
        CCRenderState.draw();
    }

    Triple<Integer, Integer, UVTransformation> getData(
        IBlockAccess w,
        int x,
        int y,
        int z
    );

    Triple<Integer, Integer, UVTransformation> getInvData();
}
