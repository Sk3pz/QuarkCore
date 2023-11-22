package es.skepz.quarkcore.commands.admin

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.sendMessage
import es.skepz.quarkcore.skepzlib.wrappers.CoreCMD
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetFreeLocCommand(val core: QuarkCore) : CoreCMD(core, "setfreeloc", "/setfreeloc", 0,
    "quarkcore.command.setfreeloc", true, false) {

    override fun run() {
        val player = sender as? Player ?: return

        val freeLoc = player.location

        core.files.data["free-loc.world"] = freeLoc.world.name
        core.files.data["free-loc.x"] = freeLoc.x
        core.files.data["free-loc.y"] = freeLoc.y
        core.files.data["free-loc.z"] = freeLoc.z
        core.files.data["free-loc.yaw"] = freeLoc.yaw
        core.files.data["free-loc.pitch"] = freeLoc.pitch

        sendMessage(player, "&7Set free location!")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return emptyList()
    }

}