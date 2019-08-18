package com.discord.bot.listener.messagedeletelistener;


import com.discord.bot.util.ApiUtil;
import org.apache.logging.log4j.util.TriConsumer;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageDeleteEvent;
import org.javacord.api.event.message.OptionalMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.time.Instant;

@Component
public class HUITMessageDeleteListener implements org.javacord.api.listener.message.MessageDeleteListener {

    @Autowired
    ApiUtil apiUtil;

    @Override
    public void onMessageDelete(MessageDeleteEvent messageDeleteEvent) {
        apiUtil.getChannelForLogging(messageDeleteEvent.getServer().get()).sendMessage(messageDeleteConsumer(messageDeleteEvent));
    }

    private EmbedBuilder messageDeleteConsumer(OptionalMessageEvent messageDeleteEvent) {
        EmbedBuilder embed = new EmbedBuilder();
        String authorId = "?";
        String channelId = "?";
        String messageId = "?";
        String content = "";
        MessageAuthor author = null;
        Message message = null;

        if (messageDeleteEvent.getMessageAuthor().isPresent()) {
            author = messageDeleteEvent.getMessageAuthor().get();
            authorId = author.getIdAsString();
            embed.setAuthor(author);
        }
        if (messageDeleteEvent.getMessage().isPresent()) {
            message = messageDeleteEvent.getMessage().get();
            messageId = message.getIdAsString();
            content = message.getContent();
        }
        embed.setTimestampToNow();
        if (messageDeleteEvent.getChannel() != null) {
            channelId = messageDeleteEvent.getChannel().getIdAsString();
        }

        embed.setColor(Color.RED)
                .setDescription("**Message sent by <@" + authorId + "> deleted in <#" + channelId + ">**\n" + content)
                .setFooter("Author: " + authorId + " | Message ID: " + messageId);
        return embed;
    }
}
