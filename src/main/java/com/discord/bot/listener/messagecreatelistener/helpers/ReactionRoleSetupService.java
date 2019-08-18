package com.discord.bot.listener.messagecreatelistener.helpers;

import com.discord.bot.util.ApiUtil;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ReactionRoleSetupService {

    public final String prefix = "t!reactionrole";

    Pattern pattern = Pattern.compile("<:[A-Za-z0-9]+:([0-9]+)>");

    @Autowired
    ApiUtil apiUtil;

    public static HashMap<String, HashMap<Long, Role>> messageReactionRoles = new HashMap<>();

    public void handleReactionRoleSetupRequest(MessageCreateEvent messageCreateEvent, String prefix) {
        System.out.println("starting reaction role setup");
        String[] params = apiUtil.extractArguments(messageCreateEvent, prefix);
        if (params.length >= 3 && params.length % 2 == 1) {
            try {
                messageCreateEvent.getServer().ifPresent(server -> {
                    generateRoleMapForMessageAndRoles(server, params);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void generateRoleMapForMessageAndRoles(Server server, String[] params) {

        HashMap<Long, Role> emojiRoleMap;

        if (messageReactionRoles.containsKey(params[0])) {
            emojiRoleMap = messageReactionRoles.get(params[0]);
        } else {
            emojiRoleMap = new HashMap<>();
            messageReactionRoles.put(params[0], emojiRoleMap);
        }

        for (int i = 1; i < params.length - 1; i += 2) {
            Matcher matcher = pattern.matcher(params[i]);
            if (matcher.matches()) { //the string is a custom emoji
                Long idAsNum = Long.parseLong(matcher.group(1));
                giveRoleBasedOnReaction(server, params[i + 1], emojiRoleMap, idAsNum);
            } else { //the string is a unicode emoji
                giveRoleBasedOnReaction(server, params[i + 1], emojiRoleMap, Integer.toUnsignedLong(params[i].hashCode()));
            }
        }
    }

    private void giveRoleBasedOnReaction(Server server, String roleVal, HashMap<Long, Role> emojiRoleMap, Long idAsNum) {
        Optional<Role> matchingRole = server.getRoles().stream()
                .filter(role -> role.getName().equalsIgnoreCase(roleVal)).findFirst();
        matchingRole.ifPresent(roleMatch -> emojiRoleMap.put(idAsNum, roleMatch));
    }
}
