package com.klaidas.townyreligions;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class ReligionExpansion extends PlaceholderExpansion {

    private final TownyReligions plugin;
    private static final String META_KEY = "townyreligions.religion";

    public ReligionExpansion(TownyReligions plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "townyreligions";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Klaidas";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "";

        // TownyAPI in your version can always take a String name:
        Resident res = TownyAPI.getInstance().getResident(player.getName());

        if (params.equalsIgnoreCase("resident")) {
            if (res == null) return "";
            var f = res.getMetadata(META_KEY);
            if (f == null || f.getValue() == null) return "";
            return f.getValue().toString();
        }

        if (params.equalsIgnoreCase("town")) {
            if (res == null) return "";
            Town town = res.getTownOrNull();
            if (town == null) return "";
            var f = town.getMetadata(META_KEY);
            if (f == null || f.getValue() == null) return "";
            return f.getValue().toString();
        }

        if (params.equalsIgnoreCase("nation")) {
            if (res == null) return "";
            Nation nation = res.getNationOrNull();
            if (nation == null) return "";
            var f = nation.getMetadata(META_KEY);
            if (f == null || f.getValue() == null) return "";
            return f.getValue().toString();
        }

        return null;
    }
}
