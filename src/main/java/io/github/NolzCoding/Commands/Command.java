package io.github.NolzCoding.Commands;

import com.sk89q.worldedit.WorldEdit;
import io.github.NolzCoding.Main;
import io.github.NolzCoding.Utils.MapGenerator;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Command implements CommandExecutor {
    private final Main main = Main.getMain();
    private final MapGenerator mapGenerator = new MapGenerator();

    //test

    @Override
    public boolean onCommand( CommandSender commandSender , org.bukkit.command.Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("gend")){
            if (commandSender instanceof Player) {
                int dim = Integer.parseInt(strings[0]);
                int tunnelMax = Integer.parseInt(strings[1]);
                int maxLenght = Integer.parseInt(strings[2]);
                mapGenerator.createMap(dim, tunnelMax, maxLenght, ((Player) commandSender).getLocation());
            }
        }
        return true;
    }

}
