package es.skepz.quarkcore.commands.tpa

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.sendMessage
import es.skepz.quarkcore.skepzlib.wrappers.CoreCMD
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class TpaCancel(val core: QuarkCore) : CoreCMD(core, "tpcancel", "&c/tpcancel",
    0, "none", true, false) {

    override fun run() {
        val player = getPlayer() ?: return
        if (core.tpaRequests.containsKey(player.uniqueId)) {
            val target = Bukkit.getPlayer(core.tpaRequests[player.uniqueId]!!)
            if (target != null) {
                sendMessage(target, "&b${player.name} &7has canceled their request to teleport to you.")
                sendMessage(sender, "&7You have canceled your request to teleport to &b${target.name}&7.")
            } else {
                sendMessage(sender, "&7You have canceled your request.")
            }
            core.tpaRequests.remove(player.uniqueId)
            return
        }
        if (core.tpahereRequests.containsKey(player.uniqueId)) {
            val target = Bukkit.getPlayer(core.tpahereRequests[player.uniqueId]!!)
            if (target != null) {
                sendMessage(target, "&b${player.name} &7has canceled their request to teleport you to them.")
                sendMessage(sender, "&7You have canceled your request to teleport &b${target.name} &7to you.")
            } else {
                sendMessage(sender, "&7You have canceled your request.")
            }
            core.tpaRequests.remove(player.uniqueId)
            return
        }

        sendMessage(sender, "&cYou dont have any outgoing requests.")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return emptyList()
    }

}