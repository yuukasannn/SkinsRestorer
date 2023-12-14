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
package net.skinsrestorer.shared.info;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.skinsrestorer.shared.plugin.SRPlatformAdapter;

@Getter
@RequiredArgsConstructor
public class EnvironmentInfo {
    private final boolean hybrid;
    private final Platform platform;
    private final PlatformType platformType;
    private final ClassInfo classInfo;

    public static EnvironmentInfo determineEnvironment(SRPlatformAdapter<?> adapter) {
        ClassInfo info = ClassInfo.get();

        Platform platform = adapter.getPlatform();

        // Find common hybrid class mixes
        boolean hybrid = (platform == Platform.BUNGEE_CORD && info.isVelocity())
                || (platform == Platform.VELOCITY && info.isBungeecord())
                || (platform == Platform.SPONGE && info.isCraftBukkit())
                || (platform == Platform.BUKKIT && (info.isSpongeVanilla() || info.isSpongeForge()));

        return new EnvironmentInfo(hybrid, platform, platform.getPlatformType(), info);
    }
}