package es.skepz.quarkcore.commands

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.tuodlib.sendMessage
import es.skepz.quarkcore.tuodlib.wrappers.CoreCMD
import org.bukkit.command.CommandSender

class ConfigsCommand(private val quarkCore: QuarkCore) : CoreCMD(quarkCore, "cfgreload", "/cfgreload",
    0, "quark.configs", false, false) {
    override fun Context.run() {
        quarkCore.files.reload()
        quarkCore.userFiles.values.forEach { it.reload() }
        sendMessage(sender, "&aReloaded all configs.")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return emptyList()
    }
}