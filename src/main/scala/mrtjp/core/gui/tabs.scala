/*
 * Copyright (c) 2014.
 * Created by MrTJP.
 * All rights reserved.
 */
package mrtjp.core.gui

import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.entity.RenderItem
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import org.lwjgl.opengl.{GL11, GL12}

trait TStackTab extends TabNode {
  var iconStack: ItemStack = null
  def setIconStack(stack: ItemStack): this.type = { iconStack = stack; this }

  abstract override def drawIcon() {
    super.drawIcon()
    GL11.glColor4f(1, 1, 1, 1)
    RenderHelper.enableGUIStandardItemLighting()
    GL11.glEnable(GL12.GL_RESCALE_NORMAL)
    TStackTab.itemRender.zLevel = (zPosition + 25).toFloat
    TStackTab.itemRender.renderItemAndEffectIntoGUI(
      fontRenderer,
      renderEngine,
      iconStack,
      position.x + 3,
      position.y + 3
    )
    GL11.glDisable(GL12.GL_RESCALE_NORMAL)
    GL11.glDisable(GL11.GL_LIGHTING)
    RenderHelper.disableStandardItemLighting()
  }
}

object TStackTab {
  val itemRender = new RenderItem
}

trait TIconTab extends TabNode {
  var icon: IIcon = null
  def setIcon(i: IIcon): this.type = { icon = i; this }

  abstract override def drawIcon() {
    super.drawIcon()
    drawTexturedModelRectFromIcon(position.x + 3, position.x + 3, icon, 16, 16)
  }
}
