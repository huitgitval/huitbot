package com.discord.bot.util;

import org.apache.logging.log4j.util.TriConsumer;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.Event;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.OptionalMessageEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class ApiUtil {
    DiscordApi curApi = null;
    private final String token = "<token goes here, removed for safety>";
    //https://discordapp.com/oauth2/authorize?client_id=612061461530935303&scope=bot&permissions=8
    private final long channelForLogging = 568244181756018688L;

    public DiscordApi getApi() {
        if (curApi == null) {
            curApi = new DiscordApiBuilder().setToken(token).login().join();
        }
        return curApi;
    }

    public TextChannel getChannelForLogging(Server server) {
        try {
            for(Channel channel: server.getChannels()) {
                if (channel.asServerTextChannel().isPresent()) {
                    if (channel.asServerTextChannel().get().getName().toLowerCase().startsWith("dyno")) {
                        return channel.asServerTextChannel().get();
                    }
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean verifyCommandTrigger(MessageCreateEvent event, String prefix) {
        return event != null && event.getMessageContent() != null && event.getMessageContent().startsWith(prefix);
    }

    public String[] extractArguments(MessageCreateEvent event, String prefix) {
        if (event != null && event.getMessageContent() != null
                && event.getMessageContent().startsWith(prefix)
                && event.getMessageContent().length() > prefix.length() + 1) {
            String suffix = event.getMessageContent().substring(prefix.length() + 1);
            return suffix.contains(" ") ? suffix.split(" ") : new String[]{suffix};
        } else {
            return null;
        }
    }

    public void handleModerationTask(MessageCreateEvent messageCreateEvent, String prefix, TriConsumer<User, User, Server> operation) {
        if (verifyCommandTrigger(messageCreateEvent, prefix)) {
            System.out.println("user mute requested");
            Optional<Server> serverOpt = messageCreateEvent.getServer();
            serverOpt.ifPresent(server -> {
                Optional<User> initiator = messageCreateEvent.getMessageAuthor().asUser();
                initiator.ifPresent(init -> {
                    List<User> targetList = messageCreateEvent.getMessage().getMentionedUsers();
                    targetList.forEach(target -> operation.accept(target, init, server));
                });
            });
        }
    }



    public EmbedBuilder processGenericMessage(OptionalMessageEvent messageDeleteEvent, MessageAuthor author, Message message, EmbedBuilder embed) {
        Instant timestamp = null;

        if (message != null) {
            timestamp = message.getCreationTimestamp();
        }
        embed.setTimestamp(timestamp).setAuthor(author);
        return embed;

    }

}
