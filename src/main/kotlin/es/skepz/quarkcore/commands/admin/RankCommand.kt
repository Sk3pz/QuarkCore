package es.skepz.quarkcore.commands.admin

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.sendMessage
import es.skepz.quarkcore.skepzlib.wrappers.CoreCMD
import es.skepz.quarkcore.utils.refreshPermissions
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class RankCommand(val core: QuarkCore) : CoreCMD(core, "rank", "/rank <create|edit|set|delete|list> <args>",
    2, "quarkcore.rank", false, true) {
    override fun run() {

        when (args[0]) {
            "create" -> {
                if (args.size != 2) {
                    sender.sendMessage("§cInvalid usage. /rank create <name>")
                    return
                }

                val rank = args[1]
                if (core.files.ranks.cfg.contains("ranks.$rank")) {
                    sender.sendMessage("§cRank already exists.")
                    return
                }

                // create the rank
                core.files.ranks["ranks.$rank.prefix"] = ""
                core.files.ranks["ranks.$rank.nameColor"] = "&8"
                core.files.ranks["ranks.$rank.permissions"] = listOf("")
                sendMessage(sender, "§aRank created.")
            }
            "set" -> {
                if (args.size != 3) {
                    sender.sendMessage("§cInvalid usage.")
                    return
                }
                val player = core.server.getPlayer(args[1])
                if (player == null) {
                    sender.sendMessage("§cPlayer not found.")
                    return
                }

                val rank = args[2]
                if (!core.files.ranks.cfg.contains("ranks.$rank")) {
                    sender.sendMessage("§cRank not found.")
                    return
                }

                val file = core.userFiles[player.uniqueId] ?: return sendMessage(sender, "&cFailed to get target's user file.")

                file.setRank(rank)
                refreshPermissions(core, player)
                sender.sendMessage("§aRank set.")
            }
            "edit" -> {
                // edit <rank> <prefix|nameColor|permissions> <value>
                if (args.size < 4) {
                    sender.sendMessage("§cInvalid usage.")
                    return
                }

                val rank = args[1]
                if (!core.files.ranks.cfg.contains("ranks.$rank")) {
                    sender.sendMessage("§cRank not found. do /rank create <name> to create a new rank.")
                    return
                }

                val value = args[2]
                when (value) {
                    "prefix" -> {
                        // edit <rank> prefix <value>
                        if (args.size != 4) {
                            sender.sendMessage("§cInvalid usage.")
                            return
                        }
                        val prefix = args[3]
                        core.files.ranks["ranks.$rank.prefix"] = prefix
                        sender.sendMessage("§aPrefix set.")
                    }
                    "nameColor" -> {
                        // edit <rank> nameColor <value>
                        if (args.size != 4) {
                            sender.sendMessage("§cInvalid usage.")
                            return
                        }
                        val nameColor = args[3]
                        core.files.ranks["ranks.$rank.nameColor"] = nameColor
                        sender.sendMessage("§aName color set.")
                    }
                    "permissions" -> {
                        // edit <rank> permissions <add|remove> <permission>
                        if (args.size != 5) {
                            sender.sendMessage("§cInvalid usage.")
                            return
                        }
                        val action = args[3]
                        val permission = args[4]
                        val permissions = core.files.ranks.cfg.getStringList("ranks.$rank.permissions")
                        when (action) {
                            "add" -> {
                                permissions.add(permission)
                                core.files.ranks["ranks.$rank.permissions"] = permissions
                                sender.sendMessage("§aPermission added.")
                            }
                            "remove" -> {
                                permissions.remove(permission)
                                core.files.ranks["ranks.$rank.permissions"] = permissions
                                sender.sendMessage("§aPermission removed.")
                            }
                            else -> sender.sendMessage("§cInvalid action.")
                        }
                    }
                    else -> sender.sendMessage("§cInvalid value.")
                }
            }
            "delete" -> {
                val rank = args[1]
                if (!core.files.ranks.cfg.contains("ranks.$rank")) {
                    sender.sendMessage("§cRank not found. do /rank create <name> to create a new rank.")
                    return
                }
                core.files.ranks["ranks.$rank"] = null
                sender.sendMessage("§aRank deleted.")

            }
            "list" -> {
                val ranks = core.files.ranks.cfg.getConfigurationSection("ranks")?.getKeys(false) ?: emptySet()
                sendMessage(sender, "§7Ranks: §b${ranks.joinToString("&7, &b")}")
            }
            else -> sender.sendMessage("§cInvalid subcommand.")
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