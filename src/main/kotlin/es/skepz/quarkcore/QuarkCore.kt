package es.skepz.quarkcore

import es.skepz.quarkcore.commands.economy.BalanceCommand
import es.skepz.quarkcore.commands.RankupCommand
import es.skepz.quarkcore.commands.admin.ConfigsCommand
import es.skepz.quarkcore.commands.admin.EcoCommand
import es.skepz.quarkcore.commands.admin.RankCommand
import es.skepz.quarkcore.commands.confirmation.QuarkCancelCommand
import es.skepz.quarkcore.commands.confirmation.QuarkConfirmCommand
import es.skepz.quarkcore.commands.economy.PayCommand
import es.skepz.quarkcore.commands.tpa.*
import es.skepz.quarkcore.events.EventPlayerChat
import es.skepz.quarkcore.events.EventPlayerJoin
import es.skepz.quarkcore.files.ServerFiles
import es.skepz.quarkcore.files.UserFile
import es.skepz.quarkcore.utils.reloadLogin
import es.skepz.quarkcore.utils.reloadLogout
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class QuarkCore : JavaPlugin() {

    val files = ServerFiles(this)
    val userFiles = mutableMapOf<UUID, UserFile>()
    val tpaRequests = HashMap<UUID, UUID>()
    val tpahereRequests = HashMap<UUID, UUID>()

    val confirmMap = HashMap<UUID, (core: QuarkCore, player: Player, confirm: Boolean) -> Unit>()

    override fun onEnable() {
        // # Register Commands

        // tpa
        TpaCommand(this).register()
        TphereCommand(this).register()
        TpaCancel(this).register()
        TpacceptCommand(this).register()
        TpdenyCommand(this).register()
        TplistCommand(this).register()

        // economy
        BalanceCommand(this).register()
        PayCommand(this).register()

        // confirmation
        QuarkCancelCommand(this).register()
        QuarkConfirmCommand(this).register()

        // other
        RankupCommand(this).register()

        // admin commands
        ConfigsCommand(this).register()
        EcoCommand(this).register()
        RankCommand(this).register()

        // # Register Events
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
