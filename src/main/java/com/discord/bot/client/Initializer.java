package com.discord.bot.client;

import com.discord.bot.listener.memberjoinedlistener.MemberJoinedListener;
import com.discord.bot.listener.memberleftlistener.MemberLeftListener;
import com.discord.bot.listener.messagecreatelistener.HUITMessageCreateListener;
import com.discord.bot.listener.messagedeletelistener.HUITMessageDeleteListener;
import com.discord.bot.listener.messageeditlistener.HUITMessageEditListener;
import com.discord.bot.listener.nicknamechangedlistener.NickNameChangedListener;
import com.discord.bot.listener.reactionaddlistener.ReactionRoleTriggerListener;
import com.discord.bot.util.ApiUtil;
import org.javacord.api.DiscordApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Component
public class Initializer {

    @Autowired
    ApiUtil apiUtil;

    @Autowired
    HUITMessageCreateListener huitMessageCreateListener;

    @Autowired
    ReactionRoleTriggerListener reactionRoleTriggerListener;

    @Autowired
    HUITMessageDeleteListener HUITMessageDeleteListener;

    @Autowired
    HUITMessageEditListener huitMessageEditListener;

    @Autowired
    NickNameChangedListener nickNameChangedListener;

    @Autowired
    MemberLeftListener memberLeftListener;

    @Autowired
    MemberJoinedListener memberJoinedListener;

    @PostConstruct
    public void initialize() {
        System.out.println("test");
        DiscordApi api = apiUtil.getApi();
        doReaction(api);
    }

    Set<String> servers = new HashSet<>();

    public void doReaction(DiscordApi api) {
        System.out.println("Collecting api...");
        System.out.println("adding messages listeners...");
        addEventTriggers(api);
    }

    public void addEventTriggers(DiscordApi api) {
        api.addMessageCreateListener(huitMessageCreateListener);
        api.addMessageDeleteListener(HUITMessageDeleteListener);
        api.addReactionAddListener(reactionRoleTriggerListener);
        api.addMessageEditListener(huitMessageEditListener);
        api.addUserChangeNicknameListener(nickNameChangedListener);
        api.addServerMemberLeaveListener(memberLeftListener);
        api.addServerMemberJoinListener(memberJoinedListener);
    }

}
