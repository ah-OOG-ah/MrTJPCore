/*
 * Copyright (c) 2015.
 * Created by MrTJP.
 * All rights reserved.
 */
package mrtjp.core.fx.particles

import codechicken.lib.render.CCRenderState
import mrtjp.core.fx._
import net.minecraft.client.Minecraft
import net.minecraft.client.particle.EntityFX
import net.minecraft.client.renderer.Tessellator
import net.minecraft.world.World
import org.lwjgl.opengl.GL11._

class BeamPulse2(w: World)
    extends CoreParticle(w)
    with TAlphaParticle
    with TColourParticle
    with TPositionedParticle
    with TTargetParticle
    with TTextureParticle {
  texture = "projectred:textures/particles/beam1.png"
  setSize(0.02f, 0.02f)

  private var s: ParticleAction = null
  def doPulse(r: Double, g: Double, b: Double) {
    import ParticleAction._
    removeAction(s)
    s = sequence(
      changeRGBATo(r, g, b, 0.8, 10),
      changeRGBATo(0.5, 0.5, 0.5, 0.3, 32)
    )
    runAction(s)
  }

  override def renderParticle(
      tess: Tessellator,
      f: Float,
      f1: Float,
      f2: Float,
      f3: Float,
      f4: Float,
      f5: Float
  ) {
    super.renderParticle(tess, f, f1, f2, f3, f4, f5)

    tess.draw()

    glPushMatrix()

    val var9 = 1.0f
    val slide = Minecraft.getMinecraft.thePlayer.ticksExisted
    val size = 0.7f
    val dptx = x - tx
    val dpty = y - ty
    val dptz = z - tz
    val length = math.sqrt(dptx * dptx + dpty * dpty + dptz * dptz)
    rotationYaw = (math.atan2(dptx, dptz) * 180.0d / math.Pi).toFloat
    rotationPitch = (math.atan2(
      dpty,
      math.sqrt(dptx * dptx + dptz * dptz)
    ) * 180.0d / math.Pi).toFloat

    CCRenderState.changeTexture(texture)

    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, 10497.0f)
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, 10497.0f)
    glDisable(GL_CULL_FACE)

    glBlendFunc(GL_SRC_ALPHA, GL_ONE)
    glDepthMask(false)

    val xx = px + dx * f - EntityFX.interpPosX
    val yy = py + dy * f - EntityFX.interpPosY
    val zz = pz + dz * f - EntityFX.interpPosZ
    glTranslated(xx, yy, zz)

    val ry = prevRotationYaw + (rotationYaw - prevRotationYaw) * f
    val rp = prevRotationPitch + (rotationPitch - prevRotationPitch) * f

    glRotatef(90.0f, 1.0f, 0.0f, 0.0f)
    glRotatef(180.0f + ry, 0.0f, 0.0f, -1.0f)
    glRotatef(rp, 1.0f, 0.0f, 0.0f)

    val var11 = slide + f
    val var12 = -var11 * 0.2f - math.floor(-var11 * 0.1f)
    val var44 = -0.15d * size
    val var17 = 0.15d * size

    for (t <- 0 until 2) {
      val var29 = length * var9
      val var31 = 0.0d
      val var33 = 1.0d
      val var35 = -1.0f + var12 + t / 3.0f
      val var37 = length * var9 + var35

      glRotatef(90.0f, 0.0f, 1.0f, 0.0f)
      tess.startDrawingQuads()
      tess.setBrightness(200)
      tess.setColorRGBA_F(
        red.toFloat,
        green.toFloat,
        blue.toFloat,
        alpha.toFloat
      )
      tess.addVertexWithUV(var44, var29, 0.0d, var33, var37)
      tess.addVertexWithUV(var44, 0.0d, 0.0d, var33, var35)
      tess.addVertexWithUV(var17, 0.0d, 0.0d, var31, var35)
      tess.addVertexWithUV(var17, var29, 0.0d, var31, var37)
      tess.draw()
    }

    glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
    glDepthMask(true)
    glBlendFunc(GL_SRC_ALPHA, 771)
    glEnable(GL_CULL_FACE)
    glPopMatrix()

    renderFlare(tess, f, f1, f2, f3, f4, f5)

    tess.startDrawingQuads()
  }

  def renderFlare(
      tess: Tessellator,
      f: Float,
      f1: Float,
      f2: Float,
      f3: Float,
      f4: Float,
      f5: Float
  ) {
    glPushMatrix()
    glDepthMask(false)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE)
    glColor4f(1.0f, 1.0f, 1.0f, 0.66f)

    CCRenderState.changeTexture("projectred:textures/particles/beamflare.png")

    val part = particleAge % 16

    val var8 = part / 16.0
    val var9 = var8 + 0.0624375
    val var10 = 0.0
    val var11 = var10 + 0.0624375
    val var12 = 0.66 * alpha
    val var13 = ptx + dtx * f - EntityFX.interpPosX
    val var14 = pty + dty * f - EntityFX.interpPosY
    val var15 = ptz + dtz * f - EntityFX.interpPosZ

    tess.startDrawingQuads()
    tess.setBrightness(200)
    tess.setColorRGBA_F(red.toFloat, green.toFloat, blue.toFloat, alpha.toFloat)
    tess.addVertexWithUV(
      var13 - f1 * var12 - f4 * var12,
      var14 - f2 * var12,
      var15 - f3 * var12 - f5 * var12,
      var9,
      var11
    )
    tess.addVertexWithUV(
      var13 - f1 * var12 + f4 * var12,
      var14 + f2 * var12,
      var15 - f3 * var12 + f5 * var12,
      var9,
      var10
    )
    tess.addVertexWithUV(
      var13 + f1 * var12 + f4 * var12,
      var14 + f2 * var12,
      var15 + f3 * var12 + f5 * var12,
      var8,
      var10
    )
    tess.addVertexWithUV(
      var13 + f1 * var12 - f4 * var12,
      var14 - f2 * var12,
      var15 + f3 * var12 - f5 * var12,
      var8,
      var11
    )
    tess.draw()

    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    glDepthMask(true)
    glPopMatrix()
  }
}
