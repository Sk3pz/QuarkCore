package es.skepz.quarkcore.mine

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.entity.Player

class Mine(val core: QuarkCore, val name: String, var requiredRank: String,
           var warp: Location,
           var corner1: Location, var corner2: Location,
           private val blocks: MutableList<MineBlock>) {

    companion object {
        fun loadFromFile(core: QuarkCore, name: String): Mine? {
            // load the mine from the file
            val file = core.files.mines.cfg

            if (!file.contains("mines.$name")) return null

            val requiredRank = file.getString("mines.$name.required-rank") ?: "a"

            val warp = Location(core.server.getWorld(file.getString("mines.$name.warp.world") ?: "world"),
                file.getDouble("mines.$name.warp.x"),
                file.getDouble("mines.$name.warp.y"),
                file.getDouble("mines.$name.warp.z"),
                file.getDouble("mines.$name.warp.yaw").toFloat(),
                file.getDouble("mines.$name.warp.pitch").toFloat())

            val corner1 = Location(core.server.getWorld(file.getString("mines.$name.corner1.world") ?: "world"),
                file.getDouble("mines.$name.corner1.x"),
                file.getDouble("mines.$name.corner1.y"),
                file.getDouble("mines.$name.corner1.z"))

            val corner2 = Location(core.server.getWorld(file.getString("mines.$name.corner2.world") ?: "world"),
                file.getDouble("mines.$name.corner2.x"),
                file.getDouble("mines.$name.corner2.y"),
                file.getDouble("mines.$name.corner2.z"))

            val blocks = mutableListOf<MineBlock>()

            val configSelection = file.getConfigurationSection("mines.$name.blocks")?.getKeys(false) ?: emptySet()

            for (block in configSelection) {
                val type = Material.valueOf(block)
                val spawnChance = file.getInt("mines.$name.blocks.$block.spawn-chance")
                val value = file.getLong("mines.$name.blocks.$block.value")

                blocks.add(MineBlock(type, spawnChance, value))
            }

            return Mine(core, name, requiredRank, warp, corner1, corner2, blocks)
        }
    }

    private var percentageResetScheduler: Int? = null

    fun start() {
        populateMine()

        val resetPercent = core.files.config.cfg.getInt("mine-reset-percent")

        percentageResetScheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(core, {
            // reset the mine if it's empty
            if (100 - checkPercentageEmpty() >= resetPercent) {
                Bukkit.getLogger().info("Resetting mine $name at ${100 - checkPercentageEmpty()}%.")
                populateMine()
            }
        }, 0, 20)
    }

    fun stop() {
        if (percentageResetScheduler != null)
            Bukkit.getScheduler().cancelTask(percentageResetScheduler!!)
    }

    fun containsBlock(block: Location): Boolean {
        return isPosWithin(corner1, corner2, block)
    }

    fun canBreak(player: Player): Boolean {
        val file = core.userFiles[player.uniqueId] ?: return false

        val rank = file.getPrestigeRank()

        // get the mines that rank can access
        val accessibleMines = core.files.prestige.cfg.getString("ranks.$rank.available_mines") ?: "a"

        return accessibleMines.contains(this.requiredRank)
    }

    fun breakBlock(player: Player, block: Block) {
        val type = block.type

        // if the mine is active
        if (percentageResetScheduler == null) {
            sendMessage(player, "&cThis mine is not active!")
            return
        }

        // if this.blocks contains type, add the item to the player's inventory
        val mineBlock = blocks.find { it.type == type } ?: if (!player.hasPermission("quarkcore.break") && !player.hasPermission("quarkcore.edit")
            && !player.hasPermission("*") && !player.isOp) {
            return sendMessage(player, "&cYou can't break that block!")
        } else {
            block.type = Material.AIR
            return
        }

        // get the would-be drops from the item
        // all drops will be the same type
        val drops = block.getDrops(player.inventory.itemInMainHand)
        if (drops.isEmpty()) {
            block.type = Material.AIR
            return
        }
        val drop = drops.first()

        // check if the player's inventory is full
        var canTakeItems = false
        for (slot in player.inventory.storageContents) {
            if (slot == null || slot.type == Material.AIR || slot.isEmpty) {
                canTakeItems = true
                break
            }

            if (slot.type == drop.type && slot.amount + drop.amount <= 64) {
                canTakeItems = true
                break
            }
        }

        if (!canTakeItems) {
            playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 5, 0.2f)
            sendMessage(player, "&cYour inventory is full!")
            return
        }

        // add the items to the player's inventory
        player.inventory.addItem(mineBlock.nameItem(core, drop))

        // remove the block
        block.type = Material.AIR
    }

    fun addBlockType(type: Material, spawnChance: Int, value: Long) {
        blocks.add(MineBlock(type, spawnChance, value))
    }

    fun removeBlockType(type: Material) {
        blocks.removeIf { it.type == type }
    }

    fun checkPercentageEmpty(): Int {
        // return the percentage of blocks that are air in the mine
        var total = 0
        var empty = 0

        // loop through all the blocks in the mine
        loopThroughBlocks(corner1, corner2) { block ->
            total++

            if (block.block.type == Material.AIR)
                empty++
        }

        return 100 - (empty.toDouble() / total.toDouble() * 100.0).toInt()
    }

    fun debug() {
        // set the corners to diamond blocks and all other blocks to sponge

        loopThroughBlocks(corner1, corner2) { block ->
            block.block.type = Material.GLASS
        }

        corner1.block.type = Material.DIAMOND_BLOCK
        corner2.block.type = Material.DIAMOND_BLOCK
        Location(warp.world, warp.x, warp.y - 1, warp.z).block.type = Material.GLOWSTONE
    }

    fun populateMine() {
        // get all players inside the mine and teleport them to the mine's warp location
        for (player in core.server.onlinePlayers) {
            if (containsBlock(player.location)) {
                sendMessage(player, "&cThe mine you were in has been reset!")
                player.teleport(warp)
            }
        }

        loopThroughBlocks(corner1, corner2) { block ->
            // set the block randomly from the blocks list based on the blocks spawn chance
            val random = random(0, blocks.sumOf { it.spawnChance })
            var chance = 0
            for (mineBlock in blocks) {
                chance += mineBlock.spawnChance
                if (random <= chance) {
                    block.block.type = mineBlock.type
                    break
                }
            }
        }
    }

    fun emptyMine() {
        loopThroughBlocks(corner1, corner2) { block ->
            block.block.type = Material.AIR
        }
    }

    fun save() {
        val file = core.files.mines

        file["mines.$name.required-rank"] = requiredRank

        file["mines.$name.warp.world"] = warp.world.name
        file["mines.$name.warp.x"] = warp.x
        file["mines.$name.warp.y"] = warp.y
        file["mines.$name.warp.z"] = warp.z
        file["mines.$name.warp.pitch"] = warp.pitch
        file["mines.$name.warp.yaw"] = warp.yaw

        file["mines.$name.corner1.world"] = corner1.world.name
        file["mines.$name.corner1.x"] = corner1.x
        file["mines.$name.corner1.y"] = corner1.y
        file["mines.$name.corner1.z"] = corner1.z

        file["mines.$name.corner2.world"] = corner2.world.name
        file["mines.$name.corner2.x"] = corner2.x
        file["mines.$name.corner2.y"] = corner2.y
        file["mines.$name.corner2.z"] = corner2.z

        // save the blocks
        for (block in blocks) {
            file["mines.$name.blocks.${block.type.name}.spawn-chance"] = block.spawnChance
            file["mines.$name.blocks.${block.type.name}.value"] = block.value
        }
    }
}