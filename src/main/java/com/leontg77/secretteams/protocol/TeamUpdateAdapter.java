/*
 * Project: SecretTeams
 * Class: com.leontg77.secretteams.protocol.TeamUpdateAdapter
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Leon Vaktskjold <leontg77@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.leontg77.secretteams.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.leontg77.secretteams.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

/**
 * Team update adapter class.
 *
 * @author LeonTG77
 */
public class TeamUpdateAdapter extends PacketAdapter {
    private final Main plugin;

    public TeamUpdateAdapter(Main plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.SCOREBOARD_TEAM);

        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onPacketSending(PacketEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Server.SCOREBOARD_TEAM)) {
            return;
        }

        PacketContainer packet = event.getPacket();
        int mode = packet.getIntegers().read(1);

        if (mode != 0 && mode != 3) {
            return;
        }

        StructureModifier<Collection> arrays = packet.getSpecificModifier(Collection.class);

        Collection<String> teammates = (Collection<String>) arrays.read(0);
        Iterator<String> it = teammates.iterator();

        while (it.hasNext()) {
            String teammate = it.next();
            OfflinePlayer offline = Bukkit.getOfflinePlayer(teammate);

            if (offline == null) {
                Bukkit.getLogger().severe("Could not hide " + teammate + "'s from his team.");
                break;
            }

            UUID uuid = offline.getUniqueId();

            if (!plugin.hasAKill.contains(uuid)) {
                it.remove();
            }
        }

        arrays.write(0, teammates);
    }
}