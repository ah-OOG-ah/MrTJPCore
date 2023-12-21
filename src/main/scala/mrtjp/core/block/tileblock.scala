/*
 * Copyright (c) 2014.
 * Created by MrTJP.
 * All rights reserved.
 */
package mrtjp.core.block

import codechicken.lib.data.{MCDataInput, MCDataOutput}
import codechicken.lib.packet.{ICustomPacketTile, PacketCustom}
import codechicken.lib.vec.{BlockCoord, Cuboid6, Rotation, Vector3}
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler
import mrtjp.core.handler.MrTJPCoreSPH
import mrtjp.core.world.WorldLib
import net.minecraft.block.Block
import net.minecraft.client.renderer.RenderBlocks
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.IIcon
import net.minecraft.world.{IBlockAccess, World}

import java.util.Random
import scala.collection.mutable.ListBuffer

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

trait TInstancedBlockRender {
  def renderWorldBlock(
      r: RenderBlocks,
      w: IBlockAccess,
      x: Int,
      y: Int,
      z: Int,
      meta: Int
  )

  def renderInvBlock(r: RenderBlocks, meta: Int)

  def renderBreaking(w: IBlockAccess, x: Int, y: Int, z: Int, icon: IIcon) {}

  def randomDisplayTick(w: World, x: Int, y: Int, z: Int, r: Random) {}

  def registerIcons(reg: IIconRegister)

  def getIcon(side: Int, meta: Int): IIcon
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

abstract class InstancedBlockTile extends TileEntity with ICustomPacketTile {
  protected var schedTick = -1L

  def prepair(meta: Int) {}

  def onBlockPlaced(
      side: Int,
      meta: Int,
      player: EntityPlayer,
      stack: ItemStack,
      hit: Vector3
  ) {}

  def onBlockRemoval() {}

  def onNeighborChange(b: Block) {}

  def canConnectRS = false
  def strongPower(side: Int) = 0
  def weakPower(side: Int) = strongPower(side)

  def getLightValue = 0

  def isFireSource(side: Int) = false

  def isSolid(side: Int) = true

  def onBlockActivated(player: EntityPlayer, side: Int) = false

  def onBlockClicked(player: EntityPlayer) = false

  def onEntityCollision(ent: Entity) {}

  def getBlockBounds = Cuboid6.full

  def getCollisionBounds = Cuboid6.full

  def onScheduledTick() {}

  def updateClient() {}

  def update() {}

  def randomTick(rand: Random) {}

  def getBlock: Block

  def getMetaData = getBlockMetadata

  def getPickBlock = new ItemStack(getBlock, 1, getMetaData)

  def addHarvestContents(ist: ListBuffer[ItemStack]) {
    ist += getPickBlock
  }

  def world = worldObj
  def x = xCoord
  def y = yCoord
  def z = zCoord

  def scheduleTick(time: Int) {
    val tn = world.getTotalWorldTime + time
    if (schedTick > 0L && schedTick < tn) return
    schedTick = tn
    markDirty()
  }

  def isTickScheduled = schedTick >= 0L

  def breakBlock_do() {
    val il = new ListBuffer[ItemStack]
    addHarvestContents(il)
    for (stack <- il) WorldLib.dropItem(world, x, y, z, stack)
    world.setBlockToAir(x, y, z)
  }

  override def markDirty() {
    world.markTileEntityChunkModified(x, y, z, this)
  }

  final def markRender() {
    world.func_147479_m(x, y, z)
  }

  final def markLight() {
    world.func_147451_t(x, y, z)
  }

  final def markDescUpdate() {
    world.markBlockForUpdate(x, y, z)
  }

  final override def updateEntity() {
    if (world.isRemote) {
      updateClient()
      return
    } else update()
    if (schedTick < 0L) return
    val time = world.getTotalWorldTime
    if (schedTick <= time) {
      schedTick = -1L
      onScheduledTick()
      markDirty()
    }
  }

  final override def readFromNBT(tag: NBTTagCompound) {
    super.readFromNBT(tag)
    schedTick = tag.getLong("sched")
    load(tag)
  }

  final override def writeToNBT(tag: NBTTagCompound) {
    super.writeToNBT(tag)
    tag.setLong("sched", schedTick)
    save(tag)
  }

  def save(tag: NBTTagCompound) {}
  def load(tag: NBTTagCompound) {}

  final override def getDescriptionPacket = {
    val packet = writeStream(0)
    writeDesc(packet)
    if (compressDesc) packet.compress()
    packet.toPacket
  }

  def compressDesc = false

  final def handleDescriptionPacket(packet: PacketCustom) =
    packet.readUByte() match {
      case 0   => readDesc(packet)
      case key => read(packet, key)
    }

  def read(in: MCDataInput, key: Int) {}

  def readDesc(in: MCDataInput) {}
  def writeDesc(out: MCDataOutput) {}

  final def writeStream(key: Int): PacketCustom = {
    val stream = new PacketCustom(MrTJPCoreSPH.channel, MrTJPCoreSPH.tilePacket)
    stream.writeCoord(x, y, z).writeByte(key)
    stream
  }

  implicit def streamToSend(out: PacketCustom) = StreamSender(out)
  implicit def sendToStream(send: StreamSender) = send.out

  case class StreamSender(out: PacketCustom) {
    def sendToChunk() {
      out.sendToChunk(world, x >> 4, z >> 4)
    }
  }
}
