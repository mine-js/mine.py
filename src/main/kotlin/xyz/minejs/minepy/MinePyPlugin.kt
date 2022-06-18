package xyz.minejs.minepy

import org.bukkit.command.CommandExecutor
import org.bukkit.plugin.java.JavaPlugin
import org.python.core.PyException
import org.python.util.PythonInterpreter
import java.nio.file.Paths
import java.util.*
import kotlin.collections.ArrayList

lateinit var plugin: MinePyPlugin

class MinePyPlugin: JavaPlugin() {
    val interpreters = ArrayList<PythonInterpreter>()

    override fun onEnable() {
        plugin = this
        Class.forName("minepy.Events")
        val props = Properties()
        props.setProperty("python.path", Paths.get(".").toAbsolutePath().normalize().toString())
        props.setProperty("python.import.site", "false")

        PythonInterpreter.initialize(System.getProperties(), props, arrayOf(""))
        loadScript()
        getCommand("pyreload")?.setExecutor(CommandExecutor { sender, command, label, args ->
            for (interpreter in interpreters) {
                val endFunction = interpreter.get("end")
                if(endFunction != null && endFunction.isCallable)
                    endFunction.__call__()
                interpreter.close()
            }
            val errors = loadScript()
            errors.forEach { error ->
                sender.sendMessage("Error when loading ${error.key} - ${error.value.message}")
            }
            sender.sendMessage("Re-loaded script(s) with ${errors.size} error(s)!")
            return@CommandExecutor true
        })
    }

    fun loadScript(): WeakHashMap<String, PyException> {
        if(!dataFolder.exists())
            dataFolder.mkdir()

        val exceptionMap = WeakHashMap<String, PyException>()

        dataFolder.listFiles()?.forEach { scriptFile ->
            try {
                interpreters.add(PythonInterpreter().also { interpreter ->
                    interpreter.systemState.classLoader = this.classLoader
                    interpreter.execfile(scriptFile.inputStream())
                })
            } catch (ex: PyException) {
                ex.printStackTrace()
                exceptionMap[scriptFile.name] = ex
            }
        }

        return exceptionMap
    }
}