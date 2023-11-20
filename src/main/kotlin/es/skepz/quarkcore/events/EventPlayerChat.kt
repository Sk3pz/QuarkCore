package es.skepz.quarkcore.events

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.files.UserFile
import es.skepz.quarkcore.tuodlib.colorize
import es.skepz.quarkcore.tuodlib.invalid
import es.skepz.quarkcore.tuodlib.serverBroadcast
import es.skepz.quarkcore.tuodlib.wrappers.CoreEvent
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import java.util.*

class EventPlayerChat(private val quark: QuarkCore): CoreEvent(quark) {

    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        event.isCancelled = true

        val player = event.player
        if (quark.userFiles[player.uniqueId] == null) {
            quark.userFiles[player.uniqueId] = UserFile(quark, player)
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

        var msgPlain = PlainTextComponentSerializer.plainText().serialize(event.message())

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

        val prestige = if (pLvl == "free") prestigeLvlDisplay else "$prestigeLvlDisplay$pRankColor$prestigeRank"

        if (rankPrefix.isEmpty()) {
            serverBroadcast("$prestige $nameColor${player.name} &7> &f${colorize(msgPlain)}")
        } else {
            serverBroadcast("$prestige $rankPrefix $nameColor${player.name} &7> &f${colorize(msgPlain)}")
        }
    }

}