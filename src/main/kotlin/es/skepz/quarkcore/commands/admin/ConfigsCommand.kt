package es.skepz.quarkcore.commands.admin

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.sendMessage
import es.skepz.quarkcore.skepzlib.wrappers.CoreCMD
import org.bukkit.command.CommandSender

class ConfigsCommand(private val quarkCore: QuarkCore) : CoreCMD(quarkCore, "cfgreload", "/cfgreload",
    0, "quarkcore.commands.cfgrl", false, false) {
    override fun run() {
        quarkCore.files.reload()
        quarkCore.userFiles.values.forEach { it.reload() }
        sendMessage(sender, "&7Reloaded all configs.")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return emptyList()
    }
}