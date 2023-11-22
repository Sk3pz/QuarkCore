package es.skepz.quarkcore.commands.admin

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.sendMessage
import es.skepz.quarkcore.skepzlib.wrappers.CoreCMD
import es.skepz.quarkcore.utils.sendConfirmMsg
import es.skepz.quarkcore.utils.setWarp
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetWarpCommand(val core: QuarkCore) : CoreCMD(core, "setwarp", "/setspawn <name>", 1,
    "quarkcore.command.setwarp", true, false) {

    override fun run() {
        val player = sender as? Player ?: return

        val warp = args[0]
        // check if the warp exists
        if (core.files.warps.cfg.contains("warps.$warp")) {
            sendConfirmMsg(core, player, "Warp &b$warp &7already exists! Are you sure you want to override it?") { _, _, confirmed ->
                if (confirmed) {
                    for (mine in core.mines) {
                        if (mine.name.equals(warp, true)) {
                            return@sendConfirmMsg sendMessage(player, "&cCan't override a mine's warp!")
                        }
                    }

                    setWarp(core, warp, player.location)
                    sendMessage(player, "&7Warp &b$warp &7has been set!")
                }
            }
            return;
        }
        for (mine in core.mines) {
            if (mine.name.equals(warp, true)) {
                return sendMessage(player, "&cCan't override a mine's warp!")
            }
        }

        setWarp(core, warp, player.location)
        sendMessage(player, "&7Warp &b$warp &7has been set!")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return emptyList()
    }

}