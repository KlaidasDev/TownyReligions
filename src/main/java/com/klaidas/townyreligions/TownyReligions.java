package com.klaidas.townyreligions;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Nation;


public class TownyReligions extends JavaPlugin {

    private static TownyReligions instance;
    private List<String> allowedReligions = new ArrayList<>();

    public static TownyReligions getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        loadAllowedReligions();

        // clean old/default boards so map shows nice "Religion: %board%"
        cleanEmptyTownAndNationBoards();

        // register /religion
        ReligionCommand cmd = new ReligionCommand(this);
        getCommand("religion").setExecutor(cmd);
        getCommand("religion").setTabCompleter(cmd);

        // placeholderapi (optional)
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ReligionExpansion(this).register();
            getLogger().info("PlaceholderAPI found, registered TownyReligions placeholders.");
        }

        getLogger().info("TownyReligions enabled.");
    }

    public void cleanEmptyTownAndNationBoards() {
        TownyAPI api = TownyAPI.getInstance();

        // clean towns
        for (Town town : api.getTowns()) {
            String board = town.getBoard();
            if (board == null
                    || board.isEmpty()
                    || board.contains("/town set board")) {
                town.setBoard("Unknown");
                api.getDataSource().saveTown(town);
            }
        }

        // clean nations
        for (Nation nation : api.getNations()) {
            String board = nation.getBoard();
            if (board == null
                    || board.isEmpty()
                    || board.contains("/nation set board")) {
                nation.setBoard("Unknown");
                api.getDataSource().saveNation(nation);
            }
        }

        getLogger().info("Cleaned empty Town/Nation boards for religion display.");
    }

    public void loadAllowedReligions() {
        List<String> list = getConfig().getStringList("allowed-religions");
        if (list == null || list.isEmpty()) {
            getLogger().warning("No 'allowed-religions' in config.yml, using fallback.");
            list = List.of("Orthodox", "Catholic");
        }
        this.allowedReligions = list;
    }

    public List<String> getAllowedReligions() {
        return this.allowedReligions;
    }

    public String getMsg(String path) {
        return getConfig().getString("messages." + path, "&cMissing message: " + path);
    }
}
