package minepy

import org.bukkit.plugin.java.JavaPlugin
import xyz.minejs.minepy.plugin

class MPy {
    companion object {
        @JvmStatic
        fun getPlugin(): JavaPlugin = plugin
    }
}