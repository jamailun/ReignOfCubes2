package fr.jamailun.reignofcubes2.placeholder;

import fr.jamailun.reignofcubes2.GameManager;
import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.configuration.WorldConfiguration;
import fr.jamailun.reignofcubes2.messages.Messages;
import fr.jamailun.reignofcubes2.objects.Ceremony;
import fr.jamailun.reignofcubes2.objects.GameCountdown;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RocPlaceholderExpansion extends PlaceholderExpansion {

    private final static String NONE = "§7none";

    private final GameManager game;

    public RocPlaceholderExpansion(GameManager game) {
        this.game = game;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String param) {
        RocPlayer rocPlayer = game.toPlayer(player);
        String lan = (rocPlayer == null ? "fr" : rocPlayer.getLanguage());
        String common = getRequestAny(param, lan);

        if(common != null)
            return common;

        if(param.toLowerCase().startsWith("i18n")) {
            return handleI18n(param, lan);
        }
        if(param.toLowerCase().startsWith("ranking")) {
            return handleRanking(param, lan, rocPlayer);
        }

        if(rocPlayer == null) {
            return switch (param) {
                case "prefix_tag", "prefix_tab" -> "§7§o";
                case "score" -> Messages.format(lan, "tab.player.no-score");
                case "is_king" -> "0";
                case "king" -> game.hasKing() ? game.getKing().getName() : Messages.format(lan, "tab.player.none");
                default -> "";
            };
        }

        return getRequest(rocPlayer, param);
    }

    private String getRequestAny(String param, String lan) {
        return switch (param.toLowerCase()) {
            // Configuration
            case "game_status" -> Messages.format(lan, "tab.game-state." + game.getState());
            case "map" -> config() == null ? NONE : config().getName();
            case "map_author" -> config() == null ? NONE : config().getAuthor();
            case "online" -> String.valueOf(game.getOnlinePlayersCount());
            case "is_playing" -> bool(game.isPlaying());
            case "is_countdown" -> bool(game.isCountdown());

            // Countdown
            case "countdown" -> {
                GameCountdown countdown = game.getCountdown();
                if (countdown == null) yield "§cNo countdown.";
                yield String.valueOf(countdown.getRemaining());
            }

            // Ceremony
            case "is_ceremony" -> bool(game.isPlaying() && game.getCeremony() != null);
            case "ceremony_text" -> {
                Ceremony ceremony = game.getCeremony();
                if (ceremony == null) yield "§cNo ceremony.";
                yield Messages.format(lan, "tab.bars.ceremony", ceremony.getPlayerName());
            }
            case "ceremony_ratio" -> {
                Ceremony ceremony = game.getCeremony();
                if (ceremony == null) yield "0";
                yield String.valueOf(ceremony.getRatio() * 100);
            }
            case "ceremony_color" -> {
                Ceremony ceremony = game.getCeremony();
                yield ceremony == null ? "RED" : ceremony.getColor();
            }
            default -> null;
        };
    }

    private @Nullable String getRequest(RocPlayer player, String param) {
        return switch (param.toLowerCase()) {
            // properties
            //TODO more titles !!
            case "title" -> getRank(player) == 1 ? "&d&l[Top 1]" : "";
            case "score" -> String.valueOf(player.getScore());
            case "is_king" -> player.isKing() ? "1" : "0";
            case "king" -> player.isKing() ? player.i18n("tab.player.you") : game.hasKing() ? game.getKing().getName() : player.i18n("tab.player.none");
            case "king_color" -> player.isKing() ? "GREEN" : "BLUE";
            case "rank" -> String.valueOf(getRank(player));

            // prefix
            case "prefix_tag" -> player.isKing() ? player.i18n("tab.prefix.king.tag") : player.i18n("tab.prefix.player.tag");
            case "prefix_tab" -> player.isKing() ? player.i18n("tab.prefix.king.tab") : player.i18n("tab.prefix.player.tab");

            default -> {
                ReignOfCubes2.error("[PlaceHolder] Invalid param for ROC: '" + param + "'.");
                yield null;
            }
        };
    }

    private String handleI18n(String param, String lan) {
        String[] tokens = param.split(":", 2);
        if(tokens.length < 2) return "§4??'"+param+"'??";
        return Messages.format(lan, tokens[1]);
    }

    private String handleRanking(String param, String lan, @Nullable RocPlayer player) {
        if(!game.isPlaying()) {
            return "§4not_started";
        }
        String[] tokens = param.split(":", 2);
        if(tokens.length < 2) return "§4bad_rank";
        int index;
        try {
            index = Integer.parseInt(tokens[1]);
        } catch(NumberFormatException ignored) {
            return "§4bad_rank_number";
        }
        Optional<RocPlayer> ranking = game.getRanking().getElementAtRank(index);
        if(ranking.isEmpty()) {
            return "";
        }
        RocPlayer ranker = ranking.get();
        String rankerName = ranker.getName();
        if(ranker.equals(player)) {
            rankerName = player.i18n("tab.player.you");
        } else if(ranker.isKing()) {
            rankerName = "&6" + rankerName;
        }
        // Overrides color for AFK
        String prefix = ranker.isValid() ? "" : "&7&o";
        return Messages.format(lan, "tab.ranking.entry", (index+1), prefix, rankerName, ranker.getScore());
    }

    private int getRank(RocPlayer player) {
        if(!game.isPlaying() || player.getScore() == 0)
            return -1;
        return game.getRanking().getRankOf(player).orElse(-1) + 1;
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

    private String bool(boolean bool) {
        return bool ? "1" : "0";
    }
}
