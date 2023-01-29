/*
 * Copyright (c) 2014.
 * Created by MrTJP.
 * All rights reserved.
 */
package mrtjp.core.resource

import net.minecraft.block.Block
import net.minecraft.world.World

object SoundLib {
  def playBlockPlacement(w: World, x: Int, y: Int, z: Int, block: Block) {
    w.playSoundEffect(
      x + 0.5,
      y + 0.5,
      z + 0.5,
      block.stepSound.func_150496_b,
      (block.stepSound.getVolume + 1.0f) / 2.0f,
      block.stepSound.getPitch * 0.8f
    )
  }
}
