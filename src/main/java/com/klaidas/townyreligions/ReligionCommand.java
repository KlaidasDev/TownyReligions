package com.klaidas.townyreligions;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReligionCommand implements CommandExecutor, TabCompleter {

    private static final String META_KEY = "townyreligions.religion";
    private final TownyReligions plugin;

    public ReligionCommand(TownyReligions plugin) {
        this.plugin = plugin;
    }

    private String color(String s) {
        return s.replace("&", "§");
    }

    private boolean isAllowedReligion(String r) {
        for (String s : plugin.getAllowedReligions()) {
            if (s.equalsIgnoreCase(r)) return true;
        }
        return false;
    }

    // ====== getters/setters for metadata ======
    private String getResidentReligion(Resident res) {
        if (res == null) return null;
        StringDataField f = (StringDataField) res.getMetadata(META_KEY);
        if (f == null || f.getValue() == null) return null;
        return f.getValue();
    }

    private void setResidentReligion(Resident res, String religion) {
        res.removeMetaData(META_KEY);
        res.addMetaData(new StringDataField(META_KEY, religion));
    }

    private void setTownReligion(Town town, String religion) {
        town.removeMetaData(META_KEY);
        town.addMetaData(new StringDataField(META_KEY, religion));
    }

    private void setNationReligion(Nation nation, String religion) {
        nation.removeMetaData(META_KEY);
        nation.addMetaData(new StringDataField(META_KEY, religion));
    }
    // ==========================================

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {

        boolean isAdmin = sender.hasPermission("townyreligions.admin")
                || sender.hasPermission("towny.command.townyadmin");

        Player pSender = null;
        Resident rSender = null;
        if (sender instanceof Player p) {
            pSender = p;
            rSender = TownyAPI.getInstance().getResident(p);
        }

        // /religion reload
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!isAdmin) {
                sender.sendMessage(color(plugin.getMsg("no-permission")));
                return true;
            }
            plugin.reloadConfig();
            plugin.loadAllowedReligions();
            sender.sendMessage(color(plugin.getMsg("prefix") + "&aConfig reloaded. Religions: &f"
                    + String.join(", ", plugin.getAllowedReligions())));
            return true;
        }

        // /religion
        if (args.length == 0) {
            sender.sendMessage(color(plugin.getMsg("list-commands")));
            sender.sendMessage(color("&d/religion list"));
            sender.sendMessage(color("&d/religion get"));
            sender.sendMessage(color("&d/religion set resident <player> <religion>"));
            sender.sendMessage(color("&d/religion set town <town> <religion>"));
            sender.sendMessage(color("&d/religion set nation <nation> <religion>"));
            sender.sendMessage(color("&d/religion reload"));
            return true;
        }

        // /religion list
        if (args[0].equalsIgnoreCase("list")) {
            sender.sendMessage(color(plugin.getMsg("list-header")));
            for (String rel : plugin.getAllowedReligions()) {
                sender.sendMessage(color("&e- " + rel));
            }
            return true;
        }

        // /religion get
        if (args[0].equalsIgnoreCase("get")) {
            if (pSender == null) {
                sender.sendMessage(color("&cPlayers only."));
                return true;
            }
            String rel = getResidentReligion(rSender);
            if (rel == null || rel.isEmpty()) {
                sender.sendMessage(color(plugin.getMsg("none")));
            } else {
                sender.sendMessage(color(plugin.getMsg("current").replace("%religion%", rel)));
            }
            return true;
        }

        // /religion set ...
        if (args[0].equalsIgnoreCase("set")) {
            if (args.length < 4) {
                sender.sendMessage(color("&cUsage: /religion set <resident|town|nation> <name> <religion>"));
                return true;
            }

            String targetType = args[1].toLowerCase(Locale.ROOT);
            String targetName = args[2];
            String newReligion = args[3];

            if (!isAllowedReligion(newReligion)) {
                sender.sendMessage(color(plugin.getMsg("invalid-religion")));
                return true;
            }

            switch (targetType) {
                case "resident": {
                    Resident res = TownyAPI.getInstance().getResident(targetName);
                    if (res == null) {
                        sender.sendMessage(color("&cResident not found."));
                        return true;
                    }

                    if (!isAdmin) {
                        if (pSender == null) {
                            sender.sendMessage(color(plugin.getMsg("no-permission")));
                            return true;
                        }
                        if (!res.getName().equalsIgnoreCase(pSender.getName())) {
                            sender.sendMessage(color("&cYou can only set your own religion."));
                            return true;
                        }
                    }

                    setResidentReligion(res, newReligion);
                    sender.sendMessage(color(plugin.getMsg("set-success")
                            .replace("%target%", res.getName())
                            .replace("%religion%", newReligion)));
                    return true;
                }

                case "town": {
                    Town playerTown = rSender != null ? rSender.getTownOrNull() : null;

                    // === PERMISSION CHECKS ===
                    if (!isAdmin) {
                        if (playerTown == null) {
                            sender.sendMessage(color("&cYou are not in a town."));
                            return true;
                        }
                        if (!playerTown.getMayor().getName().equalsIgnoreCase(rSender.getName())) {
                            sender.sendMessage(color("&cOnly the Mayor can set the town religion."));
                            return true;
                        }
                        if (!playerTown.getName().equalsIgnoreCase(targetName)) {
                            sender.sendMessage(color("&cYou can only set your own town's religion."));
                            return true;
                        }
                    }

                    // === FIND TARGET TOWN ===
                    Town town = TownyAPI.getInstance().getTown(targetName);
                    if (town == null) {
                        sender.sendMessage(color("&cTown not found."));
                        return true;
                    }

                    // === SAVE RELIGION TO METADATA ===
                    setTownReligion(town, newReligion);

                    // === UPDATE THE TOWN BOARD ===
                    town.setBoard(newReligion);
                    TownyAPI.getInstance().getDataSource().saveTown(town);

                    // === CONFIRMATION MESSAGE ===
                    sender.sendMessage(color(plugin.getMsg("set-success")
                            .replace("%target%", town.getName())
                            .replace("%religion%", newReligion)));

                    return true;
                }

                case "nation": {
                    Nation playerNation = rSender != null ? rSender.getNationOrNull() : null;

                    // === PERMISSION CHECKS ===
                    if (!isAdmin) {
                        if (playerNation == null) {
                            sender.sendMessage(color("&cYou are not in a nation."));
                            return true;
                        }
                        if (!playerNation.getKing().getName().equalsIgnoreCase(rSender.getName())) {
                            sender.sendMessage(color("&cOnly the Nation Leader can set the nation's religion."));
                            return true;
                        }
                        if (!playerNation.getName().equalsIgnoreCase(targetName)) {
                            sender.sendMessage(color("&cYou can only set your own nation's religion."));
                            return true;
                        }
                    }

                    // === FIND TARGET NATION ===
                    Nation nation = TownyAPI.getInstance().getNation(targetName);
                    if (nation == null) {
                        sender.sendMessage(color("&cNation not found."));
                        return true;
                    }

                    // === SAVE RELIGION TO METADATA ===
                    setNationReligion(nation, newReligion);

                    // === UPDATE THE NATION BOARD ===
                    nation.setBoard(newReligion);
                    TownyAPI.getInstance().getDataSource().saveNation(nation);

                    // === CONFIRMATION MESSAGE ===
                    sender.sendMessage(color(plugin.getMsg("set-success")
                            .replace("%target%", nation.getName())
                            .replace("%religion%", newReligion)));

                    return true;
                }

                default:
                    sender.sendMessage(color("&cUnknown target type. Use resident/town/nation."));
                    return true;
            }
        }

        return true;
    }

    // ===== tab complete =====
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String alias,
                                                @NotNull String[] args) {

        List<String> out = new ArrayList<>();

        boolean isAdmin = sender.hasPermission("townyreligions.admin")
                || sender.hasPermission("towny.command.townyadmin");

        // /religion
        if (args.length == 1) {
            out.add("list");
            out.add("get");
            out.add("set");
            out.add("reload");
            return out;
        }

        // /religion set
        if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            out.add("resident");
            out.add("town");
            out.add("nation");
            return out;
        }

        // /religion set <targetType> <name>
        if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            String targetType = args[1].toLowerCase(Locale.ROOT);

            if (sender instanceof Player player) {
                Resident res = TownyAPI.getInstance().getResident(player);

                switch (targetType) {
                    case "resident": {
                        out.add(player.getName());
                        break;
                    }
                    case "town": {
                        if (isAdmin) {
                            for (Town t : TownyAPI.getInstance().getTowns()) {
                                out.add(t.getName());
                            }
                        } else {
                            Town town = res != null ? res.getTownOrNull() : null;
                            if (town != null) {
                                out.add(town.getName());
                            }
                        }
                        break;
                    }
                    case "nation": {
                        if (isAdmin) {
                            for (Nation n : TownyAPI.getInstance().getNations()) {
                                out.add(n.getName());
                            }
                        } else {
                            Nation nation = res != null ? res.getNationOrNull() : null;
                            if (nation != null) {
                                out.add(nation.getName());
                            }
                        }
                        break;
                    }
                }
            }
            return out;
        }

        // /religion set <targetType> <name> <religion>
        if (args.length == 4 && args[0].equalsIgnoreCase("set")) {
            out.addAll(plugin.getAllowedReligions());
            return out;
        }

        return out;
    }
}
