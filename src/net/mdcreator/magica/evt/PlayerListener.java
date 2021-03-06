package net.mdcreator.magica.evt;

import net.mdcreator.magica.Magica;
import net.mdcreator.magica.spell.Spell;
import net.mdcreator.magica.spell.Spells;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class PlayerListener implements Listener {

    private final Magica plugin;
    private String title = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_PURPLE + "Magica" + ChatColor.DARK_GRAY + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;

    public PlayerListener(Magica plugin){
        this.plugin = plugin;
        plugin.server.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event){
        Spell spell = Spells.getSpellByCastName(event.getMessage().toLowerCase());
        if(spell!=null){
            if(!plugin.playerHas(event.getPlayer(), "magica.use.select")) return;
            event.getPlayer().setMetadata("spell", new FixedMetadataValue(plugin , spell.name));
            event.setFormat(ChatColor.DARK_GRAY + "<" +
                    ChatColor.BLUE + event.getPlayer().getDisplayName() + ChatColor.DARK_GRAY +
                    "> " + ChatColor.DARK_PURPLE + spell.asCapital());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        if((event.getAction()==Action.LEFT_CLICK_AIR||event.getAction()==Action.LEFT_CLICK_BLOCK)
                &&event.hasItem()&&event.getItem().getType()==Material.STICK){
            if(!plugin.playerHas(event.getPlayer(), "magica.use.wand")) return;
            Player player = event.getPlayer();
            if(event.getPlayer().hasMetadata("spell")){
                Spell spell = Spells.getSpellByName(player.getMetadata("spell").get(0).asString());
                if(spell!=null) spell.execute(player);
            }
        } else if((event.getAction()==Action.RIGHT_CLICK_AIR||event.getAction()==Action.RIGHT_CLICK_BLOCK)
                &&event.hasItem()&&event.getItem().getType()==Material.BOOK){
            if(!plugin.playerHas(event.getPlayer(), "magica.use.book")) return;
            Player player = event.getPlayer();
            player.sendMessage(title + "Spells List");
            for(Spell s: Spells.spells){
                player.sendMessage(s.asFormattedString());
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event){
        Entity entity = event.getEntity();
        if(entity==null) return;
        if(entity.hasMetadata("playerSpawned")){
            event.setCancelled(true);
            entity.getWorld().createExplosion(entity.getLocation(), 0.0f);
        }
    }
}
