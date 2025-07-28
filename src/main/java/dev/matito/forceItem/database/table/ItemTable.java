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

import java.util.*;
import java.util.stream.Collectors;

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
        if (getCurrentItemEntry(player) == null) return;
        update(getCurrentItem(player).setDone(true).setTime(time));
    }

    //skipped by joker, force skip deletes the entry
    default void markAsSkipped(Player player, String time) {
        if (getCurrentItem(player) == null) return;
        update(getCurrentItem(player).setSkipped(true).setTime(time));
    }

    default ItemEntry getCurrentItemEntry(Player player) {
        return selectOne(Where.equals("player", player).and(Where.equals("done", false))).map(Item::getItemEntry).orElse(null);
    }

    default Item getCurrentItem(Player player) {
        return selectOne(Where.equals("player", player).and(Where.equals("done", false))).orElse(null);
    }

    default Map<Player, ItemEntry> getPlayersCurrentItems() {
        Map<Player, ItemEntry> map = new HashMap<>();
        selectAll().stream().filter(Item::isNotDone).forEach(item -> map.put(ForceItem.INSTANCE.getServer().getPlayer(item.getPlayer().getUniqueId()), item.getItemEntry()));
        return map;
    }

    default LinkedHashMap<Player, List<Item>> getPlayersFinishedItems() {
        return selectMany(Where.equals("done", true)).stream()
                .collect(Collectors.groupingBy(Item::getOnlinePlayer))
                .entrySet().stream()
                .sorted(Map.Entry.<Player, List<Item>>comparingByValue(Comparator.comparingInt(List::size))
                        .reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    };


    default List<ItemEntry> getFinishedItemEntries(Player player) {
        return selectMany(Where.equals("player", player).and(Where.equals("done", true))).stream().map(Item::getItemEntry).toList();
    }

    default List<Item> getFinishedItems(Player player) {
        return selectMany(Where.equals("player", player).and(Where.equals("done", true)));
    }

    default int getCount() {
        return getRowCount();
    }

}
