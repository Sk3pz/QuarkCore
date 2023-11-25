package es.skepz.quarkcore.commands

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.sendMessage
import es.skepz.quarkcore.skepzlib.serverBroadcast
import es.skepz.quarkcore.skepzlib.wrappers.CoreCMD
import es.skepz.quarkcore.utils.getUserFile
import es.skepz.quarkcore.utils.getWarp
import es.skepz.quarkcore.utils.isFree
import es.skepz.quarkcore.utils.sendConfirmMsg
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import java.util.*
import kotlin.math.max

class RankupCommand(val core: QuarkCore) : CoreCMD(core, "rankup", "/rankup",
    0, "none", true, false) {

    override fun run() {
        val player = getPlayer() ?: return
        val file = getUserFile(core, player)

        val pRank = file.cfg.getString("prestige.rank") ?: return sendMessage(player, "&cAn error occurred while loading your data. Please relog.")
        val pLvl = file.cfg.getString("prestige.level") ?: return sendMessage(player, "&cAn error occurred while loading your data. Please relog.")

        if (isFree(core, player)) {
            return sendMessage(player, "&cYou are already free!")
        }

        // get the current prestige level as an int from prestige.yml levels.<prestige>.raw
        val pLvlIntRaw = core.files.prestige.cfg.getString("levels.$pLvl.raw") ?: "0"
        val pLvlInt = pLvlIntRaw.toIntOrNull() ?: 0

        // get rankup cost and next rank from prestige.yml
        val rankupCostBase = core.files.prestige.cfg.getInt("ranks.$pRank.next_cost")
        val cfgMultiplier = core.files.prestige.cfg.getInt("prestige_multiplier")

        val multiplier = max(pLvlInt * cfgMultiplier, 1)

        val rankupCost = rankupCostBase * multiplier
        val nextRank = core.files.prestige.cfg.getString("ranks.$pRank.next") ?:
            return sendMessage(player, "&cAn error occured trying to retrieve your next rank. Please contact an administrator.")

        // check if player has enough money
        val balance = file.getBal()
        if (balance < rankupCost) {
            return sendMessage(player, "&cYou don't have enough money to rankup! You need &f$rankupCost&c.")
        }

        // check if player is at max rank
        if (nextRank == "none") {
            // get the next prestige
            val nextPrestige = core.files.prestige.cfg.getString("levels.$pLvl.next") ?:
                return sendMessage(player, "&cYou are already free!")
            val displayNxt = core.files.prestige.cfg.getString("levels.$nextPrestige.raw") ?: nextPrestige

            // confirm that the user wants to prestige
            sendConfirmMsg(core, player,
                "&cYou are about to prestige for $&b$rankupCost&7.\nThis will reset your rank and balance. Are you sure?"
            ) { _, pl, confirmed ->
                if (confirmed) {
                    val f = getUserFile(core, pl)

                    // set the player to the next prestige
                    f.setPrestigeLvl(nextPrestige)
                    // get the default rank and set the player to it
                    val defaultRank = core.files.prestige.cfg.getString("default_rank") ?:
                        return@sendConfirmMsg sendMessage(pl, "&cAn error occurred trying to retrieve the default rank. Please contact an administrator.")
                    f.setPrestigeRank(defaultRank)

                    // remove the money from the players account
                    f.setBal(0L)

                    // check if the new prestige is the last one, and if so send them to warp survival
                    if (core.files.prestige.cfg.getString("levels.$nextPrestige.next") == null) {
                        // they are free
                        sendMessage(pl, "&7You are now free! Enjoy survival!")
                        serverBroadcast("&e&l${pl.name}&6&l has been set free!")

                        // get the survival world
                        val loc = getWarp(core, "survival")!!
                        pl.teleport(loc)
                    } else {
                        // send message
                        sendMessage(pl, "&7You have prestiged to &b$displayNxt&7! Your rank is now &b$defaultRank&7, and your balance is &b0&7.")
                        serverBroadcast("&b${pl.name}&7 has prestiged to &b$displayNxt&7!")
                    }
                } else {
                    sendMessage(pl, "&7You have canceled your prestige.")
                }
            }
            return
        }

        // confirm that the user wants to rankup
        val nextDisplay = nextRank.uppercase()

        core.files.prestige.cfg.getString("levels.$pLvl.next") ?:
            return sendMessage(player, "&cYou are already max prestige!")

        sendConfirmMsg(core, player,
            "&7Are you sure you want to rankup to &b$nextDisplay&7 for $&b$rankupCost&7?"
        ) { _, pl, confirmed ->
            if (confirmed) {
                val f = getUserFile(core, pl)

                // set the player to the next rank
                f.setPrestigeRank(nextRank)

                // remove the money from the players account
                f.rmFromBal(rankupCost.toLong())

                // send message
                sendMessage(pl,
                    "&7You have ranked up to &b$nextDisplay&7!")
            } else {
                sendMessage(pl, "&7You have canceled your rankup.")
            }
        }

    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return emptyList()
    }

}