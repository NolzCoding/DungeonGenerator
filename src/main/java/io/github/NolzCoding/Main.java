package io.github.NolzCoding;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.sk89q.worldedit.WorldEdit;
import io.github.NolzCoding.Commands.Command;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;

public class Main extends JavaPlugin {
    private static Main main;
    private static final Logger log = Logger.getLogger("Minecraft");
    private WorldEdit worldEdit;
    private MultiverseCore multiverseCore;

    @Override
    public void onEnable() {
        main = this;
        setupMulti();
        worldEdit = WorldEdit.getInstance();
        Objects.requireNonNull(this.getCommand("gend")).setExecutor(new Command());
        super.onEnable();
        log.info(String.format("[%s] - Successfully enabled",
                getDescription().getName()));
        saveResource("scem/square.schem", true);



    }

    private void setupMulti() {
        Plugin plugin = getServer().getPluginManager().getPlugin("Multiverse-Core");

        if (plugin instanceof MultiverseCore) {
            multiverseCore = (MultiverseCore) plugin;
            return;
        }
        throw new RuntimeException("MultiVerse not found!");
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static Main getMain() {
        return main;
    }

    public WorldEdit getWorldEdit() {
        return worldEdit;
    }
}
