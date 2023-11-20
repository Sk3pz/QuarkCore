package es.skepz.quarkcore.commands.confirmation

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.sendMessage
import es.skepz.quarkcore.skepzlib.wrappers.CoreCMD
import org.bukkit.command.CommandSender

class QuarkCancelCommand(val quark: QuarkCore) : CoreCMD(quark, "quark_cancel", "/quark_cancel",
    0, "none", true, false) {

    override fun run() {
        val player = getPlayer()!!
        if (quark.confirmMap.contains(player.uniqueId)) {
            quark.confirmMap.remove(player.uniqueId)?.let { it(quark, player, false) }
        } else {
            return sendMessage(player, "&cYou have nothing to confirm.")
        }
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return emptyList()
    }
}