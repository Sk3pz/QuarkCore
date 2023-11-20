package es.skepz.quarkcore.commands.economy

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.sendMessage
import es.skepz.quarkcore.skepzlib.wrappers.CoreCMD
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class PayCommand(val core: QuarkCore) : CoreCMD(core, "pay", "/pay <player> <amount>",
    2, "none", true, true) {

    override fun run() {
        val player = getPlayer() ?: return
        val target = args[0]

        // get the player if they exist
        val targetPlayer = core.server.getPlayer(target) ?:
            return sendMessage(sender, "&cThat player does not exist or is not online!")

        // get the amount specified by the user
        val amount = args[1].toLongOrNull() ?: return sendMessage(sender, "&cInvalid amount.")

        // get the player's user file
        val file = core.userFiles[player.uniqueId] ?: return sendMessage(sender, "&cCould not load your user file. Please relog.")

        // get the target's user file
        val targetFile = core.userFiles[targetPlayer.uniqueId] ?: return sendMessage(sender, "&cFailed to retrieve file information.")

        val balance = file.getBal()

        if (amount > balance) {
            return sendMessage(sender, "&cYou do not have enough money to do that.")
        }

        targetFile.addToBal(amount)
        file.rmFromBal(amount)
        sendMessage(sender, "&7You have sent &b$target &7$&b$amount&7.")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()

        val players = org.bukkit.Bukkit.getServer().onlinePlayers
        val names = java.util.ArrayList<String>()
        for (p in players) {
            names.add(p.name)
        }
        StringUtil.copyPartialMatches(args[0], names, completions)

        return completions
    }
}