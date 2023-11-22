package es.skepz.quarkcore.utils

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.files.UserFile
import es.skepz.quarkcore.skepzlib.colorize
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerLoginEvent

fun refreshPermissions(plugin: QuarkCore, player: Player) {
    // remove current permissions
    player.recalculatePermissions()
    for (attachment in player.effectivePermissions) {
        val attch = attachment.attachment ?: continue
        player.removeAttachment(attch)
    }

    val file = UserFile(plugin, player)

    // give players the permissions that they should have
    val rank = file.getRank()
    val rankPerms = plugin.files.ranks.cfg.getStringList("ranks.$rank.permissions")
    val op = plugin.files.ranks.cfg.getBoolean("ranks.$rank.isOp")

    for (perm in rankPerms) {
        player.addAttachment(plugin, perm, true)
    }
    player.recalculatePermissions()

    player.isOp = op
}

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

    refreshPermissions(plugin, player)

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

    // give players the permissions that they should have
    refreshPermissions(plugin, player)
}

fun logout(plugin: QuarkCore, player: Player) {
    if (plugin.userFiles.containsKey(player.uniqueId)) {
        val file = plugin.userFiles[player.uniqueId]!!
        file.setLastLogoff()
    }
    plugin.userFiles.remove(player.uniqueId)
    plugin.tpaRequests.remove(player.uniqueId)
    plugin.tpahereRequests.remove(player.uniqueId)
    plugin.confirmMap.remove(player.uniqueId)
}

fun reloadLogout(plugin: QuarkCore, player: Player) {
    plugin.userFiles.remove(player.uniqueId)
    plugin.tpaRequests.remove(player.uniqueId)
    plugin.tpahereRequests.remove(player.uniqueId)
    plugin.confirmMap.remove(player.uniqueId)
}