package es.skepz.quarkcore.commands

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.playSound
import es.skepz.quarkcore.skepzlib.sendMessage
import es.skepz.quarkcore.skepzlib.wrappers.CoreCMD
import es.skepz.quarkcore.utils.getWarp
import es.skepz.quarkcore.utils.isFree
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class WarpCommand(val core: QuarkCore) : CoreCMD(core, "warp", "warp <name>", 1, "none", true, true) {

    override fun run() {
        var player = getPlayer()!!

        if (args.size > 1) {
            if (!player.hasPermission("quarkcore.warp.other")) {
                return sendMessage(player, "&cYou don't have permission to warp other players!")
            }

            player = core.server.getPlayer(args[1]) ?: return sendMessage(player, "&cPlayer not found!")
        }

        val warp = args[0]

        var loc: Location? = null

        // get mine warps and check against those
        for (mine in core.mines) {
            if (mine.name.equals(warp, true)) {
                if (!mine.canBreak(player) && (!player.isOp && !player.hasPermission("*") && !player.hasPermission("quarkcore.warp.override")))
                    return sendMessage(player, "&cYou don't have permission to warp to this mine!")
                loc = mine.warp
                break
            }
        }

        // if the warp was not a mine warp, get the server warps
        if (loc == null) {
            if (warp == "survival" && (!isFree(core, player) && !player.isOp && !player.hasPermission("*") && !player.hasPermission("quarkcore.warp.override"))) {
                return sendMessage(player, "&cYou are not free, you cannot warp here!")
            }
            loc = getWarp(core, warp) ?: return sendMessage(player, "&cWarp not found!")
        }


        player.teleport(loc)
        playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 5, 1.0f)
        sendMessage(player, "&7You have been teleported to &b$warp&7!")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()
        val list = mutableListOf<String>()
        // add mine warps to the list
        for (mine in core.mines) {
            if (mine.canBreak(sender as Player)) list.add(mine.name)
        }

        // add server warps to the list
        val keys = core.files.warps.cfg.getConfigurationSection("warps")?.getKeys(false) ?: listOf()
        val player = sender as Player
        for (key in keys) {
            if (key == "survival" && (!isFree(core, player) && !player.isOp && !player.hasPermission("*"))) {
                continue
            }
            list.add(key)
        }

        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], list, completions)
        } else if (args.size == 2) {
            StringUtil.copyPartialMatches(args[1], core.server.onlinePlayers.map { it.name }, completions)
        }

        return completions
    }

}