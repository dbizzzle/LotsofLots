package Warpdrive.LotsofLots;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import org.bukkit.Location;

public class dataHandle {

    private static final String mDataName = "main.data";
    private static final String locDataName = "locations.data";
    private static final String jumpDataName = "jumps.data";
    private static final String dirName = "plugins/LotsofLots/";
    private static final File mainData = new File(dirName+mDataName);
    private final LotsofLots plugin;

    public dataHandle(LotsofLots plug) {
        plugin = plug;
        new File(dirName).mkdir();

        if (!mainData.exists()) {
            System.out.println("Lots of Lots: Data files don't exist, creating now...");
            try {
                mainData.createNewFile();
                (new File(dirName+locDataName)).createNewFile();
                (new File(dirName+jumpDataName)).createNewFile();
                System.out.println("Lots of Lots: Data files created!");
            } catch (IOException e) {
                System.out.println("Lots of Lots: Data files creation failed!");
                e.printStackTrace();
            }
        }
    }

    /*public void load() throws Exception{
        Scanner datain = new Scanner(new FileReader(dirName+fName));
        while (datain.hasNextLine()){
            String[] temp = datain.nextLine().split("@");
            int index = Integer.parseInt(temp[0]);
            String playername = temp[1];
            String[] tempLoc1 = temp[2].split("#");
            String[] tempLoc2 = temp[3].split("#");
            if (!LotsofLots.lotLocs.containsKey(index)){
                LotsofLots.lotLocs.put(index, new ArrayList<Location>());
            }
            Location loc1 = new Location(plugin.getServer().getWorld(tempLoc1[0]),
                    Integer.parseInt(tempLoc1[1]),
                    Integer.parseInt(tempLoc1[2]),
                    Integer.parseInt(tempLoc1[3]));

            Location loc2 = new Location(plugin.getServer().getWorld(tempLoc2[0]),
                    Integer.parseInt(tempLoc2[1]),
                    Integer.parseInt(tempLoc2[2]),
                    Integer.parseInt(tempLoc2[3]));

            LotsofLots.lotLocs.get(index).add(loc1);
            LotsofLots.lotLocs.get(index).add(loc2);
            LotsofLots.lotOwn.put(index, playername);
        }
        datain.close();
        for (int index : LotsofLots.lotOwn.keySet()) {
            if (!LotsofLots.numLots.containsKey(LotsofLots.lotOwn.get(index))) {
                LotsofLots.numLots.put(LotsofLots.lotOwn.get(index), 0);
                LotsofLots.p1.put(LotsofLots.lotOwn.get(index), false);
                LotsofLots.p2.put(LotsofLots.lotOwn.get(index), false);
            }
            LotsofLots.numLots.put(LotsofLots.lotOwn.get(index),
                    LotsofLots.numLots.get(LotsofLots.lotOwn.get(index))+1);
        }
    }*/

    /*public void save() throws Exception {
        PrintWriter dataout = new PrintWriter(new BufferedWriter(new FileWriter(data)));
        for (int index : LotsofLots.lotOwn.keySet()) {
            dataout.println(index + "@" + LotsofLots.lotOwn.get(index) + "@"
                    +LotsofLots.lotLocs.get(index).get(0).getWorld().getName()+"#"
                    +LotsofLots.lotLocs.get(index).get(0).getBlockX()+"#"
                    +LotsofLots.lotLocs.get(index).get(0).getBlockY()+"#"
                    +LotsofLots.lotLocs.get(index).get(0).getBlockZ()+"@"
                    +LotsofLots.lotLocs.get(index).get(1).getWorld().getName()+"#"
                    +LotsofLots.lotLocs.get(index).get(1).getBlockX()+"#"
                    +LotsofLots.lotLocs.get(index).get(1).getBlockY()+"#"
                    +LotsofLots.lotLocs.get(index).get(1).getBlockZ());
        }
        dataout.close();
    }*/
    public void save() throws Exception{
        ObjectOutputStream dataout = new ObjectOutputStream(new FileOutputStream(mainData));
        dataout.writeObject(LotsofLots.numLots);
        dataout.writeObject(LotsofLots.pgExchange);
        dataout.writeObject(LotsofLots.lotPrivate);
        dataout.writeObject(LotsofLots.whitelist);
        dataout.close();

        PrintWriter locout = new PrintWriter(new BufferedWriter(new FileWriter(dirName+locDataName)));
        for (int index : LotsofLots.lotLocs.keySet()) {
            locout.println(index + "@" + LotsofLots.lotOwn.get(index) + "@"
                    +LotsofLots.lotLocs.get(index).get(0).getWorld().getName()+"#"
                    +LotsofLots.lotLocs.get(index).get(0).getBlockX()+"#"
                    +LotsofLots.lotLocs.get(index).get(0).getBlockY()+"#"
                    +LotsofLots.lotLocs.get(index).get(0).getBlockZ()+"@"
                    +LotsofLots.lotLocs.get(index).get(1).getWorld().getName()+"#"
                    +LotsofLots.lotLocs.get(index).get(1).getBlockX()+"#"
                    +LotsofLots.lotLocs.get(index).get(1).getBlockY()+"#"
                    +LotsofLots.lotLocs.get(index).get(1).getBlockZ());
        }
        locout.close();

        PrintWriter jumpout = new PrintWriter(new BufferedWriter(new FileWriter(dirName+jumpDataName)));
        for (int index : LotsofLots.lotJump.keySet()) {
            jumpout.println(index + "@"
                    +LotsofLots.lotJump.get(index).getWorld().getName()+"#"
                    +LotsofLots.lotJump.get(index).getBlockX()+"#"
                    +LotsofLots.lotJump.get(index).getBlockY()+"#"
                    +LotsofLots.lotJump.get(index).getBlockZ());
        }

        jumpout.close();
    }

    public void load() throws Exception{
        ObjectInputStream datain = new ObjectInputStream(new FileInputStream(mainData));
        LotsofLots.numLots = (HashMap<String, Integer>) datain.readObject();
        LotsofLots.pgExchange = (HashMap<String, HashMap<Integer, Integer>>) datain.readObject();
        LotsofLots.lotPrivate = (HashMap<Integer, Boolean>) datain.readObject();
        LotsofLots.whitelist= (HashMap<Integer, ArrayList<String>>) datain.readObject();
        datain.close();

        Scanner locin = new Scanner(new FileReader(dirName+locDataName));
        while (locin.hasNextLine()){
            String temp1 = locin.nextLine();
            String[] temp = temp1.split("@");
            //System.out.println(temp1);
            //System.out.println(temp[0]+"0");
            //System.out.println(temp[1]+"1");
            //System.out.println(temp[2]+"2");
            //System.out.println(temp[3]+"3");
            int index = Integer.parseInt(temp[0]);
            String name = temp[1];
            String[] tempLoc1 = temp[2].split("#");
            String[] tempLoc2 = temp[3].split("#");

            if (!LotsofLots.lotLocs.containsKey(index)){
                LotsofLots.lotLocs.put(index, new ArrayList<Location>());
            }
            Location loc1 = new Location(plugin.getServer().getWorld(tempLoc1[0]),
                    Integer.parseInt(tempLoc1[1]),
                    Integer.parseInt(tempLoc1[2]),
                    Integer.parseInt(tempLoc1[3]));

            Location loc2 = new Location(plugin.getServer().getWorld(tempLoc2[0]),
                    Integer.parseInt(tempLoc2[1]),
                    Integer.parseInt(tempLoc2[2]),
                    Integer.parseInt(tempLoc2[3]));

            LotsofLots.lotOwn.put(index, name);

            LotsofLots.lotLocs.get(index).add(loc1);
            LotsofLots.lotLocs.get(index).add(loc2);



            if(!LotsofLots.numLots.containsKey(name)){
                LotsofLots.numLots.put(name, 0);
            }
            LotsofLots.numLots.put(name, LotsofLots.numLots.get(name) + 1);
        }
        locin.close();

        Scanner jumpin = new Scanner(new FileReader(dirName+jumpDataName));
        while (jumpin.hasNextLine()){
            String[] temp = jumpin.nextLine().split("@");
            int index = Integer.parseInt(temp[0]);
            String[] tempLoc1 = temp[1].split("#");

            Location loc1 = new Location(plugin.getServer().getWorld(tempLoc1[0]),
                    Integer.parseInt(tempLoc1[1]),
                    Integer.parseInt(tempLoc1[2]),
                    Integer.parseInt(tempLoc1[3]));

            LotsofLots.lotJump.put(index, loc1);
        }
        jumpin.close();
    }
}