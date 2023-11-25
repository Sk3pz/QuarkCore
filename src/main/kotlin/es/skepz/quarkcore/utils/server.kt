package es.skepz.quarkcore.utils

import org.bukkit.Location
import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.files.UserFile
import org.bukkit.entity.Player
import java.util.*

fun setWarp(plugin: QuarkCore, name: String, loc: Location) {
    plugin.files.warps["warps.$name.world"] = loc.world.uid.toString()
    plugin.files.warps["warps.$name.x"] = loc.x
    plugin.files.warps["warps.$name.y"] = loc.y
    plugin.files.warps["warps.$name.z"] = loc.z
    plugin.files.warps["warps.$name.pitch"] = loc.pitch
    plugin.files.warps["warps.$name.yaw"] = loc.yaw
}

fun getWarp(plugin: QuarkCore, name: String): Location? {
    val warps = plugin.files.warps
    val world = warps.cfg.getString("warps.$name.world") ?: return null
    val x = warps.cfg.getDouble("warps.$name.x")
    val y = warps.cfg.getDouble("warps.$name.y")
    val z = warps.cfg.getDouble("warps.$name.z")
    val pitch = warps.cfg.getDouble("warps.$name.pitch").toFloat()
    val yaw = warps.cfg.getDouble("warps.$name.yaw").toFloat()
    return Location(plugin.server.getWorld(UUID.fromString(world)), x, y, z, yaw, pitch)
}

fun delWarp(plugin: QuarkCore, name: String) {
    plugin.files.warps["warps.$name"] = null
}

fun getWarps(plugin: QuarkCore): List<String> {
    val warps = plugin.files.warps
    val warpList = warps.cfg.getConfigurationSection("warps") ?: return ArrayList()
    return warpList.getKeys(false).toList()
}

fun warpExists(plugin: QuarkCore, name: String): Boolean {
    val world = plugin.files.warps.cfg.getString("warps.$name.world")
    return world != null
}

fun getSpawn(plugin: QuarkCore): Location {
    val data = plugin.files.data
    val world = data.cfg.getString("spawn.world") ?: return plugin.server.getWorld("prison")?.spawnLocation ?: plugin.server.worlds.first().spawnLocation
    val x = data.cfg.getDouble("spawn.x")
    val y = data.cfg.getDouble("spawn.y")
    val z = data.cfg.getDouble("spawn.z")
    val pitch = data.cfg.getDouble("spawn.pitch").toFloat()
    val yaw = data.cfg.getDouble("spawn.yaw").toFloat()
    return Location(plugin.server.getWorld(UUID.fromString(world)), x, y, z, yaw, pitch)
}

fun setSpawn(plugin: QuarkCore, loc: Location) {
    val data = plugin.files.data
    data["spawn.world"] = loc.world.uid.toString()
    data["spawn.x"] = loc.x
    data["spawn.y"] = loc.y
    data["spawn.z"] = loc.z
    data["spawn.pitch"] = loc.pitch
    data["spawn.yaw"] = loc.yaw
}

fun getUserFile(core: QuarkCore, player: Player): UserFile {
    if (core.userFiles.contains(player.uniqueId)) return core.userFiles[player.uniqueId]!!
    val file = UserFile(core, player)
    core.userFiles[player.uniqueId] = file
    return file
}