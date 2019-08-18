package com.discord.bot.listener.reactionaddlistener;

import com.discord.bot.listener.messagecreatelistener.helpers.ReactionRoleSetupService;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class ReactionRoleTriggerListener implements ReactionAddListener {

    @Override
    public void onReactionAdd(ReactionAddEvent reactionAddEvent) {
        System.out.println("reaction triggered");
        if (ReactionRoleSetupService.messageReactionRoles.containsKey(Long.toString(reactionAddEvent.getMessageId()))) {
            System.out.println(ReactionRoleSetupService.messageReactionRoles.keySet());
            HashMap<Long, Role> reactionRolesForMessage = ReactionRoleSetupService.messageReactionRoles.get(Long.toString(reactionAddEvent.getMessageId()));
            if (reactionAddEvent.getEmoji().isCustomEmoji()) {
                reactionAddEvent.getEmoji().asCustomEmoji().ifPresent(customEmoji -> {
                    if (reactionRolesForMessage.containsKey(customEmoji.getId())) {
                        reactionRolesForMessage.get(customEmoji.getId()).addUser(reactionAddEvent.getUser());
                    }
                });
            } else if (reactionAddEvent.getEmoji().isUnicodeEmoji()) {
                reactionAddEvent.getEmoji().asUnicodeEmoji().ifPresent(unicodeEmoji -> {
                    Long hash = Integer.toUnsignedLong(unicodeEmoji.hashCode());
                    if (reactionRolesForMessage.containsKey(hash)) {
                        reactionRolesForMessage.get(hash).addUser(reactionAddEvent.getUser());
                    }
                });
            }
        }
    }
}
