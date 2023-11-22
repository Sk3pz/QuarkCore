package es.skepz.quarkcore.utils

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.IMessage
import es.skepz.quarkcore.skepzlib.playSound
import es.skepz.quarkcore.skepzlib.sendMessage
import es.skepz.quarkcore.skepzlib.wrappers.CFGFile
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.*

fun sendConfirmMsg(core: QuarkCore, player: Player, msg: String, callback: (core: QuarkCore, player: Player, confirm: Boolean) -> Unit) {
    playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 5, 0.2f)
    IMessage("\n&6&lConfirmation Required&r\n")
        .add("&7$msg\n")
        .add("&7&oType or click on one of the following options:\n")
        .addHoverableClickCmd("  &a&oConfirm&r\n", "/quark_confirm ${player.name}", "&7Confirm the action")
        .addHoverableClickCmd("  &c&oCancel&r", "/quark_cancel ${player.name}", "&7Cancel the action")
        .add("\n")
        .send(player)
//    sendMessage(player,"&7&l$msg")
//    sendMessage(player, "&7Type &aConfirm &7or &ccancel&7.")
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

fun forceGetUserFile(core: QuarkCore, uuid: UUID): CFGFile {
    val file = java.io.File(core.dataFolder.toString() + "/users/$uuid.yml")
    if (!file.exists()) {
        file.createNewFile()
    }

    return CFGFile(core, uuid.toString(), "users")
}