package dev.matito.forceItem.database.object;

import de.mineking.databaseutils.Column;
import dev.matito.forceItem.ForceItem;
import dev.matito.forceItem.util.ItemEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class Item {
    @Column(key = true)
    private OfflinePlayer player;

    @Column(key = true)
    private String itemEntry;

	@Column()
    private boolean done;

    public ItemEntry getItemEntry() {
        return ItemEntry.valueOf(itemEntry);
    }

    public boolean isNotDone () {
        return !done;
    }

    public Player getOnlinePlayer() {
        return ForceItem.INSTANCE.getServer().getPlayer(player.getUniqueId());
    }

}
