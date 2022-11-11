package dev.rishon.rdamageindicator;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");

    public static String translate(String msg) {
        Matcher matcher = pattern.matcher(msg);
        while (matcher.find()) {
            final net.md_5.bungee.api.ChatColor hexColor = net.md_5.bungee.api.ChatColor.of(matcher.group().substring(1));
            final String before = msg.substring(0, matcher.start());
            final String after = msg.substring(matcher.end());
            msg = before + hexColor + after;
            matcher = pattern.matcher(msg);
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
