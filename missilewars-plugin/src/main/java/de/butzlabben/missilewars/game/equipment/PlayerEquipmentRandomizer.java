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

import de.butzlabben.missilewars.Logger;
import de.butzlabben.missilewars.configuration.arena.ArenaConfig;
import de.butzlabben.missilewars.game.Game;
import de.butzlabben.missilewars.game.schematics.objects.Missile;
import de.butzlabben.missilewars.game.schematics.objects.Shield;
import de.butzlabben.missilewars.player.MWPlayer;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

/**
 * @author Butzlabben
 * @since 19.01.2018
 */

public class PlayerEquipmentRandomizer {

    private final MWPlayer mwPlayer;
    private final Game game;
    private final ArenaConfig arenaConfig;
    private final EquipmentManager equipmentManager;

    private final int maxGameDuration;
    private final Random randomizer;
    private static final int DEFAULT_INTERVAL_BY_TEAM_AMOUNT = 20;
    private static final int DEFAULT_FACTOR_BY_GAME_TIME = 1;

    int playerInterval;
    int sendEquipmentCounter = 0;
    
    int startInterval;
    int respawnInterval;


    public PlayerEquipmentRandomizer(MWPlayer mwPlayer, Game game) {
        this.mwPlayer = mwPlayer;
        this.game = game;
        this.arenaConfig = game.getArenaConfig();
        this.equipmentManager = game.getEquipmentManager();
        randomizer = new Random();
        maxGameDuration = game.getArenaConfig().getGameDuration() * 60;
        
        this.startInterval = arenaConfig.getInterval().getCustomStartInterval();
        this.respawnInterval = arenaConfig.getInterval().getCustomRespawnInterval();

        initializePlayerInterval();
    }

    public void tick() {

        setPlayerInterval(playerInterval - 1);

        if (playerInterval <= 0) {

            sendRandomGameEquipment();
            setPlayerInterval(getBasisInterval());
        }
    }

    /**
     * This method set the countdown for the player equipment
     * randomizer for the game join.
     * <p> 
     * If specified start-interval is '-1', the basis-interval is used.
     */
    public void initializePlayerInterval() {
        if (startInterval == -1) {
            setPlayerInterval(getBasisInterval());
            return;
        }
        setPlayerInterval(startInterval);
    }
    
    /**
     * This method resets the countdown for the player equipment
     * randomizer after a respawn, if activated.
     * <p> 
     * If specified respawn-interval is '-1', the basis-interval is used.
     */
    public void resetPlayerInterval() {
        // config option 'resetAfterRespawn'
        if (!arenaConfig.getInterval().isResetAfterRespawn()) return;
        
        // adding 1 value before setting the player interval because of the timing
        if (respawnInterval == -1) {
            setPlayerInterval(getBasisInterval() + 1);
            return;
        }
        setPlayerInterval(respawnInterval + 1);
    }

    /**
     * This method sets the countdown for the player equipment
     * randomizer to a specified value.
     *
     * @param playerInterval (Integer) the target interval status
     */
    private void setPlayerInterval(Integer playerInterval) {
        this.playerInterval = playerInterval;
        mwPlayer.getPlayer().setLevel(playerInterval);
    }

    /**
     * This method returns the calculated basic interval dependent 
     * on the team amount and the current game time.
     *
     * @return (int) the interval in seconds
     */
    private int getBasisInterval() {
        return (int) Math.ceil(getIntervalByTeamAmount() * getFactorByGameTime());
    }

    /**
     * This method gives the player a random item of one of the two
     * game equipment lists. The two lists alternate: after two
     * missiles from the MissileEquipmentList, the player gets a
     * special item from the SpecialEquipmentList.
     */
    private void sendRandomGameEquipment() {

        ItemStack item;
        int randomID;

        // switch between type of "items":
        // after 2 missile items, you get one special item or a shield
        if (sendEquipmentCounter >= 2) {

            // Special Equipment or Schematic Game-Equipment of the type "Shield":
            
            int specialEquipment = equipmentManager.getSpecialEquipment().getSpecialEquipmentList().size();
            int shieldsEquipment = equipmentManager.getShieldEquipment().getSchematicEquipmentList().size();
            
            randomID = randomizer.nextInt(1, specialEquipment + shieldsEquipment + 1);
            if (randomID <= specialEquipment) {
                item = equipmentManager.getSpecialEquipment().getSpecialEquipmentList().get(randomID - 1);
            } else {
                Shield shield = (Shield) equipmentManager.getShieldEquipment().getSchematicEquipmentList().get(randomID - specialEquipment - 1);
                item = shield.getItem();
            }

            sendEquipmentCounter = 0;

        } else {

            // Schematic Game-Equipment of the type "Missile":
            
            int missilesEquipment = equipmentManager.getMissileEquipment().getSchematicEquipmentList().size();
            
            randomID = randomizer.nextInt(1, missilesEquipment + 1);
            Missile missile = (Missile) equipmentManager.getMissileEquipment().getSchematicEquipmentList().get(randomID - 1);
            item = missile.getItem();

        }

        if (item == null) return;

        mwPlayer.getPlayer().getInventory().addItem(item);
        sendEquipmentCounter++;
    }

    /**
     * This method returns the interval after the player receives a new
     * item during the game. It depends on the current team size and the
     * same or next lower key value in the config.
     *
     * @return (int) the interval in seconds
     */
    private int getIntervalByTeamAmount() {

        if (arenaConfig.getInterval().getIntervalsByTeamAmount().isEmpty()) {
            Logger.WARN.log("The given interval mapping in \"" + arenaConfig.getName() + "\" is empty. Choosing default value " + DEFAULT_INTERVAL_BY_TEAM_AMOUNT + ".");
            return DEFAULT_INTERVAL_BY_TEAM_AMOUNT;
        }

        int teamSize = mwPlayer.getTeam().getMembers().size();
        for (int i = teamSize; i > 0; i--) {
            if (arenaConfig.getInterval().getIntervalsByTeamAmount().containsKey(Integer.toString(i))) {
                return arenaConfig.getInterval().getIntervalsByTeamAmount().get(Integer.toString(i));
            }
        }

        Logger.DEBUG.log("No interval value for map \"" + arenaConfig.getName() + "\" could be detected based on the team amount of " + teamSize + ". Please define at least one a interval value for a minimal team amount of 1.");
        return DEFAULT_INTERVAL_BY_TEAM_AMOUNT;
    }

    /**
     * This method returns the interval factor after the player receives a new
     * item during the game. It depends on the current game time and the
     * same or next higher key value in the config.
     *
     * @return (int) the interval factor in seconds
     */
    private double getFactorByGameTime() {

        if (arenaConfig.getInterval().getIntervalFactorByGameTime().isEmpty()) {
            Logger.WARN.log("The given interval factor mapping in \"" + arenaConfig.getName() + "\" is empty. Choosing default value " + DEFAULT_FACTOR_BY_GAME_TIME + ".");
            return DEFAULT_FACTOR_BY_GAME_TIME;
        }

        int seconds = game.getTaskManager().getTimer().getSeconds();
        for (int i = seconds; i <= maxGameDuration; i++) {
            if (arenaConfig.getInterval().getIntervalFactorByGameTime().containsKey(Integer.toString(i))) {
                return arenaConfig.getInterval().getIntervalFactorByGameTime().get(Integer.toString(i));
            }
        }

        Logger.DEBUG.log("No interval factor value for map \"" + arenaConfig.getName() + "\" could be detected based on the game time of " + seconds + " seconds. Please define at least one a interval value for a minimal team amount of " + maxGameDuration + " seconds.");
        return DEFAULT_FACTOR_BY_GAME_TIME;
    }

}
