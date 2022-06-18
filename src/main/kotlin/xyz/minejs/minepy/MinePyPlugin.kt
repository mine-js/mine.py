package xyz.minejs.minepy

import org.bukkit.command.CommandExecutor
import org.bukkit.plugin.java.JavaPlugin
import org.python.util.PythonInterpreter
import java.io.File
import java.nio.file.Paths
import java.util.*


class MinePyPlugin: JavaPlugin() {
    lateinit var interpreter: PythonInterpreter

    override fun onEnable() {
        val props = Properties()
        props.setProperty("python.path", Paths.get(".").toAbsolutePath().normalize().toString())

        PythonInterpreter.initialize(System.getProperties(), props, arrayOf(""))
        loadScript()
        getCommand("pyreload")?.setExecutor(CommandExecutor { sender, command, label, args ->
            val endFunction = interpreter.get("end")
            if(endFunction.isCallable)
                endFunction.__call__()
            interpreter.close()
            loadScript()
            sender.sendMessage("Re-loaded script(s)!")
            return@CommandExecutor true
        })
    }

    fun loadScript() {
        val scriptFile = File(dataFolder, "main.py")
        if(!scriptFile.exists())
            scriptFile.createNewFile()

        interpreter = PythonInterpreter()
        interpreter.execfile(scriptFile.inputStream())
    }
}