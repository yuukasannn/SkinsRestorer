/*
 * SkinsRestorer
 *
 * Copyright (C) 2022 SkinsRestorer
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
package net.skinsrestorer.bukkit.utils;

import com.mojang.authlib.properties.Property;
import lombok.ToString;
import net.skinsrestorer.api.property.IProperty;

import java.lang.reflect.Method;

@ToString
public class BukkitProperty implements IProperty {
    private final Property property;

    public BukkitProperty(String name, String value) {
        property = new Property(name, value);
    }

    public BukkitProperty(String name, String value, String signature) {
        property = new Property(name, value, signature);
    }

    @Override
    public Object getHandle() {
        return property;
    }

    @Override
    public String getName() {
        return property.getName();
    }

    @Override
    public String getValue() {
        try {
            return property.getValue();
        } catch (NoSuchMethodError e) {
            try {
                Method method = property.getClass().getMethod("value");

                return (String) method.invoke(property);
            } catch (ReflectiveOperationException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public String getSignature() {
        try {
            return property.getSignature();
        } catch (NoSuchMethodError e) {
            try {
                Method method = property.getClass().getMethod("signature");

                return (String) method.invoke(property);
            } catch (ReflectiveOperationException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
