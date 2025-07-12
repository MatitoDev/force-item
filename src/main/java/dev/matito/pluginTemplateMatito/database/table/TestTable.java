package dev.matito.pluginTemplateMatito.database.table;

import de.mineking.databaseutils.Table;
import de.mineking.databaseutils.Where;
import de.mineking.databaseutils.exception.ConflictException;
import dev.matito.pluginTemplateMatito.database.object.Test;
import org.bukkit.OfflinePlayer;
import java.util.Optional;

public interface TestTable extends Table<Test> {

    default boolean add(OfflinePlayer value, String key) {
        try {
            insert(new Test(
                    key,
                    value
            ));
            return true;
        } catch (ConflictException e) {
            return false;
        }
    }

    default Optional<Test> get(String key) {
        return selectOne(Where.equals("key", key));
    }

    default int getCount() {
        return getRowCount();
    }
}
