/*
 * Copyright (c) 2015.
 * Created by MrTJP.
 * All rights reserved.
 */
package mrtjp.core.fx.particles

import mrtjp.core.fx.ParticleAction
import net.minecraft.client.particle.EntityFX
import net.minecraft.client.renderer.Tessellator
import net.minecraft.world.World

import scala.collection.mutable.{ListBuffer, Seq => MSeq}

class CoreParticle(w: World)
    extends EntityFX(w, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d) {
  motionX = 0.0d
  motionY = 0.0d
  motionZ = 0.0d

  noClip = true
  var hasVelocity = false
  var isImmortal = false

  private var actions = ListBuffer[ParticleAction]()

  def setMaxAge(age: Int) {
    particleMaxAge = age
  }

  def setAge(age: Int) {
    particleAge = age
  }

  def getAge = particleAge

  def getMaxAge = particleMaxAge

  def runAction(action: ParticleAction) {
    if (!action.canOperate(this))
      throw new RuntimeException(
        "Particle action was run on an incompatible particle class."
      )
    val a1 = action.copy
    a1.compile(this)
    actions += a1
  }

  def removeAction(action: ParticleAction) {
    val idx = actions.indexOf(action)
    if (idx > -1)
      actions.remove(idx)
  }

  override def onUpdate() {
    ticksExisted += 1
    prevDistanceWalkedModified = distanceWalkedModified

    prevRotationPitch = rotationPitch
    prevRotationYaw = rotationYaw

    if (hasVelocity) moveEntity(motionX, motionY, motionZ)

    actions.foreach(_.tickLife())

    particleAge += 1
    if (particleAge > particleMaxAge && !isImmortal) setDead()
  }

  override def entityInit() {}

  override def renderParticle(
      t: Tessellator,
      frame: Float,
      cosyaw: Float,
      cospitch: Float,
      sinyaw: Float,
      sinsinpitch: Float,
      cossinpitch: Float
  ) {
    actions.foreach(_.runOn(this, frame))
    actions = actions.filterNot(_.isFinished)
  }
}
