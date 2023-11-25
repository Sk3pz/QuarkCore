package es.skepz.quarkcore.commands.admin

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.sendMessage
import es.skepz.quarkcore.skepzlib.wrappers.CoreCMD
import es.skepz.quarkcore.utils.setWarp
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetFreeLocCommand(val core: QuarkCore) : CoreCMD(core, "setfreeloc", "/setfreeloc", 0,
    "quarkcore.command.setfreeloc", true, false) {

    override fun run() {
        val player = sender as? Player ?: return

        val freeLoc = player.location

        setWarp(core, "survival", freeLoc)

        sendMessage(player, "&7Set survival warp!")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return emptyList()
    }

}