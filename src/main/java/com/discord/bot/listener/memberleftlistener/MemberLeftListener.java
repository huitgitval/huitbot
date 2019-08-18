package com.discord.bot.listener.memberleftlistener;

import com.discord.bot.util.ApiUtil;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.server.ServerLeaveEvent;
import org.javacord.api.event.server.member.ServerMemberLeaveEvent;
import org.javacord.api.event.user.UserChangeNicknameEvent;
import org.javacord.api.listener.server.ServerLeaveListener;
import org.javacord.api.listener.server.member.ServerMemberLeaveListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class MemberLeftListener implements ServerMemberLeaveListener {
    @Autowired
    ApiUtil apiUtil;

    @Override
    public void onServerMemberLeave(ServerMemberLeaveEvent serverMemberLeaveEvent) {
        apiUtil.getChannelForLogging(serverMemberLeaveEvent.getServer()).sendMessage(userLeftHandler(serverMemberLeaveEvent));
    }

    private EmbedBuilder userLeftHandler(ServerMemberLeaveEvent messageDeleteEvent) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(messageDeleteEvent.getUser());
        embed.setTimestampToNow();

        embed.setColor(Color.BLACK)
                .setDescription("**Member left** <@" + messageDeleteEvent.getUser().getIdAsString() + ">")
                .setFooter("Author: " + messageDeleteEvent.getUser().getIdAsString());
        return embed;
    }
}
