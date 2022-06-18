package xyz.minejs.minepy

import org.bukkit.plugin.java.JavaPlugin

class MPy {
    companion object {
        @JvmStatic
        fun getPlugin(): JavaPlugin = plugin
    }
}