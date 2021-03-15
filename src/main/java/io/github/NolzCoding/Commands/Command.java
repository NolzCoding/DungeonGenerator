package io.github.NolzCoding.Commands;

import io.github.NolzCoding.Utils.AsyncMapGenerator;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Command implements CommandExecutor {
    private final AsyncMapGenerator asyncMapGenerator = new AsyncMapGenerator();
    //test

    @Override
    public boolean onCommand( CommandSender commandSender , org.bukkit.command.Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("gend")){
            if (commandSender instanceof Player) {
                int dim = Integer.parseInt(strings[0]);
                int tunnelMax = Integer.parseInt(strings[1]);
                int maxLenght = Integer.parseInt(strings[2]);
                asyncMapGenerator.createmap(dim, tunnelMax, maxLenght, ((Player) commandSender).getLocation());
            }
        }
        return true;
    }

}
