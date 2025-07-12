package dev.matito.pluginTemplateMatito;

import de.mineking.databaseutils.DatabaseManager;
import dev.matito.pluginTemplateMatito.commands.TestCommand;
import dev.matito.pluginTemplateMatito.database.PlayerTypeMapper;
import dev.matito.pluginTemplateMatito.database.object.Test;
import dev.matito.pluginTemplateMatito.database.table.TestTable;
import dev.matito.pluginTemplateMatito.listener.TemplateListener;
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

@Getter
public final class PluginTemplateMatito extends JavaPlugin {

	public static PluginTemplateMatito INSTANCE;
	public final static Dotenv CREDENTIALS = Dotenv.configure().filename("credentials").load();

	private DatabaseManager database;

	private TestTable table;

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
	}

	private void registerCommands() {
		TestCommand.register();
	}

	private void registerListeners() {
		registerEvent(new TemplateListener());
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


		table = getDatabase().getTable(Test.class, Test::new).name("test").table(TestTable.class).create();
	}

	public static TextComponent getPrefix() {
		return Component.empty()
				.append(Component.text("[", NamedTextColor.DARK_GRAY)
						.append(Component.text("Template", TextColor.color(0x508d98)))
						.append(Component.text("Plugin", TextColor.color(0x1b5a65)))
						.append(Component.text("] ", NamedTextColor.DARK_GRAY))
				).color(NamedTextColor.GRAY);
	}

	@Override
	public void onDisable() {
		INSTANCE = null;
	}
}
