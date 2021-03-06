package io.github.NolzCoding.Commands;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import io.github.NolzCoding.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import scala.Int;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;


public class Command implements CommandExecutor {
    private final Main main = Main.getMain();
    private final WorldEdit worldEdit = main.getWorldEdit();


    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("gend")){
            if (commandSender instanceof Player) {
                ArrayList<Integer> arrayList = createArray(1, 5);
                int dim = (int) Math.sqrt(arrayList.size());
                for (int x = 0; x < dim; x++) {
                    for (int y = 0; y < dim; y++) {
                        if (arrayList.get(y*x) == 0) {
                            Location loc = ((Player) commandSender).getLocation();
                            loc.add(x *5, 0, y*5);
                            pastemap(loc);
                        }

                    }
                }


            }
        }

        return false;
    }

    private void pastemap(Location orgin) {
        File file = new File(main.getDataFolder(), "scem/square.schem");
        System.out.println(file.getName());
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        paste(orgin, format, file);
    }

    private ArrayList<Integer> createMap(int dimensions) {
        ArrayList<Integer> map = createArray(1, dimensions);
        int maxTunnels = 3;
        int maxLenght = 3;
        int row = random(0, dimensions);
        int column = random(0, dimensions);

        ArrayList<ArrayList<Integer>> dirs = new ArrayList<>();
        dirs.add(newdir(-1, 0));
        dirs.add(newdir(1, 0));
        dirs.add(newdir(0, -1));
        dirs.add(newdir(0, 1));
        ArrayList<Integer> lastDir = newdir(-1, 0);
        ArrayList<Integer> randomDir;
        while (maxLenght > 0 && maxTunnels > 0 && dimensions > 0) {
            do {
                randomDir = dirs.get(random(0,dirs.size() -1));
            } while ((randomDir.get(0).equals(-lastDir.get(0)) &&
                    randomDir.get(1).equals(-lastDir.get(1))) ||
                    (randomDir.get(0).equals(lastDir.get(0)) &&
                    randomDir.get(1).equals(lastDir.get(1)))
            );

            int randomLenght = random(0, maxLenght);
            int tunnelLenght = 0;
            while (tunnelLenght < randomLenght) {
                if (
                        ((row == 0) && (randomDir.get(0) == -1)) ||
                        ((column == 0) && (randomDir.get(1) == -1)) ||
                        ((row == dimensions - 1) && (randomDir.get(0) == 1)) ||
                        ((column == dimensions - 1) && (randomDir.get(1) == 1))
                ){
                    break;
                }
                else {
                    map.set(row * column, 0);
                    row += randomDir.get(0);
                    column += randomDir.get(1);
                    tunnelLenght++;
                }

            }

            if (tunnelLenght > 0) {
                lastDir = randomDir;
                maxTunnels--;
            }

        }

        return map;

    }

    private ArrayList<Integer> createArray(int num, int dimensions ) {
        ArrayList<Integer> integers = new ArrayList<>();
        for (int x = 0; x < dimensions; x++) {
            for (int y = 0; y < dimensions; y++) {
                integers.add(num);
            }
        }
        return integers;
    }

    private ArrayList<Integer> newdir(int one, int two) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(one);
        arrayList.add(two);
        return arrayList;
    }

    private int random(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    private void paste(Location loc, ClipboardFormat format, File file) {

        if (format != null) {
            try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                Clipboard clipboard = reader.read();
                try (EditSession editSession = worldEdit.getEditSessionFactory().getEditSession(new BukkitWorld(loc.getWorld()), -1)) {
                    Operation operation = new ClipboardHolder(clipboard)
                            .createPaste(editSession)
                            .to(BlockVector3.at(
                                    loc.getX(),
                                    loc.getY(),
                                    loc.getZ()
                            ))
                            .ignoreAirBlocks(false)
                            .build();
                    Operations.complete(operation);
                }
            } catch (IOException | WorldEditException e) {
                e.printStackTrace();
            }
        }
    }
}
