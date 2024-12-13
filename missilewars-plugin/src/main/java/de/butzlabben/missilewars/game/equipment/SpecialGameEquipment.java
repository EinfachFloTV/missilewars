/*
 * This file is part of MissileWars (https://github.com/Butzlabben/missilewars).
 * Copyright (c) 2018-2021 Daniel Nägele.
 *
 * MissileWars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MissileWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MissileWars.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.butzlabben.missilewars.game.equipment;

import de.butzlabben.missilewars.game.Game;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Butzlabben
 * @since 19.01.2018
 */

@Getter
public class SpecialGameEquipment {

    private final Game game;
    
    private ItemStack arrow;
    private ItemStack fireball;

    private List<ItemStack> specialEquipmentList = new ArrayList<>();


    public SpecialGameEquipment(Game game) {
        this.game = game;
        
        createArrow();
        createFireball();

        createSpecialEquipmentList();
    }

    /**
     * This method goes through all configured special equipment items
     * and adds them to the list. The higher the defined spawn-occurrence
     * of an item type being set, the more often it will be added to the list.
     * If the spawn-occurrence is 0, the equipment is skipped.
     */
    private void createSpecialEquipmentList() {
        
        int arrowOccurrence = game.getArenaConfig().getArrowConfig().getOccurrence();
        int fireballOccurrence = game.getArenaConfig().getFireballConfig().getOccurrence();

        for (int i = arrowOccurrence; i > 0; i--) {
            specialEquipmentList.add(arrow);
        }

        for (int i = fireballOccurrence; i > 0; i--) {
            specialEquipmentList.add(fireball);
        }

    }
    
    /**
     * This method creates the arrow item stack.
     */
    private void createArrow() {
        arrow = new ItemStack(Material.ARROW, game.getArenaConfig().getArrowConfig().getAmount());
    }

    /**
     * This method creates the fireball item stack.
     */
    private void createFireball() {
        fireball = new ItemStack(Material.FIRE_CHARGE);
        ItemMeta fireballMeta = fireball.getItemMeta();
        fireballMeta.setDisplayName(game.getArenaConfig().getFireballConfig().getName());
        fireball.setItemMeta(fireballMeta);
    }


}
