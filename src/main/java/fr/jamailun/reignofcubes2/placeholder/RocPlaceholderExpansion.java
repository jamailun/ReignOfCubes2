package fr.jamailun.reignofcubes2.placeholder;

import fr.jamailun.reignofcubes2.GameManager;
import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.configuration.WorldConfiguration;
import fr.jamailun.reignofcubes2.messages.Messages;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RocPlaceholderExpansion extends PlaceholderExpansion {

    private final GameManager game;

    public RocPlaceholderExpansion(GameManager game) {
        this.game = game;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String param) {
        // Common:
        // - game_status
        // - map
        // - online

        switch (param.toLowerCase()) {
            case "game_status": {
                return Messages.format(languageOfPlayer(player), "tab.game-state." + game.getState());
            }
            case "map": {
                return config() == null ? "§7none" : config().getName();
            }
            case "map_author": {
                return config() == null ? "§7none" : config().getAuthor();
            }
            case "online": {
                return "" + game.getPlayersCount();
            }
        }

        // Player-specific
        if(player == null) return "§4§lnull";
        RocPlayer rocPlayer = game.toPlayer(player);
        if(rocPlayer == null) {
            ReignOfCubes2.error("Player is not a roc-player: " + player.getName());
            return "§4not_a_player";
        }
        return getRequest(rocPlayer, param);
    }

    private @Nullable String getRequest(RocPlayer player, String param) {
        return switch (param.toLowerCase()) {
            case "score" -> String.valueOf(player.getScore());
            case "is_king" -> player.isKing() ? "1" : "0";
            case "king" -> player.isKing() ? player.i18n("king.you") : game.hasKing() ? game.getKing().getName() : player.i18n("king.none");
            default -> {
                ReignOfCubes2.error("[PlaceHolder] Invalid param for ROC: '" + param + "'.");
                yield null;
            }
        };
    }

    private String languageOfPlayer(Player p) {
        RocPlayer player = game.toPlayer(p);
        return player == null ? Messages.getDefaultLanguage() : player.getLanguage();
    }


    @Override
    public @NotNull String getIdentifier() {
        return "roc";
    }

    @Override
    public @NotNull String getAuthor() {
        return "jamailun";
    }

    @Override
    public @NotNull String getVersion() {
        return ReignOfCubes2.getMeta().getVersion();
    }

    private WorldConfiguration config() {
        return game.getWorldConfiguration();
    }
}
