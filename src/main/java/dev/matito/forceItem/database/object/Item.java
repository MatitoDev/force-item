package dev.matito.forceItem.database.object;

import de.mineking.databaseutils.Column;
import dev.matito.forceItem.ForceItem;
import dev.matito.forceItem.util.ItemEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Date;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Item {
    @Column(key = true)
    private OfflinePlayer player;

    @Column(key = true)
    private String itemEntry;

    @Setter
	@Column()
    private boolean done;

    @Setter
    @Column
    private boolean skipped;

    @Setter
    @Column
    private String time;

    public ItemEntry getItemEntry() {
        return ItemEntry.valueOf(itemEntry);
    }

    public boolean isNotDone () {
        return !done;
    }

    public Player getOnlinePlayer() {
        return ForceItem.INSTANCE.getServer().getPlayer(player.getUniqueId());
    }

    public double getTimeInSeconds() {
        double totalSeconds = 0.0;

        // First, split the time string based on "h", "m", "s"
        String[] parts = time.replaceAll(" ", "").split("h|m|s");

        if (time.contains("h")) totalSeconds += Integer.parseInt(parts[parts.length - 3]) * 3600;
        if (time.contains("m")) totalSeconds += Integer.parseInt(parts[parts.length - 2]) * 60;
        totalSeconds += Double.parseDouble(parts[parts.length - 1]) * 60;

        return totalSeconds;
    }

}
