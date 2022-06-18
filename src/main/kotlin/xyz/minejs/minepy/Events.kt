package xyz.minejs.minepy

import org.bukkit.Bukkit
import org.bukkit.event.*
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.IllegalPluginAccessException
import org.bukkit.plugin.RegisteredListener
import org.bukkit.plugin.TimedRegisteredListener
import org.python.core.PyFunction
import xyz.minejs.minepy.plugin
import java.lang.reflect.InvocationTargetException

private val Class<out Event>.handlerList : HandlerList
    get() {
        val throwException = fun (): Nothing = throw IllegalPluginAccessException(
            "Unable to find handler list for event ${name}. Static getHandlerList method required!"
        )

        val exceptionableHandlerList = fun Class<out Event>.() : HandlerList {
            val method = getDeclaredMethod("getHandlerList").apply { isAccessible = true }
            return method.invoke(null) as HandlerList
        }
        var nowClass : Class<out Event> = this
        kotlin.runCatching { return nowClass.exceptionableHandlerList() }
        while (true) {
            kotlin.runCatching { return nowClass.exceptionableHandlerList() }
            nowClass.superclass ?: throwException()
            if (nowClass.superclass == Event::class.java) throwException()
            nowClass = nowClass.superclass as Class<out Event>
        }
    }

class Events {

    companion object {
        @JvmStatic
        fun <T : Event> unregisterListener(clazz: Class<T>, listener: Listener) {
            clazz.handlerList.unregister(listener)
        }

        @JvmStatic
        fun <T : Event> registerListener(clazz: Class<T>, priority: EventPriority, ignoreCancelled: Boolean, listener: PyFunction): Listener {
            val listenerData = object : Listener {
                @EventHandler
                fun on(event : T) {
                    listener._jcall(arrayOf<Any>(event))
                }
            }
            val executor = EventExecutor { l, event ->
                runCatching {
                    if (!clazz.isAssignableFrom(event.javaClass)) {
                        return@EventExecutor
                    }
                    listenerData.javaClass.declaredMethods[0].invoke(l, event)
                }.exceptionOrNull()?.let {
                    if (it is InvocationTargetException) throw EventException(it.cause)
                    throw EventException(it)
                }
            }
            if (Bukkit.getServer().pluginManager.useTimings()) {
                clazz.handlerList.register(
                    TimedRegisteredListener(
                        listenerData,
                        executor,
                        priority,
                        plugin,
                        ignoreCancelled
                    )
                )
            } else {
                clazz.handlerList.register(
                    RegisteredListener(listenerData, executor, priority, plugin, ignoreCancelled)
                )
            }
            return listenerData
        }
    }

}