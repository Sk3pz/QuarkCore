package es.skepz.quarkcore.files

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.wrappers.CFGFile
import java.io.File

class ServerFiles(val plugin: QuarkCore) {

    var config: CFGFile = CFGFile(plugin, "config", "")
    var ranks: CFGFile
    var prestige: CFGFile
    var rules: CFGFile
    var filter: CFGFile
    var warps: CFGFile
    var mines: CFGFile

    var data: CFGFile

    fun reload() {
        config.reload()

        if (!ranks.exists())
            plugin.saveResource("ranks.yml", false)
        if (!prestige.exists())
            plugin.saveResource("prestige.yml", false)
        ranks.reload()
        prestige.reload()

        rules.reload()
        filter.reload()
        warps.reload()
        data.reload()
        mines.reload()
    }

    fun restore() {
        if (!plugin.dataFolder.exists()) plugin.dataFolder.mkdir()

        reload()

        config.default("prison-world", "prison")
        config.default("survival-world", "world")
        config.default("mine-reset-percent", 40)
        config.default("op-override-filter", true)
        config.default("can-place-in-mines", false)

        rules.default("rules", listOf("Be polite and respectful", "No cheating or using unfair game advantages",
            "No leaking personal information", "No arguing with the staff team", "No begging for a role"))

        filter.default("words", listOf("nigger", "nig", "bitch", "fuck", "ass", "nigga", "shit",
            "damn", "cunt", "clit", "dick", "penis", "cock", "vagina", "hell", "faggot", "boob"))
    }

    init {
        var file = File(plugin.dataFolder.toString(), "ranks.yml")
        if (!file.exists())
            plugin.saveResource("ranks.yml", false)
        file = File(plugin.dataFolder.toString(), "prestige.yml")
        if (!file.exists())
            plugin.saveResource("prestige.yml", false)
        prestige = CFGFile(plugin, "prestige", "")
        ranks = CFGFile(plugin,    "ranks",    "")

        rules = CFGFile(plugin,  "rules",  "")
        filter = CFGFile(plugin, "filter", "")

        data = CFGFile(plugin,  "data",  "ds")
        warps = CFGFile(plugin, "warps", "ds")
        mines = CFGFile(plugin, "mines", "ds")

        restore()
    }

    fun getPrestigeLvlDisplay(lvl: String): String {
        return prestige.cfg.getString("levels.$lvl.prefix") ?: "&c&l"
    }

    fun getPrestigeRankColor(rank: String): String {
        return prestige.cfg.getString("ranks.$rank.chat_color") ?: "&c&l"
    }
}