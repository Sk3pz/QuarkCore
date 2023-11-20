package es.skepz.quarkcore.utils

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.files.UserFile
import es.skepz.quarkcore.tuodlib.colorize
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerLoginEvent

fun login(plugin: QuarkCore, player: Player, event: PlayerLoginEvent): Boolean {
    // create user file
    val file = UserFile(plugin, player)
    file.setLastLogin()
    // ban check
    if (file.isBanned()) {
        event.kickMessage(Component.text(colorize("&cYou are banned from this server!\n" +
                "&cReason: &4${file.banReason()}\n" +
                (if (file.banSender() == "none") "" else "&cBanned by: &f${file.banSender()}\n") +
                (if (file.banTime() == -1L) "&cThis ban is permanent." else "&cBanned until: &f${file.bannedUntil()}"))))
        event.disallow(PlayerLoginEvent.Result.KICK_BANNED, event.kickMessage())
        return false
    }
    return true
}

// this is used for handling reloads and should not call the setLastLogin() method
fun reloadLogin(plugin: QuarkCore, player: Player) {
    // create user file
    val file = UserFile(plugin, player)
    // ban check
    if (file.isBanned()) {
        player.kick(Component.text(colorize("&cYou are banned from this server!\n" +
                "&cReason: &f${file.banReason()}\n" +
                (if (file.banSender() == "none") "" else "&cBanned by: &f${file.banSender()}\n") +
                (if (file.banTime() == -1L) "&cThis ban is permanent." else "&cBanned until: &4${file.bannedUntil()}"))))
        return
    }
}

fun logout(plugin: QuarkCore, player: Player) {
    val file = plugin.userFiles[player.uniqueId]!!
    file.setLastLogoff()
    plugin.userFiles.remove(player.uniqueId)
}

fun reloadLogout(plugin: QuarkCore, player: Player) {
    plugin.userFiles.remove(player.uniqueId)
}