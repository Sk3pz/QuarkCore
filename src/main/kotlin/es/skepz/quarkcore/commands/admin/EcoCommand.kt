package es.skepz.quarkcore.commands.admin

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.sendMessage
import es.skepz.quarkcore.skepzlib.wrappers.CoreCMD
import es.skepz.quarkcore.utils.getOfflineUserFileRaw
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class EcoCommand(val core: QuarkCore) : CoreCMD(core, "eco", "/eco <give|take|set|check> <player> <amount>",
    2, "quarkcore.command.eco", false, true) {

    override fun run() {
        val mode = args[0]
        val target = args[1]

        // get the player if they exist, if not attempt to get offline player
        val targetPlayer = core.server.getPlayer(target) ?: core.server.getOfflinePlayer(target)

        // get the target's user file if it exists
        val file = getOfflineUserFileRaw(core, targetPlayer.uniqueId) ?:
            return sendMessage(sender, "&cThat player does not exist or has not played before!")

        when (mode) {
            "give" -> {
                if (args.size != 3) {
                    return sendMessage(sender, "&cInvalid usage. /eco give <player> <amount>")
                }
                val amount = args[2].toIntOrNull() ?: return sendMessage(sender, "&cInvalid amount.")
                file.cfg.set("balance", file.cfg.getInt("balance") + amount)
                file.save()
                sendMessage(sender, "&7You have given &b$target &7$&b$amount&7.")
            }
            "take" -> {
                if (args.size != 3) {
                    return sendMessage(sender, "&cInvalid usage. /eco give <player> <amount>")
                }
                val amount = args[2].toIntOrNull() ?: return sendMessage(sender, "&cInvalid amount.")
                file.cfg.set("balance", file.cfg.getInt("balance") - amount)
                file.save()
                sendMessage(sender, "&7You have taken $&b$amount &7from &b$target&7.")
            }
            "set" -> {
                if (args.size != 3) {
                    return sendMessage(sender, "&cInvalid usage. /eco give <player> <amount>")
                }
                val amount = args[2].toIntOrNull() ?: return sendMessage(sender, "&cInvalid amount.")
                file.cfg.set("balance", amount)
                file.save()
                sendMessage(sender, "&7You have set &b$target&7's balance to $&b$amount&7.")
            }
            "check" -> {
                sendMessage(sender, "&7$target&7's balance: $&b${file.cfg.getInt("balance")}&7.")
            }
            else -> {
                sendMessage(sender, "&cInvalid mode. Valid modes: give, take, set.")
            }
        }
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()

        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], listOf("set", "give", "take", "check"), completions)
        } else if (args.size == 2) {
            val players = org.bukkit.Bukkit.getServer().onlinePlayers
            val names = java.util.ArrayList<String>()
            for (p in players) {
                names.add(p.name)
            }
            StringUtil.copyPartialMatches(args[1], names, completions)
        }

        return completions
    }
}