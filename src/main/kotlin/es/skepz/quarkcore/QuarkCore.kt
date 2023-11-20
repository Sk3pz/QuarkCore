package es.skepz.quarkcore

import es.skepz.quarkcore.commands.ConfigsCommand
import es.skepz.quarkcore.events.EventPlayerChat
import es.skepz.quarkcore.events.EventPlayerJoin
import es.skepz.quarkcore.files.ServerFiles
import es.skepz.quarkcore.files.UserFile
import es.skepz.quarkcore.utils.reloadLogin
import es.skepz.quarkcore.utils.reloadLogout
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

/***
 * TODO:
 *   - creating, deleting, and teleporting to worlds
 *   - warps
 *   - /spawn
 *   - /tpa and associated commands
 *   - custom items config
 *   - prison mines
 *   - prison prestige system
 *   - regular ranks
 *   - chat filter
 *   - /rules
 *   - /help
 *   - admin commands
 */

class QuarkCore : JavaPlugin() {

    val files = ServerFiles(this)
    val userFiles = mutableMapOf<UUID, UserFile>()

    override fun onEnable() {
        // register commands
        ConfigsCommand(this).register()

        // register events
        EventPlayerJoin(this).register()
        EventPlayerChat(this).register()

        // loop through online players and create their user files
        server.onlinePlayers.forEach { player ->
            reloadLogin(this, player)
            userFiles[player.uniqueId] = UserFile(this, player)
        }
    }

    override fun onDisable() {
        // Plugin shutdown logic
        server.onlinePlayers.forEach { player ->
            reloadLogout(this, player)
        }
    }

}
