package com.discord.bot.listener.messagecreatelistener.helpers;

import com.discord.bot.util.ApiUtil;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
public class SetColorRequestService  {
    @Autowired
    ApiUtil apiUtil;

    public final String prefix = "t!setcolor";

    public void handleSetColorRequest(MessageCreateEvent messageCreateEvent, String prefix) {
        System.out.println("set color initiated");
        String[] params = apiUtil.extractArguments(messageCreateEvent, prefix);
        if (params.length == 1) {
            System.out.println("correct number of arguments");
            setUserRoleBasedOnColor(messageCreateEvent, params[0]);
        }
    }

    private void setUserRoleBasedOnColor(MessageCreateEvent messageCreateEvent, String color) {
        try {
            Optional<User> userOptional = messageCreateEvent.getMessageAuthor().asUser();
            userOptional.ifPresent(user -> {
                Color userRoleColor = getColorBasedOnUserInput(color);
                messageCreateEvent.getServer().ifPresent(server -> {
                    checkForExistingRoleColor(user, userRoleColor, server);
                    if (!(user.getRoleColor(server).orElse(new Color(0x99aab5)).getRGB() == (userRoleColor.getRGB()))) {
                        createNewRoleForColor(user, userRoleColor, server);
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final Color[] COLOR_VAL = {Color.BLACK, Color.blue, Color.cyan, Color.MAGENTA, Color.PINK, Color.CYAN, Color.RED, Color.YELLOW, Color.ORANGE, Color.GRAY, Color.GREEN};
    final String[] COLOR_NAME = {"black", "blue", "cyan", "magenta", "pink", "cyan", "red", "yellow", "orange", "gray", "green"};

    private Color getColorBasedOnUserInput(String color) {
        if (color.startsWith("0x")) {
            return new Color(Integer.parseUnsignedInt(color.substring(2), 16));
        } else if (color.startsWith("#")) {
            return new Color(Integer.parseUnsignedInt(color.substring(1), 16));
        } else if (Arrays.stream(COLOR_NAME).anyMatch(a -> a.equalsIgnoreCase(color))) {
            for(int i = 0; i < COLOR_NAME.length; i++) {
                if (COLOR_NAME[i].equalsIgnoreCase(color)) {
                    return COLOR_VAL[i];
                }
            }
        }
        return new Color(Integer.parseUnsignedInt(color, 16));
    }

    private void createNewRoleForColor(User user, Color userRoleColor, Server server) {
        try {
            user.getRoles(server).forEach(role -> {
                try {
                    if (role.getName().startsWith("token-color")) {
                        if (role.getUsers().size() <= 1) {
                            role.delete().get();
                            System.out.println("deleting unused role");
                        }
                        role.removeUser(user).get();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            CompletableFuture<Role> roleICompletableFuture = server.createRoleBuilder()
                    .setColor(userRoleColor)
                    .setAuditLogReason("Creating Color Role for user")
                    .setDisplaySeparately(false)
                    .setMentionable(false)
                    .setName("token-color-" + userRoleColor.getRGB())
                    .setPermissions(new PermissionsBuilder()
                            .setAllUnset()
                            .build())
                    .create();
            server.addRoleToUser(user, roleICompletableFuture.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkForExistingRoleColor(User user, Color userRoleColor, Server server) {
        server.getRoles().forEach(role -> {
            if (role.getName().startsWith("token-color")) {
                role.getColor().ifPresent(roleColor -> {

                    try {
                        if (roleColor.getRGB() == (userRoleColor.getRGB())) {
                            System.out.println("checking permissions");
                            if (checkPrivWontBeEscalated(user, server, role)) {
                                System.out.println("permission check successful, adding role");
                                server.addRoleToUser(user, role).get();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private boolean checkPrivWontBeEscalated(User user, Server server, Role role) throws InterruptedException, java.util.concurrent.ExecutionException {
        int largestUser = user.getRoles(server).stream().mapToInt((a) -> a.getPermissions().getAllowedBitmask()).max().orElse(-1);
        int largestColor = role.getPermissions().getAllowedBitmask();
        return ((largestUser | largestColor) == largestUser);
    }
}
