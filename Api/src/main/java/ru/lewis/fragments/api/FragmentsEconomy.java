package ru.lewis.fragments.api;

import org.bukkit.entity.Player;

public interface FragmentsEconomy {
    void addFragments(Player player, int count);
    void removeFragments(Player player, int count);
    void setFragments(Player player, int count);
}
