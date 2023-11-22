package es.skepz.quarkcore.commands.economy

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.sendMessage
import es.skepz.quarkcore.skepzlib.wrappers.CoreCMD
import es.skepz.quarkcore.utils.getUserFile
import org.bukkit.command.CommandSender

class BalanceCommand(val quarkCore: QuarkCore) : CoreCMD(quarkCore, "balance", "/balance",
    0, "none", true, false) {

    override fun run() {
        val player = getPlayer()!!
        val file = getUserFile(quarkCore, player)

        sendMessage(sender, "&7Your balance is $&b${file.getBal()}&7.")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return emptyList()
    }
}