package Warpdrive.LotsofLots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class LotsofLots extends JavaPlugin{

    private final LOLBListener bListener = new LOLBListener(this);
    private final dataHandle data = new dataHandle(this);
    public static HashMap<String, Boolean> p1 = new HashMap<String, Boolean>();
    public static HashMap<String, Boolean> p2 = new HashMap<String, Boolean>();
    public static HashMap<String, Location> p1pos = new HashMap<String, Location>();
    public static HashMap<String, Location> p2pos = new HashMap<String, Location>();
    public static HashMap<String, Boolean> createReady = new HashMap<String, Boolean>();
    public static HashMap<String, Integer> numLots = new HashMap<String, Integer>();
    public static HashMap<String, HashMap<Integer, Integer>> pgExchange = new HashMap<String, HashMap<Integer, Integer>>();
    public static HashMap<Integer, String> lotOwn = new HashMap<Integer, String>();
    public static HashMap<Integer, Boolean> lotPrivate = new HashMap<Integer, Boolean>();
    public static HashMap<Integer, ArrayList<Location>> lotLocs = new HashMap<Integer, ArrayList<Location>>();
    public static HashMap<Integer, Location> lotJump = new HashMap<Integer, Location>();
    public static HashMap<Integer, ArrayList<String>> whitelist = new HashMap<Integer, ArrayList<String>>();

    public void onDisable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println("Lots of Lots: Saving data.");
        try {
            data.save();
            System.out.println("Lots of Lots: Saving successful.");
        } catch (Exception e) {
            System.out.println("Lots of Lots: Saving failed.");
            e.printStackTrace();
        }
        System.out.println(pdfFile.getName() + "(v" + pdfFile.getVersion() + ") by " + pdfFile.getAuthors() + " is disabled!");
    }

    public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.BLOCK_PLACE, bListener, Priority.Low, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, bListener, Priority.Low, this);
        System.out.println("Lots of Lots: Loading data.");
        try {
            data.load();
            System.out.println("Lots of Lots: Loading successful.");
        } catch (Exception e) {
            System.out.println("Lots of Lots: Loading failed.");
            e.printStackTrace();
        }
        System.out.println(pdfFile.getName() + "(v" + pdfFile.getVersion() + ") by " + pdfFile.getAuthors() + " is enabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player player = (Player) sender;
        String playername = player.getName();

        if (!numLots.containsKey(playername)) {
            p1.put(playername, false);
            p2.put(playername, false);
            numLots.put(playername, 0);
        }
        if (!pgExchange.containsKey(playername)) {
            pgExchange.put(playername, new HashMap<Integer, Integer>());
        }

        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        if (commandLabel.equalsIgnoreCase("lcreate")) {
            if (createReady.containsKey(playername)) {
                if (createReady.get(playername)) {
                    Location temp1 = smaller(p1pos.get(playername), p2pos.get(playername));
                    Location temp2 = bigger(p1pos.get(playername), p2pos.get(playername));
                    int index = lotLocs.size();

                    lotLocs.put(index, new ArrayList<Location>());
                    if (args.length == 0) {
                        lotOwn.put(index, playername);
                        pgExchange.get(playername).put(numLots.get(playername), index);
                        numLots.put(playername, numLots.get(playername) + 1);

                        lotLocs.get(index).add(temp1);
                        lotLocs.get(index).add(temp2);
                        createReady.put(playername, false);
                        whitelist.put(index, new ArrayList<String>());

                        player.sendMessage(ChatColor.GREEN + "Lot creation successful!");
                        player.sendMessage(ChatColor.GREEN + "Lot has id of " + index + ", and belongs to " + playername);
                        player.sendMessage(ChatColor.GREEN + "This Lot also has a player Lot ID of " + Integer.toString(numLots.get(playername) - 1));

                    } else if (args.length == 1) {
                        List<Player> players = this.getServer().matchPlayer(args[0]);
                        if (players.isEmpty()) {
                            player.sendMessage(ChatColor.RED
                                    + "No Matching Player");
                        } else if (players.size() > 1) {
                            player.sendMessage(ChatColor.RED
                                    + "More than one matching player");
                            player.sendMessage(ChatColor.RED
                                    + "Be more specific!");
                        } else if (players.size() == 1) {
                            String username = players.get(0).getName();
                            if (!numLots.containsKey(username)) {
                                numLots.put(username, 0);
                                pgExchange.put(username, new HashMap<Integer, Integer>());
                            }
                            lotOwn.put(index, username);

                            pgExchange.get(username).put(numLots.get(username), index);
                            numLots.put(username, numLots.get(username) + 1);

                            lotLocs.get(index).add(temp1);
                            lotLocs.get(index).add(temp2);
                            createReady.put(playername, false);
                            whitelist.put(index, new ArrayList<String>());

                            player.sendMessage(ChatColor.GREEN + "Lot creation successful!");
                            player.sendMessage(ChatColor.GREEN + "Lot has ID of " + index + ", and belongs to " + username);
                            player.sendMessage(ChatColor.GREEN + "This Lot also has a player Lot ID of " + Integer.toString(numLots.get(username) - 1));

                        }
                    }
                } else {
                    p1.put(playername, true);
                    player.sendMessage(ChatColor.GREEN + "Place block to define first corner of Lot.");
                    lotPrivate.put(lotPrivate.size(), false);
                }
            } else {
                p1.put(playername, true);
                player.sendMessage(ChatColor.GREEN + "Place block to define first corner of Lot.");
                lotPrivate.put(lotPrivate.size(), false);
            }
        }

        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        else if (commandLabel.equalsIgnoreCase("lot")) {
            if (args.length == 0) {
                boolean lotfound = false;
                for (int index : LotsofLots.lotLocs.keySet()) {
                    if ((player.getWorld() == lotLocs.get(index).get(0).getWorld())
                            && (player.getLocation().getBlockX() >= lotLocs.get(index).get(0).getBlockX())
                            && (player.getLocation().getBlockX() <= lotLocs.get(index).get(1).getBlockX())
                            && (player.getLocation().getBlockZ() >= lotLocs.get(index).get(0).getBlockZ())
                            && (player.getLocation().getBlockZ() <= lotLocs.get(index).get(1).getBlockZ())) {
                        lotfound = true;
                        if (!lotPrivate.get(index)) {
                            player.sendMessage("LotID: " + index);
                            player.sendMessage("Player LotID: "+localID(playername, index));
                            player.sendMessage("Owner: " + lotOwn.get(index));
                            player.sendMessage("Members: "+whitelist.get(index));
                            player.sendMessage("Flags: ");
                            player.sendMessage("This lot is "
                                    + Integer.toString(LotsofLots.lotLocs.get(index).get(1).getBlockX()
                                    - LotsofLots.lotLocs.get(index).get(0).getBlockX()) + "x"
                                    + Integer.toString(LotsofLots.lotLocs.get(index).get(1).getBlockZ()
                                    - LotsofLots.lotLocs.get(index).get(0).getBlockZ())
                                    + " and spans from ("
                                    + LotsofLots.lotLocs.get(index).get(0).getBlockX()
                                    + ", " + LotsofLots.lotLocs.get(index).get(0).getBlockZ() + ") to ("
                                    + LotsofLots.lotLocs.get(index).get(1).getBlockX()
                                    + ", " + LotsofLots.lotLocs.get(index).get(1).getBlockZ()
                                    + ").");
                        }
                    }
                }
                if (!lotfound) {
                    player.sendMessage("There is no Lot here!");
                }

            }else if (args.length == 1){
                List<Player> players = this.getServer().matchPlayer(args[0]);
                    if (players.isEmpty()) {
                        player.sendMessage(ChatColor.RED
                                + "No Matching Player");
                    } else if (players.size() > 1) {
                        player.sendMessage(ChatColor.RED
                                + "More than one matching player");
                        player.sendMessage(ChatColor.RED
                                + "Be more specific!");
                    } else if (players.size() == 1) {
                        String username = players.get(0).getName();
                        if(!numLots.containsKey(username)){
                            numLots.put(username, 0);
                        }
                        ArrayList<Integer> owns = new ArrayList<Integer>();
                        for (int index : lotOwn.keySet()){
                            if (lotOwn.get(index).equalsIgnoreCase(username)){
                                owns.add(index);
                            }
                        }
                        player.sendMessage(username+" owns "+numLots.get(username)+" Lot(s).");
                        player.sendMessage("With LotIDs of: "+owns);
                    }
            }
        }

        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        else if (commandLabel.equalsIgnoreCase("lotallow")) {
            if (args.length == 1) {
                //if player is standing in a lot they own.
                boolean lotfound = false;
                for (int index : lotLocs.keySet()) {
                    if ((player.getWorld() == lotLocs.get(index).get(0).getWorld())
                            && (player.getLocation().getBlockX() >= lotLocs.get(index).get(0).getBlockX())
                            && (player.getLocation().getBlockX() <= lotLocs.get(index).get(1).getBlockX())
                            && (player.getLocation().getBlockZ() >= lotLocs.get(index).get(0).getBlockZ())
                            && (player.getLocation().getBlockZ() <= lotLocs.get(index).get(1).getBlockZ())) {
                        lotfound = true;
                        if (lotOwn.get(index).equalsIgnoreCase(playername)) {
                            List<Player> players = this.getServer().matchPlayer(args[0]);
                            if (players.isEmpty()) {
                                player.sendMessage(ChatColor.RED
                                        + "No Matching Player");
                            } else if (players.size() > 1) {
                                player.sendMessage(ChatColor.RED
                                        + "More than one matching player");
                                player.sendMessage(ChatColor.RED
                                        + "Be more specific!");
                            } else if (players.size() == 1) {
                                //Do stuff
                                if (!whitelist.get(index).contains(players.get(0).getName())) {
                                    whitelist.get(index).add(players.get(0).getName());
                                    player.sendMessage(ChatColor.GREEN + players.get(0).getName() + " was added to the whitelist.");
                                } else {
                                    player.sendMessage(ChatColor.RED + players.get(0).getName() + " is already allowed on this Lot!");
                                }
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You do not own this Lot!");
                        }
                    }
                }
                if (!lotfound) {
                    player.sendMessage(ChatColor.RED+"You are not standing in a Lot.");
                }
                
            } else if (args.length == 2) { //lotallow lotid player
                //if you own the lot with the id given.
                int lotid = Integer.parseInt(args[0]);
                if (lotOwn.get(lotid).equalsIgnoreCase(playername)) {
                    List<Player> players = this.getServer().matchPlayer(args[1]);
                    if (players.isEmpty()) {
                        player.sendMessage(ChatColor.RED
                                + "No Matching Player");
                    } else if (players.size() > 1) {
                        player.sendMessage(ChatColor.RED
                                + "More than one matching player");
                        player.sendMessage(ChatColor.RED
                                + "Be more specific!");
                    } else if (players.size() == 1) {
                        //Do stuff
                        if(!whitelist.get(lotid).contains(players.get(0).getName())){
                            whitelist.get(lotid).add(players.get(0).getName());
                            player.sendMessage(ChatColor.GREEN + players.get(0).getName() + " was added to the whitelist.");
                        }
                        else
                            player.sendMessage(ChatColor.RED+players.get(0).getName()+" is already allowed on this Lot!");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You do not own the Lot with that id.");
                    player.sendMessage(ChatColor.RED + "Type /lot <yourname> to see your Lots.");
                }


            } else {
                player.sendMessage(ChatColor.RED + "Incorrect use of lotallow!");
                player.sendMessage(ChatColor.RED + "Please use /lotallow <playername> if in a Lot.");
                player.sendMessage(ChatColor.RED + "Or /lotallow <LotID> <playername> if you own the Lot.");
            }
        }

        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        else if (commandLabel.equalsIgnoreCase("lotdeny")) {
            if (args.length == 1) {
                //if player is standing in a lot they own.
                boolean lotfound = false;
                for (int index : lotLocs.keySet()) {
                    if ((player.getWorld() == lotLocs.get(index).get(0).getWorld())
                            && (player.getLocation().getBlockX() >= lotLocs.get(index).get(0).getBlockX())
                            && (player.getLocation().getBlockX() <= lotLocs.get(index).get(1).getBlockX())
                            && (player.getLocation().getBlockZ() >= lotLocs.get(index).get(0).getBlockZ())
                            && (player.getLocation().getBlockZ() <= lotLocs.get(index).get(1).getBlockZ())) {
                        lotfound = true;
                        if (lotOwn.get(index).equalsIgnoreCase(playername)) {
                            List<Player> players = this.getServer().matchPlayer(args[0]);
                            if (players.isEmpty()) {
                                player.sendMessage(ChatColor.RED
                                        + "No Matching Player");
                            } else if (players.size() > 1) {
                                player.sendMessage(ChatColor.RED
                                        + "More than one matching player");
                                player.sendMessage(ChatColor.RED
                                        + "Be more specific!");
                            } else if (players.size() == 1) {
                                //Do stuff
                                if (whitelist.get(index).contains(players.get(0).getName())) {
                                    whitelist.get(index).remove(players.get(0).getName());
                                    player.sendMessage(ChatColor.GREEN + players.get(0).getName() + " was removed from the whitelist.");
                                } else {
                                    player.sendMessage(ChatColor.RED + "That player was not on the whitelist!");
                                }
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You do not own this Lot!");
                        }
                    }
                }
                if (!lotfound) {
                    player.sendMessage(ChatColor.RED+"You are not standing in a Lot.");
                }

            } else if (args.length == 2) { //lotdeny lotid player
                //if you own the lot with the id given.
                int lotid = Integer.parseInt(args[0]);
                
                if (lotOwn.get(lotid).equalsIgnoreCase(playername)) {
                    List<Player> players = this.getServer().matchPlayer(args[1]);
                    if (players.isEmpty()) {
                        player.sendMessage(ChatColor.RED
                                + "No Matching Player");
                    } else if (players.size() > 1) {
                        player.sendMessage(ChatColor.RED
                                + "More than one matching player");
                        player.sendMessage(ChatColor.RED
                                + "Be more specific!");
                    } else if (players.size() == 1) {
                        //Do stuff
                        if (whitelist.get(lotid).contains(players.get(0).getName())) {
                            whitelist.get(lotid).remove(players.get(0).getName());
                            player.sendMessage(ChatColor.GREEN + players.get(0).getName() + " was removed from the whitelist.");
                        } else {
                            player.sendMessage(ChatColor.RED+"That player was not on the whitelist!");
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You do not own the Lot with that id.");
                    player.sendMessage(ChatColor.RED + "Type /lot <yourname> to see your Lots.");
                }


            } else {
                player.sendMessage(ChatColor.RED + "Incorrect use of lotdeny!");
                player.sendMessage(ChatColor.RED + "Please use /lotdeny <playername> if in a Lot.");
                player.sendMessage(ChatColor.RED + "Or /lotdeny <LotID> <playername> if you own the Lot.");
            }
        }

        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        else if (commandLabel.equalsIgnoreCase("setflag")) {
        }

        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        else if (commandLabel.equalsIgnoreCase("gotolot")) {
            if (args.length == 1){
                int index = Integer.parseInt(args[0]);
                if (lotOwn.containsKey(index)){
                    if (lotJump.containsKey(index)){
                        player.teleport(lotJump.get(index));
                        player.sendMessage(ChatColor.GREEN+"Going to Lot with ID of "+index);
                    }else{ //Lot doesn't have a jump location
                        player.sendMessage(ChatColor.RED + "This Lot does't have a jump location,");
                        player.sendMessage(ChatColor.RED + "set it with /ljumpset <LotID>.");
                    }
                }else{ //Lot doesn't exist
                    player.sendMessage(ChatColor.RED+"Lot with that ID does not exist!");
                }
            }else if (args.length == 2) {
                List<Player> players = this.getServer().matchPlayer(args[0]);
                if (players.isEmpty()) {
                    player.sendMessage(ChatColor.RED
                            + "No Matching Player");
                } else if (players.size() > 1) {
                    player.sendMessage(ChatColor.RED
                            + "More than one matching player");
                    player.sendMessage(ChatColor.RED
                            + "Be more specific!");
                } else {// if (players.size() == 1) {
                    //Do stuff
                    Player user = players.get(0);
                    String username = user.getName();
                    int index = Integer.parseInt(args[1]);
                    int indexnew;

                    if (pgExchange.containsKey(username)) {
                        if (pgExchange.get(username).containsKey(index)) {
                            indexnew = pgExchange.get(username).get(index);
                            if (lotJump.containsKey(indexnew)) {
                                player.teleport(lotJump.get(indexnew));
                                player.sendMessage(ChatColor.GREEN + "Going to Lot with ID of " + indexnew);
                            } else { //Lot doesn't have a jump location
                                player.sendMessage(ChatColor.RED + "This Lot does't have a jump location,");
                                player.sendMessage(ChatColor.RED + "set it with /ljumpset <LotID>.");
                            }
                        } else { //player doesn't own lot with this number
                            player.sendMessage(ChatColor.RED + "Lot with that ID does not exist!");
                        }
                    } else { //this player doesn't own any lots
                        player.sendMessage(ChatColor.RED + "That player doesn't own any Lots!");
                    }
                }
            } else { //command wasn't called right
                player.sendMessage(ChatColor.RED + "Incorrect Syntax!");
                player.sendMessage(ChatColor.RED + "Please use /goto <LotID>,");
                player.sendMessage(ChatColor.RED + "or /goto <player> <Lotnumber>");
            }
        }

        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        else if (commandLabel.equalsIgnoreCase("mylot")) {
            int index = Integer.parseInt(args[0]);
            int indexnew;

            if (args.length == 1) {
                if (pgExchange.containsKey(playername)) {
                    if (pgExchange.get(playername).containsKey(index)) {
                        indexnew = pgExchange.get(playername).get(index);
                        if (lotJump.containsKey(indexnew)) {
                            player.sendMessage(ChatColor.GREEN+"Going to Lot with ID of "+indexnew);
                            player.teleport(lotJump.get(indexnew));
                        } else { //Lot doesn't have a jump location
                            player.sendMessage(ChatColor.RED + "This Lot does't have a jump location,");
                            player.sendMessage(ChatColor.RED + "set it with /ljumpset <LotID>.");
                        }
                    } else { //player doesn't own lot with this number
                        player.sendMessage(ChatColor.RED+"Lot with that ID does not exist!");
                    }
                } else { //this player doesn't own any lots
                    player.sendMessage(ChatColor.RED+"You don't own any Lots!");
                }
            } else { //didn't use the command correctly
                player.sendMessage(ChatColor.RED+"Incorrect Syntax!");
                player.sendMessage(ChatColor.RED+"Please use /mylot <lotnumber>.");
            }
        } //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        else if (commandLabel.equalsIgnoreCase("ljumpset")) {
            if (args.length == 1){
                int index = Integer.parseInt(args[0]);

                if (lotOwn.containsKey(index)) {
                    if (lotOwn.get(index).equalsIgnoreCase(playername)) {
                        lotJump.put(index, player.getLocation().getBlock().getLocation());
                        player.sendMessage(ChatColor.GREEN + "Lot jump location set.");
                    } else { //player doesn't own lot!
                        player.sendMessage(ChatColor.RED + "You do not own the Lot with that ID!");
                    }
                } else { //lot doesn't exist
                    player.sendMessage(ChatColor.RED+"Lot with that ID does not exist!");
                }
            } else {
                player.sendMessage(ChatColor.RED+"Incorrect Syntax!");
                player.sendMessage(ChatColor.RED+"Please use /ljumpset <LotID>.");
            }
        }
        return true;
    }
    private static Location bigger(Location loc1, Location loc2){
        int x1, x2, z1, z2;
        x1=loc1.getBlockX();
        x2=loc2.getBlockX();
        z1=loc1.getBlockZ();
        z2=loc2.getBlockZ();

        return new Location(loc1.getWorld(), (x1>x2 ? x1 : x2), loc1.getBlockY(), (z1>z2 ? z1 : z2));
    }
    private static Location smaller(Location loc1, Location loc2){
        int x1, x2, z1, z2;
        x1=loc1.getBlockX();
        x2=loc2.getBlockX();
        z1=loc1.getBlockZ();
        z2=loc2.getBlockZ();

        return new Location(loc1.getWorld(), (x1<x2 ? x1 : x2), loc1.getBlockY(), (z1<z2 ? z1 : z2));
    }
    private static int localID(String playername, int index){
        for(int local : pgExchange.get(playername).keySet()){
            if (pgExchange.get(playername).get(local).equals(index)){
                return local;
            }
        }
        return index;
    }
}
