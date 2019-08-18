package com.discord.bot.listener.messagecreatelistener;

import com.discord.bot.listener.messagecreatelistener.helpers.*;
import com.discord.bot.util.ApiUtil;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HUITMessageCreateListener implements MessageCreateListener {
    @Autowired
    ApiUtil apiUtil;

    @Autowired
    BanRequestService banRequestService;
    @Autowired
    KickRequestService kickRequestService;
    @Autowired
    MessageHistoryGraphRequestService messageHistoryGraphRequestService;
    @Autowired
    MuteRequestService muteRequestService;
    @Autowired
    ReactionRoleSetupService reactionRoleSetupService;
    @Autowired
    SetColorRequestService setColorRequestService;
    @Autowired
    DefaultRoleService defaultRoleService;

    public final String banPrefix = "t!ban";
    public final String kickPrefix = "t!kick";
    public final String messageHistoryGraphPrefix = "t!graph";
    public final String mutePrefix = "t!mute";
    public final String reactionRolePrefix = "t!reactionrole";
    public final String setColorPrefix = "t!color";
    public final String newbieSetRole = "t!noobrole";

    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        if (messageCreateEvent != null && messageCreateEvent.getMessageContent() != null) {
            int spaceIndex = messageCreateEvent.getMessageContent().indexOf(" ");
            switch(messageCreateEvent.getMessageContent().substring(0, spaceIndex == -1 ? messageCreateEvent.getMessageContent().length() : spaceIndex)) {
                case banPrefix:
                    new Thread(() -> {
                        banRequestService.handleBanRequest(messageCreateEvent, banPrefix);
                    }).start();
                    break;
                case kickPrefix:
                    new Thread(() -> {
                        kickRequestService.handleKickRequest(messageCreateEvent, kickPrefix);
                    }).start();
                    break;
                case messageHistoryGraphPrefix:
                    new Thread(() -> {
                        messageHistoryGraphRequestService.handleMessageHistoryGraphRequest(messageCreateEvent, messageHistoryGraphPrefix);
                    }).start();
                    break;
                case mutePrefix:
                    new Thread(() -> {
                        muteRequestService.handleMuteRequest(messageCreateEvent, mutePrefix);
                    }).start();
                    break;
                case reactionRolePrefix:
                    new Thread(() -> {
                        reactionRoleSetupService.handleReactionRoleSetupRequest(messageCreateEvent, reactionRolePrefix);
                    }).start();
                    break;
                case setColorPrefix:
                    new Thread(() -> {
                        setColorRequestService.handleSetColorRequest(messageCreateEvent, setColorPrefix);
                    }).start();
                    break;
                case newbieSetRole:
                    new Thread(() -> {
                        defaultRoleService.addDefaultRole(messageCreateEvent, newbieSetRole);
                    }).start();
                default:
                    System.out.println(messageCreateEvent);
            }
        }
    }
}
