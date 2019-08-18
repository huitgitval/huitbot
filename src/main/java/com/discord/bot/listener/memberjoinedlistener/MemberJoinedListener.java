package com.discord.bot.listener.memberjoinedlistener;

import com.discord.bot.listener.messagecreatelistener.helpers.DefaultRoleService;
import com.discord.bot.util.ApiUtil;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.javacord.api.event.server.member.ServerMemberLeaveEvent;
import org.javacord.api.listener.server.member.ServerMemberJoinListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class MemberJoinedListener implements ServerMemberJoinListener {
    @Autowired
    ApiUtil apiUtil;

    @Autowired
    DefaultRoleService defaultRoleService;

    @Override
    public void onServerMemberJoin(ServerMemberJoinEvent serverMemberJoinEvent) {
        apiUtil.getChannelForLogging(serverMemberJoinEvent.getServer()).sendMessage(userLeftHandler(serverMemberJoinEvent));
        for(Role role: defaultRoleService.rolesForNewbies) {
            serverMemberJoinEvent.getUser().addRole(role);
        }
    }


    private EmbedBuilder userLeftHandler(ServerMemberJoinEvent messageDeleteEvent) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(messageDeleteEvent.getUser());
        embed.setTimestampToNow();
        embed.setColor(Color.GREEN)
                .setDescription("**Member joined** <@" + messageDeleteEvent.getUser().getIdAsString() + ">")
                .setFooter("Author: " + messageDeleteEvent.getUser().getIdAsString());
        return embed;
    }
}
