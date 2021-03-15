package io.github.NolzCoding.Utils;

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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;



public class AsyncMapGenerator {

    private final WorldEdit worldEdit = WorldEdit.getInstance();
    private final Main main = Main.getMain();
    public void createmap(int dim, int maxT, int maxL, Location loc) {
        CallBack<ArrayList<Integer>> callback = response -> {
            pastepart(loc, response, dim);
        };
        new BukkitRunnable() {
            @Override
            public void run() {
                int maxTunnels = maxT;
                Location location = loc;

                Bukkit.getLogger().info("GENERATING PLEASE WAIT");
                ArrayList<Integer> map = createArray(1, dim);
                int row = random(0, dim);
                int column = random(0, dim);
                ArrayList<ArrayList<Integer>> dirs = new ArrayList<>();
                dirs.add(newdir(-1, 0));
                dirs.add(newdir(1, 0));
                dirs.add(newdir(0, -1));
                dirs.add(newdir(0, 1));
                ArrayList<Integer> lastDir = dirs.get(random(0,dirs.size() -1));
                ArrayList<Integer> randomDir;
                LocalDateTime then = LocalDateTime.now(); //Stops the shit from running for ever, prob teribble idea

                while (maxL > 0 && maxTunnels > 0 && dim > 0) {
                    do {
                        randomDir = dirs.get(random(0,dirs.size() -1));
                        if (ChronoUnit.SECONDS.between(then, LocalDateTime.now()) >= 2) break; //Stops the shit from running for ever
                    } while ((randomDir.get(0).equals(-lastDir.get(0)) &&
                            randomDir.get(1).equals(-lastDir.get(1))) ||
                            (randomDir.get(0).equals(lastDir.get(0)) &&
                                    randomDir.get(1).equals(lastDir.get(1)))
                    );
                    if (ChronoUnit.SECONDS.between(then, LocalDateTime.now()) >= 2) break; //Stops the shit from running for ever
                    int randomLenght = random(0, maxL);
                    int tunnelLenght = 0;
                    while (tunnelLenght < randomLenght) {
                        if (
                                ((row == 0) && (randomDir.get(0) == -1)) ||
                                        ((column == 0) && (randomDir.get(1) == -1)) ||
                                        ((row == dim - 1) && (randomDir.get(0) == 1)) ||
                                        ((column == dim - 1) && (randomDir.get(1) == 1))
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
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        callback.execute(map);
                    }
                }.runTask(main);

            }
        }.runTaskAsynchronously(Main.getMain());
    }


    private void pastepart(Location orgin, ArrayList<Integer> map, int dimensions) {
        File file = new File(main.getDataFolder(), "scem/square.schem");
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        for (int x = 0; x < dimensions; x++) {
            for (int y = 0; y < dimensions; y++) {
                if (map.get(y*x) == 0) {
                    Location loc = orgin.clone();
                    loc.add(x * 5, 0, y * 5);
                    paste(loc, format, file);
                }

            }
        }

    }

    private int random(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }


    private ArrayList<Integer> createArray(int num, int dimensions) {
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
