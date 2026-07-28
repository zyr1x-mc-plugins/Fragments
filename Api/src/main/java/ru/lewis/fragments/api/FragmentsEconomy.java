package ru.lewis.fragments.api;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface FragmentsEconomy {
    void addFragments(Player player, int count);
    void removeFragments(Player player, int count);
    void setFragments(Player player, int count);

    void addFragments(UUID uniqueId, int count);
    void removeFragments(UUID uniqueId, int count);
    void setFragments(UUID uniqueId, int count);
}
