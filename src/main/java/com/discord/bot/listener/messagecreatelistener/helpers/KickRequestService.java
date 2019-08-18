package com.discord.bot.listener.messagecreatelistener.helpers;

import com.discord.bot.util.ApiUtil;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KickRequestService {

    @Autowired
    ApiUtil apiUtil;

    public void handleKickRequest(MessageCreateEvent messageCreateEvent, String prefix) {
        apiUtil.handleModerationTask(messageCreateEvent, prefix, (target, init, server) -> {
            if (server.canKickUser(init, target)) {
                server.kickUser(target);
            }
        });
    }
}
