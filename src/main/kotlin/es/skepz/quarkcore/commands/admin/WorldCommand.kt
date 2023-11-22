package es.skepz.quarkcore.commands.admin

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.sendMessage
import es.skepz.quarkcore.skepzlib.wrappers.CoreCMD
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class WorldCommand(val core: QuarkCore): CoreCMD(core, "world", "/world <create|delete|tp|list>", 1,
    "quarkcore.command.world", true, true) {

    override fun run() {
        val player = getPlayer()!!

        val mode = args[0]

        when (mode) {
            "create" -> {
                if (args.size < 2) {
                    sendMessage(player, "&cUsage: &f/world create <name>")
                    return
                }
                val name = args[1]
                val world = core.server.getWorld(name)
                if (world != null) {
                    player.sendMessage("&cThe world &f$name &calready exists.")
                    return
                }

                val creator = WorldCreator(name)
                creator.generatorSettings("2;0;1;")
                creator.generateStructures(false)
                creator.environment(World.Environment.NORMAL)

                core.server.createWorld(creator) ?: return sendMessage(player, "&cError creating world $name.")
                sendMessage(player, "&7Created world &b$name&7.")
            }
            "delete" -> {
                if (args.size < 2) {
                    sendMessage(player, "&cUsage: &f/world create <name>")
                    return
                }
                val name = args[1]
                val world = core.server.getWorld(name)
                if (world == null) {
                    sendMessage(player, "&cThe world &f$name &cdoesn't exist.")
                    return
                }

                core.server.unloadWorld(world, true)
                // delete the world's folder
                val worldFolder = core.server.worldContainer.resolve(name)
                worldFolder.deleteRecursively()
                sendMessage(player, "&7Deleted world &b$name&b.")
            }
            "tp" -> {
                if (args.size < 2) {
                    sendMessage(player, "&cUsage: &f/world create <name>")
                    return
                }
                val name = args[1]
                val world = core.server.getWorld(name)
                if (world == null) {
                    sendMessage(player, "&cThe world &f$name &cdoesn't exist.")
                    return
                }

                player.teleport(world.spawnLocation)
                sendMessage(player, "&7Teleported to world &b$name&7.")
            }
            "list" -> {
                val worlds = core.server.worlds
                val worldNames = worlds.joinToString("&7, &b") { it.name }
                sendMessage(player, "&7Worlds: &b$worldNames&7.")
            }
        }

    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()

        val options = when (args.size) {
            1 -> {
                listOf("create", "delete", "tp", "list")
            }
            2 -> {
                when (args[0]) {
                    "create", "delete", "tp" -> {
                        core.server.worlds.map { it.name }
                    }
                    else -> emptyList()
                }
            }
            else -> emptyList()
        }

        StringUtil.copyPartialMatches(args.last(), options, completions)

        return completions
    }

}