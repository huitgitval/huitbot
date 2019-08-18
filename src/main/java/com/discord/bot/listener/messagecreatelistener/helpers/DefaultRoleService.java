package com.discord.bot.listener.messagecreatelistener.helpers;

import com.discord.bot.util.ApiUtil;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DefaultRoleService {
    @Autowired
    ApiUtil apiUtil;

    public List<Role> rolesForNewbies = new ArrayList<>();

    public void addDefaultRole(MessageCreateEvent messageCreateEvent, String prefix) {
        String[] val = apiUtil.extractArguments(messageCreateEvent, prefix);
        for (String role: val) {
            messageCreateEvent.getServer().ifPresent(server -> {
                messageCreateEvent.getMessageAuthor().asUser().ifPresent(caller -> {
                    if (server.canManageRoles(caller)) {
                        server.getRoles().forEach(serverRole -> {
                            if (serverRole.getName().equalsIgnoreCase(role)) {
                                rolesForNewbies.add(serverRole);
                            }
                        });
                    }
                });
            });
        }
    }
}
