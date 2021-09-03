package it.ancientrealms.utils

import net.md_5.bungee.api.ChatColor

object TextUtils {

    fun parseColors(message: String?): String {
        val colorCodeChar = '&'
        return ChatColor.translateAlternateColorCodes(colorCodeChar, message ?: "")
    }
}