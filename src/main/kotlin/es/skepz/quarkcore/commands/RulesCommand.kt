package es.skepz.quarkcore.commands

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.sendMessage
import es.skepz.quarkcore.skepzlib.wrappers.CoreCMD
import org.bukkit.command.CommandSender

class RulesCommand(val core: QuarkCore) : CoreCMD(core, "rules", "/rules", 0,
    "none", true, false) {

    override fun run() {
        val player = getPlayer()!!

        // get the rules
        val rules = core.files.rules.cfg.getStringList("rules")

        // send the rules to the player
        val listDisplay = rules.joinToString("\n") { "&7- &f$it" }

        sendMessage(player, "&7&lRules:\n$listDisplay")
    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return emptyList()
    }
}