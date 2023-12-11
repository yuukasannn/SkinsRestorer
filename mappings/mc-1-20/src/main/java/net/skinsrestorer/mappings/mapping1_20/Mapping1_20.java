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
package net.skinsrestorer.mappings.mapping1_20;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.biome.BiomeManager;
import net.skinsrestorer.mappings.shared.IMapping;
import net.skinsrestorer.mappings.shared.MappingReflection;
import net.skinsrestorer.mappings.shared.ViaPacketData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class Mapping1_20 implements IMapping {
    private static void sendPacket(ServerPlayer player, Packet<?> packet) {
        player.connection.send(packet);
    }

    @Override
    public void accept(Player player, Predicate<ViaPacketData> viaFunction) {
        ServerPlayer entityPlayer = MappingReflection.getHandle(player, ServerPlayer.class);

        ClientboundPlayerInfoRemovePacket removePlayer = new ClientboundPlayerInfoRemovePacket(List.of(player.getUniqueId()));
        ClientboundPlayerInfoUpdatePacket addPlayer = ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(entityPlayer));

        // Slowly getting from object to object till we get what is needed for
        // the respawn packet
        ServerLevel world = entityPlayer.serverLevel();
        ServerPlayerGameMode gamemode = entityPlayer.gameMode;

        ClientboundRespawnPacket respawn = new ClientboundRespawnPacket(
                world.dimensionTypeId(),
                world.dimension(),
                BiomeManager.obfuscateSeed(world.getSeed()),
                gamemode.getGameModeForPlayer(),
                gamemode.getPreviousGameModeForPlayer(),
                world.isDebug(),
                world.isFlat(),
                ClientboundRespawnPacket.KEEP_ALL_DATA,
                entityPlayer.getLastDeathLocation(),
                entityPlayer.getPortalCooldown()
        );

        Location l = player.getLocation();

        sendPacket(entityPlayer, removePlayer);
        sendPacket(entityPlayer, addPlayer);

        @SuppressWarnings("deprecation")
        int dimension = player.getWorld().getEnvironment().getId();

        if (viaFunction.test(new ViaPacketData(player, dimension, respawn.getSeed(), respawn.getPlayerGameType().getId(), respawn.isFlat()))) {
            sendPacket(entityPlayer, respawn);
        }

        entityPlayer.onUpdateAbilities();

        sendPacket(entityPlayer, new ClientboundPlayerPositionPacket(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch(), new HashSet<>(), 0));

        entityPlayer.resetSentInfo();

        PlayerList playerList = entityPlayer.server.getPlayerList();
        playerList.sendPlayerPermissionLevel(entityPlayer);
        playerList.sendLevelInfo(entityPlayer, world);
        playerList.sendAllPlayerInfo(entityPlayer);

        // Resend their effects
        for (MobEffectInstance mobEffect : entityPlayer.getActiveEffects()) {
            ClientboundUpdateMobEffectPacket effect = new ClientboundUpdateMobEffectPacket(entityPlayer.getId(), mobEffect);
            sendPacket(entityPlayer, effect);
        }
    }

    @Override
    public Set<String> getSupportedVersions() {
        return Set.of(
                "34f399b4f2033891290b7f0700e9e47b", // 1.20
                "bcf3dcb22ad42792794079f9443df2c0" // 1.20.1
        );
    }
}
