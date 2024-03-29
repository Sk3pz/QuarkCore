package es.skepz.quarkcore.commands.admin

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.sendMessage
import es.skepz.quarkcore.skepzlib.wrappers.CoreCMD
import es.skepz.quarkcore.utils.getUserFile
import es.skepz.quarkcore.utils.refreshPermissions
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class RankCommand(val core: QuarkCore) : CoreCMD(core, "rank", "/rank <create|edit|set|delete|list> <args>",
    2, "quarkcore.command.rank", false, true) {
    override fun run() {

        when (args[0]) {
            "create" -> {
                if (args.size != 2) {
                    sendMessage(sender, "&cInvalid usage. /rank create <name>")
                    return
                }

                val rank = args[1]
                if (core.files.ranks.cfg.contains("ranks.$rank")) {
                    sendMessage(sender, "&cRank already exists.")
                    return
                }

                // create the rank
                core.files.ranks["ranks.$rank.prefix"] = ""
                core.files.ranks["ranks.$rank.nameColor"] = "&8"
                core.files.ranks["ranks.$rank.permissions"] = listOf("")
                sendMessage(sender, "&7Rank created.")
            }
            "set" -> {
                if (args.size != 3) {
                    sendMessage(sender, "&cInvalid usage. /rank set <player> <rank>")
                    return
                }
                val player = core.server.getPlayer(args[1])
                if (player == null) {
                    sendMessage(sender, "&cPlayer not found.")
                    return
                }

                val rank = args[2]
                if (!core.files.ranks.cfg.contains("ranks.$rank")) {
                    sendMessage(sender, "&cRank not found.")
                    return
                }

                val file = getUserFile(core, player)

                file.setRank(rank)
                refreshPermissions(core, player)
                sendMessage(sender, "&7Rank set.")
                // send a message to the player
                sendMessage(player, "&7Your rank has been set to &b$rank")
            }
            "edit" -> {
                // edit <rank> <prefix|nameColor|permissions> <value>
                if (args.size < 4) {
                    sendMessage(sender, "&cInvalid usage.")
                    return
                }

                val rank = args[1]
                if (!core.files.ranks.cfg.contains("ranks.$rank")) {
                    sendMessage(sender, "&cRank not found. do &f/rank create <name> &cto create a new rank.")
                    return
                }

                val value = args[2]
                when (value) {
                    "prefix" -> {
                        // edit <rank> prefix <value>
                        if (args.size != 4) {
                            sendMessage(sender, "&cInvalid usage.")
                            return
                        }
                        val prefix = args[3]
                        core.files.ranks["ranks.$rank.prefix"] = prefix
                        sendMessage(sender, "&7Prefix set.")
                    }
                    "nameColor" -> {
                        // edit <rank> nameColor <value>
                        if (args.size != 4) {
                            sendMessage(sender, "&cInvalid usage.")
                            return
                        }
                        val nameColor = args[3]
                        core.files.ranks["ranks.$rank.nameColor"] = nameColor
                        sendMessage(sender, "&7Name color set.")
                    }
                    "permissions" -> {
                        // edit <rank> permissions <add|remove> <permission>
                        if (args.size != 5) {
                            sendMessage(sender, "&cInvalid usage.")
                            return
                        }
                        val action = args[3]
                        val permission = args[4]
                        val permissions = core.files.ranks.cfg.getStringList("ranks.$rank.permissions")
                        when (action) {
                            "add" -> {
                                permissions.add(permission)
                                core.files.ranks["ranks.$rank.permissions"] = permissions
                                sendMessage(sender, "&7Permission added.")
                            }
                            "remove" -> {
                                permissions.remove(permission)
                                core.files.ranks["ranks.$rank.permissions"] = permissions
                                sendMessage(sender, "&7Permission removed.")
                            }
                            else -> sendMessage(sender, "&cInvalid action.")
                        }
                    }
                    else -> sendMessage(sender, "&cInvalid value.")
                }
            }
            "delete" -> {
                val rank = args[1]
                if (!core.files.ranks.cfg.contains("ranks.$rank")) {
                    sendMessage(sender, "&cRank not found. do /rank create <name> to create a new rank.")
                    return
                }
                core.files.ranks["ranks.$rank"] = null
                sendMessage(sender, "&7Rank deleted.")

            }
            "list" -> {
                val ranks = core.files.ranks.cfg.getConfigurationSection("ranks")?.getKeys(false) ?: emptySet()
                sendMessage(sender, "&7Ranks: &b${ranks.joinToString("&7, &b")}")
            }
            else -> sendMessage(sender, "&cInvalid subcommand.")
        }
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()

        var names = when (args.size) {
            1 -> {
                listOf("create", "edit", "set", "delete", "list")
            }
            2 -> {
                when (args[0]) {
                    "create" -> emptyList()
                    "edit" -> {
                        val ranks = core.files.ranks.cfg.getConfigurationSection("ranks")?.getKeys(false) ?: emptySet()
                        ranks.toList()
                    }
                    "set" -> {
                        core.server.onlinePlayers.map { it.name }
                    }
                    "delete" -> {
                        val ranks = core.files.ranks.cfg.getConfigurationSection("ranks")?.getKeys(false) ?: emptySet()
                        ranks.toList()
                    }
                    "list" -> emptyList()
                    else -> emptyList()
                }
            }
            3 -> {
                when (args[0]) {
                    "create" -> emptyList()
                    "edit" -> {
                        val rank = args[1]
                        if (!core.files.ranks.cfg.contains("ranks.$rank")) {
                            emptyList()
                        } else {
                            listOf("prefix", "nameColor", "permissions")
                        }
                    }
                    "set" -> {
                        val ranks = core.files.ranks.cfg.getConfigurationSection("ranks")?.getKeys(false) ?: emptySet()
                        ranks.toList()
                    }
                    "delete" -> emptyList()
                    "list" -> emptyList()
                    else -> emptyList()
                }
            }
            4 -> {
                when (args[0]) {
                    "create" -> emptyList()
                    "edit" -> {
                        val rank = args[1]
                        if (!core.files.ranks.cfg.contains("ranks.$rank")) {
                            emptyList()
                        } else {
                            val value = args[2]
                            when (value) {
                                "prefix" -> emptyList()
                                "nameColor" -> emptyList()
                                "permissions" -> listOf("add", "remove")
                                else -> emptyList()
                            }
                        }
                    }
                    "set" -> emptyList()
                    "delete" -> emptyList()
                    "list" -> emptyList()
                    else -> emptyList()
                }
            }
            else -> emptyList()
        }

        StringUtil.copyPartialMatches(args.last(), names, completions)

        return completions
    }
}