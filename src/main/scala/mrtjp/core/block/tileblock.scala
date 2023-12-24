/*
 * Copyright (c) 2014.
 * Created by MrTJP.
 * All rights reserved.
 */
package mrtjp.core.block

import codechicken.lib.vec.{BlockCoord, Rotation, Vector3}
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler
import net.minecraft.block.Block
import net.minecraft.client.renderer.RenderBlocks
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.world.IBlockAccess

object TileRenderRegistry extends ISimpleBlockRenderingHandler {
  var renderID = -1

  var renders = Map[String, Array[TInstancedBlockRender]]()
    .withDefaultValue(new Array[TInstancedBlockRender](16))

  def setRenderer(b: Block, meta: Int, r: TInstancedBlockRender) {
    val name = b.getUnlocalizedName
    val a = renders.get(name) match {
      case Some(e) => e
      case None    => new Array[TInstancedBlockRender](16)
    }
    a(meta) = r
    renders += name -> a
  }

  def getRenderer(b: Block, meta: Int) =
    renders.get(b.getUnlocalizedName) match {
      case Some(e) => e(meta)
      case None    => NullRenderer
    }

  def registerIcons(b: Block, reg: IIconRegister) {
    for (r <- renders(b.getUnlocalizedName))
      if (r != null) r.registerIcons(reg)
  }

  def getIcon(b: Block, side: Int, meta: Int) =
    getRenderer(b, meta).getIcon(side, meta)

  override def getRenderId = renderID

  override def shouldRender3DInInventory(modelId: Int) = true

  override def renderInventoryBlock(
      b: Block,
      meta: Int,
      rID: Int,
      r: RenderBlocks
  ) {
    if (rID != renderID) return

    val render = getRenderer(b, meta)
    if (render == null) {
      println(
        "No render mapping found for " + b.getUnlocalizedName + ":" + meta
      )
      return
    }

    render.renderInvBlock(r, meta)
  }

  override def renderWorldBlock(
      w: IBlockAccess,
      x: Int,
      y: Int,
      z: Int,
      b: Block,
      rID: Int,
      r: RenderBlocks
  ): Boolean = {
    if (rID != renderID) return false

    val meta = w.getBlockMetadata(x, y, z)
    val render = getRenderer(b, meta)

    if (render == null) {
      println(
        "No render mapping found for " + b.getUnlocalizedName + ":" + meta
      )
      return true
    }

    if (r.hasOverrideBlockTexture)
      render.renderBreaking(w, x, y, z, r.overrideBlockTexture)
    else render.renderWorldBlock(r, w, x, y, z, meta)
    true
  }
}

object NullRenderer extends TInstancedBlockRender {
  override def renderWorldBlock(
      r: RenderBlocks,
      w: IBlockAccess,
      x: Int,
      y: Int,
      z: Int,
      meta: Int
  ) {}
  override def getIcon(side: Int, meta: Int) = null
  override def renderInvBlock(r: RenderBlocks, meta: Int) {}
  override def registerIcons(reg: IIconRegister) {}
}

trait TTileOrient extends InstancedBlockTile {
  var orientation: Byte = 0

  def side = orientation >> 2

  def setSide(s: Int) {
    val oldOrient = orientation
    orientation = (orientation & 0x3 | s << 2).toByte
    if (oldOrient != orientation) onOrientChanged(oldOrient)
  }

  def rotation = orientation & 0x3

  def setRotation(r: Int) {
    val oldOrient = orientation
    orientation = (orientation & 0xfc | r).toByte
    if (oldOrient != orientation) onOrientChanged(oldOrient)
  }

  def position = new BlockCoord(xCoord, yCoord, zCoord)

  def rotationT = Rotation.sideOrientation(side, rotation).at(Vector3.center)

  def onOrientChanged(oldOrient: Int) {}

  // internal r from absRot
  def toInternal(absRot: Int) = (absRot + 6 - rotation) % 4

  // absRot from internal r
  def toAbsolute(r: Int) = (r + rotation + 2) % 4

  // absDir from absRot
  def absoluteDir(absRot: Int) = Rotation.rotateSide(side, absRot)

  // absRot from absDir
  def absoluteRot(absDir: Int) = Rotation.rotationTo(side, absDir)
}
