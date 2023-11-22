package es.skepz.quarkcore.commands.admin

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.sendMessage
import es.skepz.quarkcore.skepzlib.wrappers.CoreCMD
import es.skepz.quarkcore.utils.setSpawn
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetSpawnCommand(val core: QuarkCore) : CoreCMD(core, "setspawn", "/setspawn", 0,
    "quarkcore.command.setspawn", true, false) {

    override fun run() {
        val player = sender as? Player ?: return

        setSpawn(core, player.location)
        sendMessage(player, "&7Spawn set!")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return emptyList()
    }

}