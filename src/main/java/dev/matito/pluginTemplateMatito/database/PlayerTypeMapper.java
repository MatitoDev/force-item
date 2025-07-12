package dev.matito.pluginTemplateMatito.database;

import de.mineking.databaseutils.DatabaseManager;
import de.mineking.databaseutils.TypeMapper;
import de.mineking.databaseutils.type.DataType;
import de.mineking.databaseutils.type.PostgresType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerTypeMapper implements TypeMapper<UUID, OfflinePlayer> {
    @Override
    public boolean accepts(@NotNull DatabaseManager manager, @NotNull Type type, @NotNull Field f) {
        return type.equals(OfflinePlayer.class);
    }

    @NotNull
    @Override
    public DataType getType(@NotNull DatabaseManager manager, @NotNull Type type, @NotNull Field f) {
        return PostgresType.UUID;
    }

    @NotNull
    @Override
    public Type getFormattedType(@NotNull DatabaseManager manager, @NotNull Type type, @NotNull Field f, @Nullable Object value) {
        return UUID.class;
    }

    @NotNull
    @Override
    public UUID format(@NotNull DatabaseManager manager, @NotNull Type type, @NotNull Field f, @Nullable Object value) {
        if (value instanceof OfflinePlayer p) return p.getUniqueId();
        return new UUID(0, 0);
    }

    @Nullable
    @Override
    public UUID extract(@NotNull ResultSet set, @NotNull String name, @NotNull Type target) throws SQLException {
        String temp = set.getString(name);
        return temp.isEmpty() ? null : java.util.UUID.fromString(temp);
    }

    @Nullable
    @Override
    public OfflinePlayer parse(@NotNull DatabaseManager manager, @NotNull Type type, @NotNull Field field, @Nullable UUID value) {
        return value == null ? null : Bukkit.getOfflinePlayer(value);
    }
}
