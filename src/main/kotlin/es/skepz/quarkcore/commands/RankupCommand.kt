package es.skepz.quarkcore.commands

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.sendMessage
import es.skepz.quarkcore.skepzlib.wrappers.CoreCMD
import es.skepz.quarkcore.utils.sendConfirmMsg
import org.bukkit.command.CommandSender

class RankupCommand(val core: QuarkCore) : CoreCMD(core, "rankup", "/rankup",
    0, "none", true, false) {

    override fun run() {
        val player = getPlayer() ?: return
        val file = core.userFiles[player.uniqueId] ?: return sendMessage(player, "&cAn error occurred while loading your data. Please relog.")

        val pRank = file.cfg.getString("prestige.rank") ?: return sendMessage(player, "&cAn error occurred while loading your data. Please relog.")
        val pLvl = file.cfg.getString("prestige.level") ?: return sendMessage(player, "&cAn error occurred while loading your data. Please relog.")

        // get rankup cost and next rank from prestige.yml
        val rankupCost = core.files.prestige.cfg.getInt("ranks.$pRank.next_cost")
        val nextRank = core.files.prestige.cfg.getString("ranks.$pRank.next_rank") ?:
            return sendMessage(player, "&cAn error occured trying to retrieve your next rank. Please contact an administrator.")

        // check if player has enough money
        val balance = file.getBal()
        if (balance < rankupCost) {
            return sendMessage(player, "&cYou don't have enough money to rankup! You need &b$rankupCost&c.")
        }

        // check if player is at max rank
        if (nextRank == "none") {
            // get the next prestige
            val nextPrestige = core.files.prestige.cfg.getString("prestiges.$pLvl.next") ?:
                return sendMessage(player, "&cYou are already max rank and max prestige!")

            // confirm that the user wants to prestige
            sendConfirmMsg(core, player,
                "&7Are you sure you want to prestige to &b$nextPrestige&7? This will reset your rank and level, and set your balance to 0!"
            ) { _, pl, confirmed ->
                if (confirmed) {
                    val f = core.userFiles[pl.uniqueId] ?:
                        return@sendConfirmMsg sendMessage(player, "&cAn error occurred while loading your data. Please relog.")

                    // set the player to the next prestige
                    f.setPrestigeLvl(nextPrestige)
                    // get the default rank and set the player to it
                    val defaultRank = core.files.prestige.cfg.getString("default_rank") ?:
                        return@sendConfirmMsg sendMessage(pl, "&cAn error occured trying to retrieve the default rank. Please contact an administrator.")
                    f.setRank(defaultRank)

                    // remove the money from the players account
                    f.setBal(0L)

                    // send message
                    sendMessage(pl,
                        "&7You have prestiged to &b$nextPrestige&7! Your rank is now &b$defaultRank&7, and your balance is &b0&7.")
                } else {
                    sendMessage(pl, "&7You have canceled your prestige.")
                }
            }
            return
        }

        // confirm that the user wants to rankup
        sendConfirmMsg(core, player,
            "&7Are you sure you want to rankup to &b$nextRank&7?"
        ) { _, pl, confirmed ->
            if (confirmed) {
                val f = core.userFiles[pl.uniqueId] ?:
                    return@sendConfirmMsg sendMessage(player, "&cAn error occurred while loading your data. Please relog.")

                // set the player to the next rank
                f.setRank(nextRank)

                // remove the money from the players account
                f.rmFromBal(rankupCost.toLong())

                // send message
                sendMessage(pl,
                    "&7You have ranked up to &b$nextRank&7!")
            } else {
                sendMessage(pl, "&7You have canceled your rankup.")
            }
        }

    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return emptyList()
    }

}