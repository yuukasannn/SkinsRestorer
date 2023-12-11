/*
 * SkinsRestorer
 *
 * Copyright (C) 2023 SkinsRestorer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 */
package net.skinsrestorer.bukkit.skinrefresher;

import net.skinsrestorer.bukkit.utils.MappingManager;
import net.skinsrestorer.bukkit.utils.NoMappingException;
import net.skinsrestorer.mappings.shared.IMapping;
import net.skinsrestorer.mappings.shared.ViaPacketData;
import net.skinsrestorer.shared.log.SRLogger;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MappingSpigotSkinRefresher implements Consumer<Player> {
    private final IMapping mapping;
    private final boolean viaWorkaround;

    public MappingSpigotSkinRefresher(SRLogger logger, Server server, boolean viaWorkaround) throws NoMappingException {
        this.viaWorkaround = viaWorkaround;

        Optional<IMapping> mapping = MappingManager.getMapping(server);
        if (!mapping.isPresent()) {
            throw new NoMappingException(server);
        } else {
            this.mapping = mapping.get();
        }

        logger.debug("Using MappingSpigotSkinRefresher");
    }

    @Override
    public void accept(Player player) {
        Predicate<ViaPacketData> viaFunction;

        if (viaWorkaround) {
            viaFunction = ViaWorkaround::sendCustomPacketVia;
        } else {
            viaFunction = data -> true;
        }

        mapping.accept(player, viaFunction);
    }
}
