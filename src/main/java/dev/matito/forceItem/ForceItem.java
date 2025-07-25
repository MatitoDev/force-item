package dev.matito.forceItem;

import de.mineking.databaseutils.DatabaseManager;
import de.miraculixx.timer.api.MTimerAPI;
import dev.matito.forceItem.commands.*;
import dev.matito.forceItem.database.PlayerTypeMapper;
import dev.matito.forceItem.database.object.Item;
import dev.matito.forceItem.database.table.ItemTable;
import dev.matito.forceItem.listener.JokerListener;
import dev.matito.forceItem.util.ItemEntry;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

@Getter
public final class ForceItem extends JavaPlugin {

	public static ForceItem INSTANCE;
	public final static Dotenv CREDENTIALS = Dotenv.configure().filename("credentials").load();

	private DatabaseManager database;
	private MTimerAPI timer;

	private GameManager gameManager;

	private ItemTable itemTable;

	private Set<ItemEntry> skippedItems;

	public static DatabaseManager getDatabase() {
		return INSTANCE.database;
	}

	@Override
	public void onLoad() {
		if (INSTANCE != null) throw new RuntimeException("Plugin already loaded");
		INSTANCE = this;

		registerCommands();
	}

	@Override
	public void onEnable() {
		registerListeners();
		setupDatabase();
		setupTimer();
		gameManager = new GameManager();
		skippedItems = new HashSet<>();
	}

	private void registerCommands() {
		TestCommand.register();
		StartCommand.register();
		AddPlayerCommand.register();
		ResetCommand.register();
		ForceSkipCommand.register();
		GiveJokerCommand.register();
	}

	private void registerListeners() {
		registerEvent(new JokerListener());
	}

	private void registerEvent(@NotNull Listener listener) {
		getServer().getPluginManager().registerEvents(listener, this);
	}

	private void setupDatabase() {
		try {
			//Force server to load the driver
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		database = new DatabaseManager("jdbc:postgresql://" + CREDENTIALS.get("DATABASE_HOST"), CREDENTIALS.get("DATABASE_USER"), CREDENTIALS.get("DATABASE_PASSWORD"));
		database.getDriver().installPlugin(new PostgresPlugin());

		database.addMapper(new PlayerTypeMapper());


		itemTable = getDatabase().getTable(Item.class, Item::new).name("items").table(ItemTable.class).create();
	}

	private void setupTimer() {
		//idk -> ai -> worked
		try {
			Field instanceField = MTimerAPI.class.getDeclaredField("INSTANCE");
			instanceField.setAccessible(true);
			timer = (MTimerAPI) instanceField.get(null);

			if (timer == null) {
				getLogger().warning("MTimerAPI not accessible!");
				return;
			}

			getLogger().info("Successfully connected to MTimerAPI!");

		} catch (Exception e) {
			getLogger().severe("Failed to access MTimerAPI: " + e.getMessage());
			e.printStackTrace();
			return;
		}
	}

	public static TextComponent getPrefix() {
		return Component.empty()
				.append(Component.text("[", NamedTextColor.DARK_GRAY)
						.append(Component.text("Force", TextColor.color(0x508d98)))
						.append(Component.text("Item", TextColor.color(0x1b5a65)))
						.append(Component.text("] ", NamedTextColor.DARK_GRAY))
				).color(NamedTextColor.AQUA);
	}

	@Override
	public void onDisable() {
		INSTANCE = null;
	}
}
