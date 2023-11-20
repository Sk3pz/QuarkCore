package es.skepz.quarkcore.files

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.wrappers.CFGFile

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

    private fun defaultPrestige() {
        prestige.default("default_level", "0")
        prestige.default("default_rank", "a")

        prestige.default("levels.zero.prefix", "&8&l0")
        prestige.default("levels.zero.next", "one")
        prestige.default("levels.zero.raw", "0")

        prestige.default("levels.one.prefix", "&7&l1")
        prestige.default("levels.one.next", "two")
        prestige.default("levels.one.raw", "1")

        prestige.default("levels.two.prefix", "&a&l2")
        prestige.default("levels.two.next", "three")
        prestige.default("levels.two.raw", "2")

        prestige.default("levels.three.prefix", "&d&l3")
        prestige.default("levels.three.next", "four")
        prestige.default("levels.three.raw", "3")

        prestige.default("levels.four.prefix", "&b&l4")
        prestige.default("levels.four.next", "five")
        prestige.default("levels.four.raw", "4")

        prestige.default("levels.five.prefix", "&e&l5")
        prestige.default("levels.five.next", "free")
        prestige.default("levels.five.raw", "5")

        prestige.default("levels.free.prefix", "&6&lFree")
        prestige.default("levels.free.raw", "Free")

        val defaultPrestige = "abcdefghijklmnopqrstuvwxyz"
        val chars = defaultPrestige.toCharArray()

        for (x in defaultPrestige.indices) {
            val c = chars[x]
            val next = if (x == defaultPrestige.length - 1) {
                "none"
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