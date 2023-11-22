package es.skepz.quarkcore.commands.admin

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.mine.Mine
import es.skepz.quarkcore.skepzlib.sendMessage
import es.skepz.quarkcore.skepzlib.wrappers.CoreCMD
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class MineCommand(val core: QuarkCore) : CoreCMD(core, "mine",
    "/mine help", 1,
    "quarkcore.command.mine", true, false) {

    override fun run() {
        val player = getPlayer()!!

        when (args[0]) {
            "help" -> {
                sendMessage(sender, "&7&lMine Tutorial:\n"
                        + "&7First, Create the mine:\n"
                        + "  &b/mine create <name> <required-rank>\n"
                        + "&7Second, set the location players will teleport to when teleporting to the mine:\n"
                        + "  &b/mine setwarp <name>\n"
                        + "&7Third, set a corner of the mine:\n"
                        + "  &b/mine setcorner1 <name>\n"
                        + "&7Fourth, set the exact opposite corner:\n"
                        + "  &b/mine setcorner2 <name>\n"
                        + "&7Fifth, add blocks to the mine:\n"
                        + "  &b/mine addblock <name> <type> <spawn-chance> <value>\n"
                        + "&7Finally, start the mine\n"
                        + "  &b/mine start <name>\n"
                        + "&7Other relevant commands:\n"
                        + "  &b/mine removeblock <name> <type>\n"
                        + "  &b/mine delete <name>\n"
                        + "  &b/mine reset <name>\n"
                        + "  &b/mine empty <name>\n"
                        + "  &b/mine status <name>\n"
                        + "  &b/mine stop <name>\n"
                        + "  &b/mine list")
            }
            "debug" -> {
                if (args.size != 2) {
                    sendMessage(sender, "&cInvalid usage. &f/mine start <name>")
                    return
                }
                val name = args[1]

                val mine = core.mines.find { it.name == name }
                if (mine == null) {
                    sendMessage(sender, "&cA mine with that name doesn't exist.")
                    return
                }

                mine.debug()
                sendMessage(sender, "&7Placed debug blocks.")
            }
            "start" ->  { // /mine start <name>
                if (args.size != 2) {
                    sendMessage(sender, "&cInvalid usage. &f/mine start <name>")
                    return
                }
                val name = args[1]

                val mine = core.mines.find { it.name == name }
                if (mine == null) {
                    sendMessage(sender, "&cA mine with that name doesn't exist.")
                    return
                }

                mine.start()
                sendMessage(sender, "&7Mine started successfully.")
            }
            "create" -> { // /mine create <name> <required-rank> <reset-time>
                if (args.size != 3) {
                    sendMessage(sender, "&cInvalid usage. &f/mine create <name> <required-rank>")
                    return
                }
                val name = args[1]
                val requiredRank = args[2]

                if (core.files.mines.cfg.contains("mines.$name")) {
                    sendMessage(sender, "&cA mine with that name already exists.")
                    return
                }

                val mine = Mine(core, name, requiredRank, player.location, player.location, player.location, mutableListOf())
                mine.save()

                core.mines.add(mine)

                sendMessage(sender, "&7Mine created successfully. Use &b/mine setwarp $name &7to set the warp.")
            }
            "delete" -> { // /mine delete <name>
                if (args.size != 2) {
                    sendMessage(sender, "&cInvalid usage. &f/mine delete <name>")
                    return
                }
                val name = args[1]

                val mine = core.mines.find { it.name == name }
                if (mine == null) {
                    sendMessage(sender, "&cA mine with that name doesn't exist.")
                    return
                }

                mine.stop()
                core.mines.remove(mine)

                core.files.mines["mines.$name"] = null

                sendMessage(sender, "&7Mine deleted successfully.")
            }
            "status" -> { // /mine status <name>
                if (args.size != 2) {
                    sendMessage(sender, "&cInvalid usage. &f/mine status <name>")
                    return
                }
                val name = args[1]

                val mine = core.mines.find { it.name == name }
                if (mine == null) {
                    sendMessage(sender, "&cA mine with that name doesn't exist.")
                    return
                }

                sendMessage(sender, "&7&lMine Status:\n"
                        + "&7Name: &f${mine.name}\n"
                        + "&7Required Rank: &f${mine.requiredRank}\n"
                        + "&7Percent Mined: &f${100 - mine.checkPercentageEmpty()}")
            }
            "reset" -> { // /mine reset <name>
                if (args.size != 2) {
                    sendMessage(sender, "&cInvalid usage. &f/mine reset <name>")
                    return
                }
                val name = args[1]

                val mine = core.mines.find { it.name == name }
                if (mine == null) {
                    sendMessage(sender, "&cA mine with that name doesn't exist.")
                    return
                }

                mine.populateMine()

                sendMessage(sender, "&7Mine reset successfully.")
            }
            "empty" -> { // /mine empty <name>
                if (args.size != 2) {
                    sendMessage(sender, "&cInvalid usage. &f/mine empty <name>")
                    return
                }
                val name = args[1]

                val mine = core.mines.find { it.name == name }
                if (mine == null) {
                    sendMessage(sender, "&cA mine with that name doesn't exist.")
                    return
                }

                mine.emptyMine()

                sendMessage(sender, "&7Mine emptied successfully.")
            }
            "stop" -> { // /mine stop <name>
                if (args.size != 2) {
                    sendMessage(sender, "&cInvalid usage. &f/mine stop <name>")
                    return
                }
                val name = args[1]

                val mine = core.mines.find { it.name == name }
                if (mine == null) {
                    sendMessage(sender, "&cA mine with that name doesn't exist.")
                    return
                }
                mine.stop()

                sendMessage(sender, "&7Mine stopped successfully.")
            }
            "setwarp" -> { // /mine setwarp <name>
                if (args.size != 2) {
                    sendMessage(sender, "&cInvalid usage. &f/mine setwarp <name>")
                    return
                }
                val name = args[1]

                val mine = core.mines.find { it.name == name }
                if (mine == null) {
                    sendMessage(sender, "&cA mine with that name doesn't exist.")
                    return
                }

                mine.warp = player.location
                mine.save()

                sendMessage(sender, "&7Warp set successfully.")
            }
            "setcorner1" -> { // /mine setcorner1 <name>
                if (args.size != 2) {
                    sendMessage(sender, "&cInvalid usage. &f/mine setcorner1 <name>")
                    return
                }
                val name = args[1]

                val mine = core.mines.find { it.name == name }
                if (mine == null) {
                    sendMessage(sender, "&cA mine with that name doesn't exist.")
                    return
                }

                mine.corner1 = player.location
                mine.save()

                sendMessage(sender, "&7Corner 1 set successfully.")
            }
            "setcorner2" -> { // /mine setcorner2 <name>
                if (args.size != 2) {
                    sendMessage(sender, "&cInvalid usage. &f/mine setcorner2 <name>")
                    return
                }
                val name = args[1]

                val mine = core.mines.find { it.name == name }
                if (mine == null) {
                    sendMessage(sender, "&cA mine with that name doesn't exist.")
                    return
                }

                mine.corner2 = player.location
                mine.save()

                sendMessage(sender, "&7Corner 2 set successfully.")
            }
            "addblock" -> { // /mine addblock <name> <type> <spawn-chance> <value>
                if (args.size != 5) {
                    sendMessage(sender, "&cInvalid usage. &f/mine addblock <name> <type> <spawn-chance> <value>")
                    return
                }
                val name = args[1]
                val type = args[2]
                val spawnChance = args[3].toInt()
                val value = args[4].toLong()

                val mine = core.mines.find { it.name == name }
                if (mine == null) {
                    sendMessage(sender, "&cA mine with that name doesn't exist.")
                    return
                }

                val material = Material.getMaterial(type) ?: Material.STONE

                mine.addBlockType(material, spawnChance, value)
                mine.save()
                mine.populateMine()

                sendMessage(sender, "&7Block added successfully.")
            }
            "removeblock" -> { // /mine removeblock <name> <type>
                if (args.size != 3) {
                    sendMessage(sender, "&cInvalid usage. &f/mine removeblock <name> <type>")
                    return
                }
                val name = args[1]
                val type = args[2]

                val mine = core.mines.find { it.name == name }
                if (mine == null) {
                    sendMessage(sender, "&cA mine with that name doesn't exist.")
                    return
                }

                val material = Material.getMaterial(type) ?: Material.STONE

                mine.removeBlockType(material)
                mine.save()
                mine.populateMine()

                sendMessage(sender, "&7Block removed successfully.")
            }
            "list" -> { // /mine list
                sendMessage(sender, "&7&lMines:")
                for (mine in core.mines) {
                    sendMessage(sender, "&b${mine.name}")
                }
            }
            else -> sendMessage(sender, "&cInvalid subcommand.")
        }
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()

        // handle each completion option based on the help subcommand
        val name = when (args.size) {
            1 -> {
                listOf("help", "start", "stop", "create", "delete", "reset", "empty", "setwarp", "setcorner1", "setcorner2", "addblock", "removeblock", "list", "status")
            }
            2 -> {
                when (args[0]) {
                    "start", "stop", "setwarp", "setcorner1", "setcorner2", "addblock", "removeblock", "delete", "reset", "empty", "status" -> {
                        core.mines.map { it.name }
                    }
                    else -> listOf()
                }
            }
            3 -> {
                when (args[0]) {
                    "addblock", "removeblock" -> {
                        Material.entries.map { it.name }
                    }
                    else -> listOf()
                }
            }
            else -> listOf()
        }

        StringUtil.copyPartialMatches(args.last(), name, completions)

        return completions
    }
}