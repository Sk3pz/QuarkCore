package es.skepz.quarkcore.commands.admin.punish

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.files.UserFile
import es.skepz.quarkcore.skepzlib.colorize
import es.skepz.quarkcore.skepzlib.sendMessage
import es.skepz.quarkcore.skepzlib.wrappers.CoreCMD
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import java.util.ArrayList

class KickCommand(val core: QuarkCore) : CoreCMD(core, "kick", "/kick <name> <reason?>", 1,
    "quarkcore.command.kick", false, true) {

    override fun run() {
        // get the player or offline player from the first argument
        val target = args[0]
        val senderName = if (sender is Player)
            PlainTextComponentSerializer.plainText().serialize((sender as Player).displayName())
        else "Console"

        val reason = if (args.size > 1) {
            args.removeAt(0)
            args.joinToString(" ")
        } else {
            "No reason provided"
        }

        val targetPlayer = core.server.getPlayer(target) ?: return sendMessage(sender, "&cThat player is not online!")

        // get the target's file
        val file = UserFile(core, targetPlayer)

        // check the player's rank
        val rank = file.getRank()

        // check if the player's rank has isOp set, or if they have the * permission
        val isOp = core.files.ranks.cfg.getBoolean("ranks.$rank.isOp")
        val permissions = core.files.ranks.cfg.getStringList("ranks.$rank.permissions")
        if ((permissions.contains("*") || isOp)
            && !sender.hasPermission("quarkcore.punish-restriction-bypass") && !sender.hasPermission("*")) {
            sender.sendMessage("&cYou cannot kick this player!")
            return
        }

        // add a kick to the user's file
        file.addKick()

        // kick the player if they are online
        targetPlayer.kick(
                Component.text(
                    colorize("&cYou are kicked from this server!\n" +
                            "&cReason: &f$reason\n" +
                            "&cKicked by: &f$senderName\n")))

        sendMessage(sender, "&7You have kicked &b$target &7for &b$reason&7.")
        Bukkit.getLogger().severe("$senderName has kicked $target for $reason")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()

        val players = Bukkit.getServer().onlinePlayers
        val names = ArrayList<String>()
        for (p in players) {
            names.add(p.name)
        }

        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], names, completions)
        }
        return completions
    }
}