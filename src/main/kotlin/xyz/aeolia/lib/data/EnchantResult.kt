package xyz.aeolia.lib.data

enum class EnchantResult {
  SUCCESS,
  INCOMPATIBLE_ENCHANTMENT, // this one refers to if an enchantment is not compatible with an item
  INVALID_ENCHANTMENT,
  INVALID_LEVEL,
  INSUFFICIENT_FUNDS,
  CONFLICTING_ENCHANTMENTS, // this one refers to if an enchantment is not compatible with other enchantments already on the item
  MISSING_DEPENDENCY
}