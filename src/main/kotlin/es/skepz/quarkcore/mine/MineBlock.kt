package es.skepz.quarkcore.mine

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.colorize
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class MineBlock(val type: Material, val spawnChance: Int, val value: Long) {

    fun nameItem(core: QuarkCore, item: ItemStack): ItemStack {
        val meta = item.itemMeta
        val name = item.type.name
        meta.displayName(Component.text(colorize("&6${name}")))
        meta.lore(listOf(Component.text(colorize("&7Value: &e$value"))))

        // set the value in persistent data
        meta.persistentDataContainer.set(core.mineBlocKey, PersistentDataType.LONG, value)

        item.setItemMeta(meta)

        return item
    }

}