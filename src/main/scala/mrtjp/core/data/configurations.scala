/*
 * Copyright (c) 2015.
 * Created by MrTJP.
 * All rights reserved.
 */
package mrtjp.core.data

import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement
import cpw.mods.fml.client.config.{GuiConfig, IConfigElement}
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.common.config.{ConfigElement, Configuration}

import java.util.{ArrayList => JAList}
import scala.collection.JavaConversions._

object SpecialConfigGui {
  def buildCategories(config: Configuration) =
    new JAList[IConfigElement[_]](config.getCategoryNames.map(s => {
      new DummyCategoryElement(
        s,
        "",
        new ConfigElement(config.getCategory(s)).getChildElements
      ) {
        override def getComment = config.getCategory(s).getComment
      }
    }))
}

class SpecialConfigGui(parent: GuiScreen, modid: String, config: Configuration)
    extends GuiConfig(
      parent,
      SpecialConfigGui.buildCategories(config),
      modid,
      false,
      false,
      GuiConfig.getAbridgedConfigPath(config.toString)
    )

