package es.skepz.quarkcore.commands

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.playSound
import es.skepz.quarkcore.skepzlib.sendMessage
import es.skepz.quarkcore.skepzlib.wrappers.CoreCMD
import es.skepz.quarkcore.utils.getSpawn
import org.bukkit.Sound
import org.bukkit.command.CommandSender

class SpawnCommand(val core: QuarkCore) : CoreCMD(core, "spawn", "/spawn", 0,
    "none", true, false) {

    override fun run() {
        val player = getPlayer()!!

        player.teleport(getSpawn(core))
        playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 5, 1.0f)
        sendMessage(player, "&7Woosh! You have been teleported to spawn!")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return emptyList()
    }
}