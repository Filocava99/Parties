package it.forgottenworld.fwparties.util;

import net.md_5.bungee.api.ChatColor;

public class TextUtility {

    public static  String parseColors(String message){
        char colorCodeChar  = '&';
        return ChatColor.translateAlternateColorCodes(colorCodeChar, message);
    }

}
