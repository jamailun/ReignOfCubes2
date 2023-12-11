package fr.jamailun.reignofcubes2.placeholder;

import fr.jamailun.reignofcubes2.GameManager;
import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.configuration.WorldConfiguration;
import fr.jamailun.reignofcubes2.messages.Messages;
import fr.jamailun.reignofcubes2.objects.Ceremony;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RocPlaceholderExpansion extends PlaceholderExpansion {

    private final static String NONE = "§7none";

    private final GameManager game;

    public RocPlaceholderExpansion(GameManager game) {
        this.game = game;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String param) {
        // Common:
        switch (param.toLowerCase()) {
            case "game_status": {
                return Messages.format(languageOfPlayer(player), "tab.game-state." + game.getState());
            }
            case "map": {
                return config() == null ? NONE : config().getName();
            }
            case "map_author": {
                return config() == null ? NONE : config().getAuthor();
            }
            case "online": {
                return "" + game.getPlayersCount();
            }
            case "is_playing": return game.isPlaying() ? "1" : "0";
            case "is_ceremony": return game.isPlaying() && game.getCeremony() != null ? "1" : "0";
            case "ceremony_text": {
                Ceremony ceremony = game.getCeremony();
                if(ceremony == null) return "§cNo ceremony.";
                return Messages.format(languageOfPlayer(player), "tab.bars.ceremony", ceremony.getPlayerName());
            }
            case "ceremony_ratio": {
                Ceremony ceremony = game.getCeremony();
                if(ceremony == null) return "0";
                return String.valueOf(ceremony.getRatio() * 100);
            }
            case "ceremony_color": {
                return "YELLOW";
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
            // properties
            case "title" -> ""; //TODO titles ! :)
            case "score" -> String.valueOf(player.getScore());
            case "is_king" -> player.isKing() ? "1" : "0";
            case "king" -> player.isKing() ? player.i18n("tab.king.you") : game.hasKing() ? game.getKing().getName() : player.i18n("tab.king.none");
            // prefix
            case "prefix_tag" -> player.isKing() ? player.i18n("tab.prefix.king.tag") : player.i18n("tab.prefix.player.tag");
            case "prefix_tab" -> player.isKing() ? player.i18n("tab.prefix.king.tab") : player.i18n("tab.prefix.player.tab");
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
