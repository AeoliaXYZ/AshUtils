package xyz.aeolia.lib.utils

import org.bukkit.entity.Player
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

/*
AUTHOR: Dev_Richard (https://www.spigotmc.org/members/dev_richard.38792/)
DESC: A simple and easy to use class that can get and set a player's total experience points.
Feel free to use this class in both public and private plugins, however if you release your
plugin please link to this gist publicly so that others can contribute and benefit from it.
Updated by Asheiou (https://github.com/Asheiou) the 26th May 2025 to:
- Remove deprecated functions in Java 9+
- Fix a bug where 351 XP was not a valid value to set
Updated the 14th August 2025 to:
- Kotlinise
https://gist.github.com/Asheiou/8f4873ccdf65c2be56b8799b1a9a31bd
*/



class Experience(private val player: Player) {
  var totalExperience: Int
    get() {
      var experience: Int
      val level = player.level.toDouble()
      if (level >= 0 && level <= 15) {
        experience = ceil(level.pow(2.0) + (6 * level)).toInt()
        val requiredExperience = 2 * level + 7
        val currentExp = player.exp
        experience += ceil(currentExp * requiredExperience).toInt()
        return experience
      } else if (level > 15 && level <= 30) {
        experience = ceil((2.5 * level.pow(2.0) - (40.5 * level) + 360)).toInt()
        val requiredExperience = 5 * level - 38
        val currentExp = player.exp
        experience += ceil(currentExp * requiredExperience).toInt()
        return experience
      } else {
        experience = ceil(((4.5 * level.pow(2.0) - (162.5 * level) + 2220))).toInt()
        val requiredExperience = 9 * level - 158
        val currentExp = player.exp.toDouble()
        experience += ceil(currentExp * requiredExperience).toInt()
        return experience
      }
    }
    set(xp) {
      //Levels 0 through 15
      if (xp >= 0 && xp < 352) {
        //Calculate Everything
        val a = 1
        val b = 6
        val c = -xp
        val level = (-b + sqrt(b.toDouble().pow(2.0) - (4 * a * c))).toInt() / (2 * a)
        val xpForLevel = (level.toDouble().pow(2.0) + (6 * level)).toInt()
        val remainder = xp - xpForLevel
        val experienceNeeded = (2 * level) + 7
        var experience = remainder.toFloat() / experienceNeeded.toFloat()
        experience = round(experience)
        println("xpForLevel: $xpForLevel")
        println(experience)

        //Set Everything
        player.level = level
        player.setExp(experience)
        //Levels 16 through 30
      } else if (xp >= 352 && xp < 1507) {
        //Calculate Everything
        val a = 2.5
        val b = -40.5
        val c = -xp + 360
        val dLevel = (-b + sqrt(b.pow(2.0) - (4 * a * c))) / (2 * a)
        val level = floor(dLevel)
        val xpForLevel = (2.5 * level.pow(2.0) - (40.5 * level) + 360).toInt()
        val remainder = xp - xpForLevel
        val experienceNeeded = (5 * level) - 38
        var experience = remainder.toFloat() / experienceNeeded.toFloat()
        experience = round(experience)
        println("xpForLevel: $xpForLevel")
        println(experience)

        //Set Everything
        player.level = level.toInt()
        player.exp = experience
        //Level 31 and greater
      } else {
        //Calculate Everything
        val a = 4.5
        val b = -162.5
        val c = -xp + 2220
        val dLevel = (-b + sqrt(b.pow(2.0) - (4 * a * c))) / (2 * a)
        val level = floor(dLevel)
        val xpForLevel = (4.5 * level.pow(2.0) - (162.5 * level) + 2220).toInt()
        val remainder = xp - xpForLevel
        val experienceNeeded = (9 * level) - 158
        var experience = remainder.toFloat() / experienceNeeded.toFloat()
        experience = round(experience)
        println("xpForLevel: $xpForLevel")
        println(experience)

        //Set Everything
        player.level = level.toInt()
        player.setExp(experience)
      }
    }

  private fun round(d: Float): Float {
    var bd = BigDecimal(d.toString())
    bd = bd.setScale(2, RoundingMode.HALF_DOWN)
    return bd.toFloat()
  }
}