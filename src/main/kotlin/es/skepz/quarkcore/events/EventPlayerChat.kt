package es.skepz.quarkcore.events

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.files.UserFile
import es.skepz.quarkcore.skepzlib.colorize
import es.skepz.quarkcore.skepzlib.invalid
import es.skepz.quarkcore.skepzlib.serverBroadcast
import es.skepz.quarkcore.skepzlib.wrappers.CoreEvent
import es.skepz.quarkcore.utils.isFree
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import java.util.*

class EventPlayerChat(private val quark: QuarkCore): CoreEvent(quark) {

    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        event.isCancelled = true
        var msgPlain = PlainTextComponentSerializer.plainText().serialize(event.message())

        val player = event.player
        if (quark.userFiles[player.uniqueId] == null) {
            quark.userFiles[player.uniqueId] = UserFile(quark, player)
        }

        // check if confirming, if so run the confirm function
        if (quark.confirmMap.containsKey(player.uniqueId)) {
            val confirm = quark.confirmMap[player.uniqueId]!!
            quark.confirmMap.remove(player.uniqueId)
            val lowermsg = msgPlain.lowercase()
            val confirmed = lowermsg == "confirm" || lowermsg == "c"
                    || lowermsg == "yes" || lowermsg == "y"
            confirm(quark, player, confirmed)
            return
        }

        // mute check
        val file = quark.userFiles[player.uniqueId]!!
        if (file.isMuted()) {
            invalid(player, "&c&lCannot send message!", "&cYou are muted in this chat.",
                "&cReason: &f${file.muteReason()}\n" +
                        (if (file.muteSender() == "none") "" else "&cMuted by: &f${file.muteSender()}\n") +
                        (if (file.muteTime() == -1L) "&cThis mute is permanent." else "&cMuted until: &f${file.mutedUntil()}"))
            return
        }

        val rank = file.getRank()
        val rankPrefix = quark.files.ranks.cfg.getString("ranks.$rank.prefix") ?: ""
        val nameColor = quark.files.ranks.cfg.getString("ranks.$rank.nameColor") ?: "&8"

        val prestigeRank = file.getPrestigeRank()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        val pRankColor = quark.files.getPrestigeRankColor(prestigeRank)

        val pLvl = file.getPrestigeLvl()
        val prestigeLvlDisplay = quark.files.getPrestigeLvlDisplay(pLvl)

        // filter chat
        if (!(player.isOp && quark.files.config.cfg.getBoolean("op-override-filter"))) {
            val filteredWords = quark.files.filter.cfg.getStringList("words")
            for (word in filteredWords) {
                // check if the msgPlain contains a filtered word
                if (msgPlain.contains(word)) {
                    // replace the filtered word with asterisks
                    msgPlain = msgPlain.replace(word, "*".repeat(word.length))
                }
            }
        }

        val prestige = if (isFree(quark, player)) prestigeLvlDisplay else "$prestigeLvlDisplay$pRankColor$prestigeRank"

        val prefix = if (rankPrefix.isEmpty()) {
            "$prestige $nameColor"
        } else {
            "$prestige $rankPrefix $nameColor"
        }

        serverBroadcast("$prefix${player.name} &7> &f${colorize(msgPlain)}")
    }

}