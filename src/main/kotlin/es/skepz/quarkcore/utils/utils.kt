package es.skepz.quarkcore.utils

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.wrappers.CFGFile
import org.bukkit.entity.Player
import java.util.*

fun sendConfirmMsg(core: QuarkCore, player: Player, msg: String, callback: (core: QuarkCore, player: Player, confirm: Boolean) -> Unit) {
    player.sendMessage("&7&l$msg")
    player.sendMessage("&7Type &aConfirm &7or &ccancel&7.")
    core.confirmMap[player.uniqueId] = callback
}

fun getOfflineUserFileRaw(core: QuarkCore, uuid: UUID): CFGFile? {
    // check if the user file exists in core.dataFolder.toString() + "/users/"
    val file = java.io.File(core.dataFolder.toString() + "/users/$uuid.yml")
    if (!file.exists()) {
        return null
    }

    return CFGFile(core, uuid.toString(), "users")
}