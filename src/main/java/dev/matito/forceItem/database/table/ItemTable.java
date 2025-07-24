package dev.matito.forceItem.database.table;

import de.mineking.databaseutils.Table;
import de.mineking.databaseutils.Where;
import de.mineking.databaseutils.exception.ConflictException;
import dev.matito.forceItem.ForceItem;
import dev.matito.forceItem.database.object.Item;
import dev.matito.forceItem.util.ItemEntry;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ItemTable extends Table<Item> {

    default boolean add(OfflinePlayer player, ItemEntry item) {
        if (!selectMany(Where.equals("player", player).and(Where.equals("done", false))).isEmpty()) return false;

        try {
            insert(new Item(
                    player,
                    item.toString(),
                    false,
                    false,
                    null
            ));
            return true;
        } catch (ConflictException e) {
            return false;
        }
    }

    default ItemStack getItemstack(Player player) {
        return selectOne(Where.equals("player", player)).orElseThrow().getItemEntry().getItemStack();
    }

    default ItemEntry getItemEntry(Player player) {
        return selectOne(Where.equals("player", player)).orElseThrow().getItemEntry();
    }

    default void markAsDone(Player player, String time) {
        if (getCurrentItem(player) == null) return;
        update(new Item(player, getCurrentItem(player).toString(), true, false, time));
    }

    //skipped by joker, force skip ist just same as done :)
    default void markAsSkipped(Player player, String time) {
        if (getCurrentItem(player) == null) return;
        update(new Item(player, getCurrentItem(player).toString(), false, true, time));
    }

    default ItemEntry getCurrentItem(Player player) {
        return selectOne(Where.equals("player", player).and(Where.equals("done", false))).map(Item::getItemEntry).orElse(null);
    }

    default Map<Player, ItemEntry> getPlayersCurrentItems() {
        Map<Player, ItemEntry> map = new HashMap<>();
        selectAll().stream().filter(Item::isNotDone).forEach(item -> map.put(ForceItem.INSTANCE.getServer().getPlayer(item.getPlayer().getUniqueId()), item.getItemEntry()));
        return map;
    }

    default List<ItemEntry> getFinishedItems(Player player) {
        return selectMany(Where.equals("player", player).and(Where.equals("done", true))).stream().map(Item::getItemEntry).toList();
    }

    default int getCount() {
        return getRowCount();
    }

}
