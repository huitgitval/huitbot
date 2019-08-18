package com.discord.bot.listener.nicknamechangedlistener;

import com.discord.bot.util.ApiUtil;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageEditEvent;
import org.javacord.api.event.user.UserChangeNicknameEvent;
import org.javacord.api.listener.user.UserChangeNicknameListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class NickNameChangedListener implements UserChangeNicknameListener {
    @Autowired
    ApiUtil apiUtil;

    @Override
    public void onUserChangeNickname(UserChangeNicknameEvent userChangeNicknameEvent) {
        apiUtil.getChannelForLogging(userChangeNicknameEvent.getServer()).sendMessage(nickNameChangedConsumer(userChangeNicknameEvent));
    }

    private EmbedBuilder nickNameChangedConsumer(UserChangeNicknameEvent messageDeleteEvent) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(messageDeleteEvent.getUser());

        if (messageDeleteEvent.getOldNickname().isPresent()) {
            embed.addField("Old", messageDeleteEvent.getOldNickname().get());
        } else {
            embed.addField("Old", messageDeleteEvent.getUser().getName());
        }

        if (messageDeleteEvent.getNewNickname().isPresent()) {
            embed.addField("New", messageDeleteEvent.getNewNickname().get());
        } else {
            embed.addField("New", messageDeleteEvent.getUser().getName());
        }

        embed.setTimestampToNow();

        embed.setColor(Color.PINK)
                .setDescription("**Nickname changed by** <@" + messageDeleteEvent.getUser().getIdAsString() + ">")
                .setFooter("Author: " + messageDeleteEvent.getUser().getIdAsString());
        return embed;
    }
}
