package fr.jamailun.reignofcubes2.tags;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import fr.jamailun.reignofcubes2.players.ScoreAddReason;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class MineurTag extends Tag implements Listener {

  /**
   * Create a new tag.
   * @param plugin the main plugin instance.
   */
  public MineurTag(ReignOfCubes2 plugin) {
    super("mineur", plugin);
  }

  @Override
  public void added(RocPlayer holder) {
    ReignOfCubes2.debug(holder.getName() + " is now mineur");
  }

  @Override
  public void removed(RocPlayer holder) {
    ReignOfCubes2.debug(holder.getName() + " is no longer mineur");
  }

  @EventHandler(priority = EventPriority.LOWEST)
  void whenMinerMines(BlockBreakEvent event) {
    RocPlayer hPlayer = getRocPlayer(event.getPlayer());
    var type = event.getBlock().getType();
    if(hPlayer != null && is(hPlayer) && ReignOfCubes2.getTags().isMinable(type)) {
      hPlayer.addScore(ReignOfCubes2.getTags().getMinablePoints(), ScoreAddReason.TAG_MINEUR);
      event.setCancelled(true);
      event.getBlock().setType(Material.BEDROCK);
      ReignOfCubes2.runTaskLater(() -> event.getBlock().setType(type), 0.25);
    }
  }

  @Override
  public void holderAttacks(RocPlayer holder, RocPlayer victim, EntityDamageByEntityEvent event) {

  }

  @Override
  public void holderDefends(RocPlayer holder, @Nullable RocPlayer attacker, EntityDamageEvent event) {

  }

}
