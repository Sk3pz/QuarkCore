package es.skepz.quarkcore.files

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.tuodlib.wrappers.CFGFile

class ServerFiles(val plugin: QuarkCore) {

    var config: CFGFile
    var ranks: CFGFile
    var prestige: CFGFile
    var rules: CFGFile
    var filter: CFGFile
    var warps: CFGFile

    var data: CFGFile

    fun reload() {
        config.reload()
    }

    fun reloadConfigs() {
        config = CFGFile(plugin, "config", "")
        ranks = CFGFile(plugin, "ranks", "")
        prestige = CFGFile(plugin, "prestige", "")
        rules = CFGFile(plugin, "rules",  "")
        filter = CFGFile(plugin, "filter", "")
        data = CFGFile(plugin, "data",   "ds")
        warps = CFGFile(plugin, "warps",   "ds")

        reload()
    }

    fun restore() {
        if (!plugin.dataFolder.exists()) plugin.dataFolder.mkdir()

        reloadConfigs()

        config.default("prison-world", "prison")
        config.default("survival-world", "world")
        config.default("op-override-filter", true)
        config.default("warps.shorten-coords", false)

        ranks.default("default-rank", "default")

        defaultPrestige()

        // todo: default rank permissions
        ranks.default("ranks.default.prefix", "") // no prefix for default rank
        ranks.default("ranks.default.nameColor", "&8")
        ranks.default("ranks.default.permissions", listOf(""))

        ranks.default("ranks.helper.prefix", "&2&lHelper")
        ranks.default("ranks.helper.nameColor", "&a")
        ranks.default("ranks.helper.permissions", listOf(""))

        ranks.default("ranks.mod.prefix", "&9&lMod")
        ranks.default("ranks.mod.nameColor", "&3")
        ranks.default("ranks.mod.permissions", listOf(""))

        ranks.default("ranks.admin.prefix", "&4&lAdmin")
        ranks.default("ranks.admin.nameColor", "&c")
        ranks.default("ranks.admin.permissions", listOf("*"))

        ranks.default("ranks.dev.prefix", "&3&lDev")
        ranks.default("ranks.dev.nameColor", "&b")
        ranks.default("ranks.dev.permissions", listOf("*"))

        rules.default("rules", listOf("Be polite and respectful", "No cheating or using unfair game advantages",
            "No leaking personal information", "No arguing with the staff team", "No begging for a role"))

        filter.default("words", listOf("nigger", "nig", "bitch", "fuck", "ass", "nigga", "shit",
            "damn", "cunt", "clit", "dick", "penis", "cock", "vagina", "hell", "faggot", "boob"))
    }

    init {
        config = CFGFile(plugin, "config", "")
        ranks = CFGFile(plugin, "ranks", "")
        prestige = CFGFile(plugin, "prestige", "")
        rules = CFGFile(plugin, "rules",  "")
        filter = CFGFile(plugin, "filter", "")
        data = CFGFile(plugin, "data",   "ds")
        warps = CFGFile(plugin, "warps",   "ds")

        restore()
    }

    fun defaultPrestige() {
        prestige.default("default_level", "0")

        prestige.default("levels.zero", "&8&l0")
        prestige.default("levels.one", "&7&l1")
        prestige.default("levels.two", "&a&l2")
        prestige.default("levels.three", "&d&l3")
        prestige.default("levels.four", "&c&l4")
        prestige.default("levels.five", "&e&l5")
        prestige.default("levels.free", "&6&lFree")

        val defaultPrestige = "abcdefghijklmnopqrstuvwxyz"
        val chars = defaultPrestige.toCharArray()

        for (x in defaultPrestige.indices) {
            val c = chars[x]
            val next = if (x == defaultPrestige.length - 1) {
                chars.first()
            } else {
                chars[x + 1]
            }

            // calculate a reasonable cost for each rank based on x
            val cost = (x + 1) * 1000L

            prestige.default("ranks.$c.chat_color", "&c&l")
            prestige.default("ranks.$c.next", next)
            prestige.default("ranks.$c.next_cost", cost)
        }
    }

    fun getPrestigeLvlDisplay(lvl: String): String {
        return prestige.cfg.getString("levels.$lvl") ?: "&c&l"
    }

    fun getPrestigeRankColor(rank: String): String {
        return prestige.cfg.getString("ranks.$rank.chat_color") ?: "&c&l"
    }
}