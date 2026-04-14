package pl.norbit.treecuter.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import pl.norbit.treecuter.TreeCuter;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.service.ToggleService;
import pl.norbit.treecuter.utils.ChatUtils;

public class PlaceholderRegistry extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "treecuter";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Norbit";
    }

    @Override
    public @NotNull String getVersion() {
        return TreeCuter.getInstance().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.contains("toggle_raw")) {
            if(!ToggleService.getToggle(player.getUniqueId())){
                return  "false";
            }
            return "true";
        }else if (params.contains("toggle")) {
            if(!ToggleService.getToggle(player.getUniqueId())){
                return ChatUtils.format(Settings.getPlaceholderToggleOff());
            }
            return ChatUtils.format(Settings.getPlaceholderToggleOn());
        }
        return "";
    }
}
