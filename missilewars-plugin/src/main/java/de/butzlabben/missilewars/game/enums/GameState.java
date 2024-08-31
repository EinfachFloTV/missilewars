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

package de.butzlabben.missilewars.game.enums;

import de.butzlabben.missilewars.configuration.Messages;
import lombok.Getter;

/**
 * @author Butzlabben
 * @since 01.01.2018
 */
@Getter
public enum GameState {

    LOBBY (Messages.getMessage(false, Messages.MessageEnum.GAME_STATE_LOBBY)),
    INGAME (Messages.getMessage(false, Messages.MessageEnum.GAME_STATE_INGAME)),
    END (Messages.getMessage(false, Messages.MessageEnum.GAME_STATE_END)),
    ERROR (Messages.getMessage(false, Messages.MessageEnum.GAME_STATE_ERROR));

    private final String gameStateMsg;
    
    GameState(String gameStateMsg) {
        this.gameStateMsg = gameStateMsg;
    }
    
}
