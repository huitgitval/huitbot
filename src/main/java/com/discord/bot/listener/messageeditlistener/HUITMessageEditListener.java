package com.discord.bot.listener.messageeditlistener;

import com.discord.bot.util.ApiUtil;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageEditEvent;
import org.javacord.api.event.message.OptionalMessageEvent;
import org.javacord.api.listener.message.MessageEditListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.time.Instant;

@Component
public class HUITMessageEditListener implements MessageEditListener {

    @Autowired
    ApiUtil apiUtil;

    @Override
    public void onMessageEdit(MessageEditEvent messageEditEvent) {
        apiUtil.getChannelForLogging(messageEditEvent.getServer().get()).sendMessage(messageDeleteConsumer(messageEditEvent));
    }

    private EmbedBuilder messageDeleteConsumer(MessageEditEvent messageDeleteEvent) {
        EmbedBuilder embed = new EmbedBuilder();
        String authorId = "?";
        String channelId = "?";
        String messageId = "?";
        MessageAuthor author;
        Message message;
        String link = "";

        if (messageDeleteEvent.getMessageAuthor().isPresent()) {
            author = messageDeleteEvent.getMessageAuthor().get();
            authorId = author.getIdAsString();
            embed.setAuthor(author);
        }
        if (messageDeleteEvent.getMessage().isPresent()) {
            message = messageDeleteEvent.getMessage().get();
            messageId = message.getIdAsString();
        }
        embed.setTimestampToNow();
        if (messageDeleteEvent.getChannel() != null) {
            channelId = messageDeleteEvent.getChannel().getIdAsString();
        }
        if (messageDeleteEvent.getServer().isPresent()) {
            Server server = messageDeleteEvent.getServer().get();
            link = "[JUMP TO MESSAGE](https://discordapp.com/channels/" + server.getId() + "/" + channelId + "/" + messageId + ")";
        }

        if (messageDeleteEvent.getOldContent().isPresent()) {
            embed.addField("Old", messageDeleteEvent.getOldContent().get());
        }

        if (messageDeleteEvent.getNewContent() != null) {
            embed.addField("New", messageDeleteEvent.getNewContent());
        }

        embed.setColor(Color.BLUE)
                .setDescription("**Message edited by <@" + authorId + "> in <#" + channelId + ">** " + link + "\n")
                .setFooter("Author: " + authorId + " | Message ID: " + messageId);
        return embed;
    }
}
