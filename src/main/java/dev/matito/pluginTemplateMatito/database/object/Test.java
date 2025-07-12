package dev.matito.pluginTemplateMatito.database.object;

import de.mineking.databaseutils.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;

import java.time.Instant;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class Test {
    @Column(key = true)
    private String key;

    @Column
    private OfflinePlayer value;
}
