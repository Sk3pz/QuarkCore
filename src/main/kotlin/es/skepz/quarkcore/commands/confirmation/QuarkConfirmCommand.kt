package es.skepz.quarkcore.commands.confirmation

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.sendMessage
import es.skepz.quarkcore.skepzlib.wrappers.CoreCMD
import org.bukkit.command.CommandSender

class QuarkConfirmCommand(val quark: QuarkCore) : CoreCMD(quark, "quark_confirm", "/quark_confirm",
    0, "none", true, false) {

    override fun run() {
        val player = getPlayer()!!
        val callback = quark.confirmMap[player.uniqueId] ?: return sendMessage(player, "&cYou have nothing to confirm.")
            callback(quark, player, true)
        quark.confirmMap.remove(player.uniqueId)
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return emptyList()
    }
}