package Warpdrive.LotsofLots;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class LOLBListener extends BlockListener{

    private final LotsofLots plugin;

    public LOLBListener(final LotsofLots plugin){
        this.plugin = plugin;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event){
        Location block = event.getBlock().getLocation();
        Player player = event.getPlayer();
        String playername = player.getName();

        for (Integer index : LotsofLots.lotLocs.keySet()) {
            if (LotsofLots.lotOwn.get(index).equalsIgnoreCase(player.getName())) {
                continue;
            } else {
                /*System.out.println("x: "+LotsofLots.lotLocs.get(index).get(0).getBlockX()+" "
                        +block.getBlockX()+" "
                        +LotsofLots.lotLocs.get(index).get(1).getBlockX());
                System.out.println("z: "+LotsofLots.lotLocs.get(index).get(0).getBlockZ()+" "
                        +block.getBlockZ()+" "
                                     +LotsofLots.lotLocs.get(index).get(1).getBlockZ());*/
                if ((block.getWorld() == LotsofLots.lotLocs.get(index).get(0).getWorld())
                        && (block.getBlockX() >= LotsofLots.lotLocs.get(index).get(0).getBlockX())
                        && (block.getBlockX() <= LotsofLots.lotLocs.get(index).get(1).getBlockX())
                        && (block.getBlockZ() >= LotsofLots.lotLocs.get(index).get(0).getBlockZ())
                        && (block.getBlockZ() <= LotsofLots.lotLocs.get(index).get(1).getBlockZ())) {

                    if (!LotsofLots.whitelist.get(index).contains(playername)) {
                        event.setCancelled(true);
                        if (!LotsofLots.lotPrivate.get(index)) {
                            player.sendMessage(ChatColor.RED + "You are in " + LotsofLots.lotOwn.get(index) + "'s lot!");
                        } else {
                            player.sendMessage(ChatColor.RED + "This is not your lot!");
                        }
                    }
                }
            }
        }

        if (LotsofLots.p1.containsKey(playername)) {
            if (LotsofLots.p1.get(playername)) {
                LotsofLots.p1pos.put(playername, event.getBlock().getLocation());
                LotsofLots.p1.put(playername, false);
                player.sendMessage(ChatColor.GREEN + "Defined first corner of lot!");
                LotsofLots.p2.put(playername, true);
                player.sendMessage(ChatColor.GREEN + "Place block to define second corner of lot.");
            }else if (LotsofLots.p2.get(playername)) {
                LotsofLots.p2pos.put(playername, event.getBlock().getLocation());
                LotsofLots.p2.put(playername, false);
                player.sendMessage(ChatColor.GREEN + "Defined second corner of lot!");
                LotsofLots.createReady.put(playername, true);

                player.sendMessage(ChatColor.GREEN + "To create the lot for yourself type /lcreate.");
                player.sendMessage(ChatColor.GREEN + "Or for someone else type /lcreate <playername>.");

            }
        }
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event){
        Location block = event.getBlock().getLocation();
        Player player = event.getPlayer();
        String playername = player.getName();

        for (Integer index : LotsofLots.lotOwn.keySet()) {
            if (LotsofLots.lotOwn.get(index).equalsIgnoreCase(player.getName())) {
                continue;
            } else {
                if ((block.getWorld() == LotsofLots.lotLocs.get(index).get(0).getWorld())
                        && (block.getBlockX() >= LotsofLots.lotLocs.get(index).get(0).getBlockX())
                        && (block.getBlockX() <= LotsofLots.lotLocs.get(index).get(1).getBlockX())
                        && (block.getBlockZ() >= LotsofLots.lotLocs.get(index).get(0).getBlockZ())
                        && (block.getBlockZ() <= LotsofLots.lotLocs.get(index).get(1).getBlockZ())) {
                    if (!LotsofLots.whitelist.get(index).contains(playername)) {
                        event.setCancelled(true);
                        if (!LotsofLots.lotPrivate.get(index)) {
                            player.sendMessage(ChatColor.RED + "You are in " + LotsofLots.lotOwn.get(index) + "'s lot!");
                        } else {
                            player.sendMessage(ChatColor.RED + "This is not your lot!");
                        }
                    }
                }
            }
        }
    }
    
}
