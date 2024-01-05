/*
 * Copyright (c) 2015.
 * Created by MrTJP.
 * All rights reserved.
 */
package mrtjp.core.world

import net.minecraft.world.gen.feature.WorldGenerator
import net.minecraft.world.{World, WorldType}
import net.minecraftforge.common.BiomeDictionary

import java.util.Random

trait TGenerationLogic extends ISimpleStructureGen {
  var name = ""

  var dimensionBlacklist = true
  var dimensions = Set[Int](-1, 1)

  var biomeBlacklist = true
  var biomes = Set[Set[BiomeDictionary.Type]]()

  var typeBlacklist = true
  var types = Set[WorldType](WorldType.FLAT)

  var resistance = 0
  var allowRetroGen = false

  override def genID = name

  def preFiltCheck(
      w: World,
      chunkX: Int,
      chunkZ: Int,
      rand: Random,
      isRetro: Boolean
  ): Boolean = {
    if (isRetro && !allowRetroGen) return false
    if (dimensionBlacklist == dimensions.contains(w.provider.dimensionId))
      return false
    if (typeBlacklist == types.contains(w.provider.terrainType)) return false
    if (resistance > 1 && rand.nextInt(resistance) != 0) return false
    true
  }

  def postFiltCheck(w: World, x: Int, z: Int, rand: Random): Boolean = {
    val types =
      BiomeDictionary.getTypesForBiome(w.getBiomeGenForCoords(x, z)).toSet
    if (biomeBlacklist == biomes.contains(types)) return false
    true
  }

  override def generate(
      w: World,
      chunkX: Int,
      chunkZ: Int,
      rand: Random,
      isRetro: Boolean
  ): Boolean = {
    if (!preFiltCheck(w, chunkX, chunkZ, rand, isRetro)) return false
    generate_impl(w, chunkX, chunkZ, rand)
  }

  def generate_impl(w: World, chunkX: Int, chunkZ: Int, rand: Random): Boolean
}

class GenLogicUniform extends TGenerationLogic {
  var gen: WorldGenerator = null

  var attempts = 1
  var minY = 0
  var maxY = 0

  override def generate_impl(
      w: World,
      chunkX: Int,
      chunkZ: Int,
      rand: Random
  ) = {
    var generated = false
    for (i <- 0 until attempts) {
      val x = chunkX * 16 + rand.nextInt(16)
      val y = minY + rand.nextInt(maxY - minY)
      val z = chunkZ * 16 + rand.nextInt(16)
      if (postFiltCheck(w, x, z, rand))
        generated |= gen.generate(w, rand, x, y, z)
    }
    generated
  }
}

class GenLogicSurface extends TGenerationLogic {
  var gen: WorldGenerator = null

  var attempts = 1

  override def generate_impl(
      w: World,
      chunkX: Int,
      chunkZ: Int,
      rand: Random
  ) = {
    var generated = false
    for (i <- 0 until attempts) {
      val x = chunkX * 16 + rand.nextInt(16)
      val z = chunkZ * 16 + rand.nextInt(16)
      if (postFiltCheck(w, x, z, rand)) {
        val y = WorldLib.findSurfaceHeight(w, x, z)
        if (y > 0) generated |= gen.generate(w, rand, x, y, z)
      }
    }
    generated
  }
}
