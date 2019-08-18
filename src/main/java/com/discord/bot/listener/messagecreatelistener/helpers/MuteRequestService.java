package com.discord.bot.listener.messagecreatelistener.helpers;

import com.discord.bot.util.ApiUtil;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MuteRequestService {

    public final String prefix = "t!mute";

    @Autowired
    ApiUtil apiUtil;

    public void handleMuteRequest(MessageCreateEvent messageCreateEvent, String prefix) {
        apiUtil.handleModerationTask(messageCreateEvent, prefix, (target, init, server) -> {
            if (server.canMuteMembers(init)) {
                server.muteUser(target);
            }
        });
    }
}
