/*
 * Forge-Event-Bridge
 * Copyright (C) 2021  Liangbai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package site.liangbai.forgeeventbridge.util;

import org.bukkit.Bukkit;

public final class NMSVersion {
    public static String VERSION;

    static {
        VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    public static Class<?> getCraftBukkitClassOrNull(String className) {
        return Reflection.findClassOrNull("org.bukkit.craftbukkit." + VERSION + "." + className);
    }

    public static Class<?> getNMSClassOrNull(String className) {
        return Reflection.findClassOrNull("net.minecraft.server." + VERSION + "." + className);
    }
}
