package es.skepz.quarkcore.commands

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.sendMessage
import es.skepz.quarkcore.skepzlib.wrappers.CoreCMD
import es.skepz.quarkcore.utils.getUserFile
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.StringUtil

class SellCommand(val core: QuarkCore): CoreCMD(core, "sell", "/sell <hand|all>", 1,
    "none", true, true) {

    private fun sellItem(item: ItemStack): Long? {
        // check if the item has persistent data
        if (!item.hasItemMeta()) return null
        val itemMeta = item.itemMeta
        val itemData = itemMeta.persistentDataContainer

        if (!itemData.has(core.mineBlocKey)) return null

        val value = itemData.get(core.mineBlocKey, PersistentDataType.LONG) ?: return null

        return value * item.amount.toLong()
    }

    override fun run() {
        val player = getPlayer()!!

        when (args[0]) {
            "hand" -> {
                val item = player.inventory.itemInMainHand

                val soldFor = sellItem(item) ?: return sendMessage(player, "&cThat item can't be sold!")

                player.inventory.removeItem(item)

                val display = item.itemMeta.displayName()

                val name = if (display != null) {
                    PlainTextComponentSerializer.plainText().serialize(display)
                } else {
                    item.type.name
                }

                sendMessage(player, "&7Sold &b${name} &7for &b$soldFor")

                // get the player's file
                val file = getUserFile(core, player)
                file.addToBal(soldFor)
            }
            "all" -> {
                var total = 0L
                var sold = false
                for (item in player.inventory.storageContents) {
                    if (item == null) continue
                    val soldFor = sellItem(item) ?: continue

                    player.inventory.removeItem(item)

                    total += soldFor
                    sold = true
                }

                if (!sold) return sendMessage(player, "&cYou don't have any sellable items!")

                // get the player's file
                val file = getUserFile(core, player)
                file.addToBal(total)

                sendMessage(player, "&7Sold all items for &b$total")
            }
        }

    }

    override fun registerTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        val completions = mutableListOf<String>()
        val list = listOf("hand", "all")
        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], list, completions)
        }

        return completions
    }

}