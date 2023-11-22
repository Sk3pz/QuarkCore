package es.skepz.quarkcore

import es.skepz.quarkcore.commands.economy.BalanceCommand
import es.skepz.quarkcore.commands.RankupCommand
import es.skepz.quarkcore.commands.SellCommand
import es.skepz.quarkcore.commands.SpawnCommand
import es.skepz.quarkcore.commands.WarpCommand
import es.skepz.quarkcore.commands.admin.*
import es.skepz.quarkcore.commands.admin.punish.*
import es.skepz.quarkcore.commands.confirmation.QuarkCancelCommand
import es.skepz.quarkcore.commands.confirmation.QuarkConfirmCommand
import es.skepz.quarkcore.commands.economy.PayCommand
import es.skepz.quarkcore.commands.tpa.*
import es.skepz.quarkcore.events.*
import es.skepz.quarkcore.files.ServerFiles
import es.skepz.quarkcore.files.UserFile
import es.skepz.quarkcore.mine.Mine
import es.skepz.quarkcore.utils.reloadLogin
import es.skepz.quarkcore.utils.reloadLogout
import es.skepz.quarkcore.utils.setSpawn
import es.skepz.quarkcore.utils.setWarp
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class QuarkCore : JavaPlugin() {

    val files = ServerFiles(this)
    val userFiles = mutableMapOf<UUID, UserFile>()
    val tpaRequests = HashMap<UUID, UUID>()
    val tpahereRequests = HashMap<UUID, UUID>()

    val confirmMap = HashMap<UUID, (core: QuarkCore, player: Player, confirm: Boolean) -> Unit>()

    val mines = mutableListOf<Mine>()

    val mineBlocKey = NamespacedKey(this, "mine-block")

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
        SpawnCommand(this).register()
        SellCommand(this).register()
        WarpCommand(this).register()

        // admin commands
        ConfigsCommand(this).register()
        EcoCommand(this).register()
        RankCommand(this).register()
        MineCommand(this).register()
        SetSpawnCommand(this).register()
        SetFreeLocCommand(this).register()
        WorldCommand(this).register()
        SetWarpCommand(this).register()

        // punishment commands
        BanCommand(this).register()
        KickCommand(this).register()
        TempBanCommand(this).register()
        UnbanCommand(this).register()
        MuteCommand(this).register()
        TempMuteCommand(this).register()
        UnmuteCommand(this).register()

        // # Register Events
        EventPlayerJoin(this).register()
        EventPlayerChat(this).register()
        EventPlayerQuit(this).register()

        EventPlayerBreak(this).register()
        EventPlayerPlace(this).register()

        // loop through online players and create their user files
        server.onlinePlayers.forEach { player ->
            reloadLogin(this, player)
            userFiles[player.uniqueId] = UserFile(this, player)
        }

        // create the prison world and the survival world
        val survivalWorld = files.config.cfg.getString("survival-world") ?: "world"
        val prisonWorld = files.config.cfg.getString("prison-world") ?: "prison"
        val survivalCreator = WorldCreator(survivalWorld)
        survivalCreator.environment(World.Environment.NORMAL)
        server.createWorld(survivalCreator)

        val prisonCreator = WorldCreator(prisonWorld)
        survivalCreator.environment(World.Environment.NORMAL)
        server.createWorld(prisonCreator)

        // create the prison warp if it doesn't exist
        if (files.warps.cfg.getConfigurationSection("warps")?.getKeys(false)?.contains(prisonWorld) != true) {
            setWarp(this, "prison", server.getWorld(prisonWorld)?.spawnLocation ?: server.worlds.first().spawnLocation)
        }

        // default the config spawn location to the spawn of the survival world
        // if spawn is not set
        if (files.config.cfg.getConfigurationSection("spawn")?.getKeys(false)?.contains("world") != true) {
            setSpawn(this, server.getWorld(survivalWorld)?.spawnLocation ?: server.worlds.first().spawnLocation)
        }

        // set the default free position
        val freeLoc = server.getWorld(survivalWorld)?.spawnLocation ?: server.worlds.first().spawnLocation
        files.data["free-loc.world"] = freeLoc.world.name
        files.data["free-loc.x"] = freeLoc.x
        files.data["free-loc.y"] = freeLoc.y
        files.data["free-loc.z"] = freeLoc.z
        files.data["free-loc.yaw"] = freeLoc.yaw
        files.data["free-loc.pitch"] = freeLoc.pitch

        // start the mines
        files.mines.cfg.getConfigurationSection("mines")?.getKeys(false)?.forEach { mine ->
            val mineObj = Mine.loadFromFile(this, mine) ?: return@forEach
            mines.add(mineObj)
            mineObj.start()
        }
    }

    override fun onDisable() {
        // Plugin shutdown logic
        server.onlinePlayers.forEach { player ->
            reloadLogout(this, player)
        }
    }

}
