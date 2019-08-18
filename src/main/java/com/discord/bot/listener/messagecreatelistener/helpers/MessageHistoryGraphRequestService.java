package com.discord.bot.listener.messagecreatelistener.helpers;

import com.discord.bot.util.ApiUtil;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.entity.message.Message;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

@Component
public class MessageHistoryGraphRequestService {
    @Autowired
    ApiUtil apiUtil;

    int daysToConsider = 30;
    final long millisInDay = 24 * 60 * 60 * 1000;

    public void handleMessageHistoryGraphRequest(MessageCreateEvent messageCreateEvent, String prefix) {
        aggregateAndGraphDataForMessages(messageCreateEvent, prefix);
    }

    private void aggregateAndGraphDataForMessages(MessageCreateEvent messageCreateEvent, String prefix) {

        //set the number of days in the data set based on user input
        daysToConsider = setDaysBasedOnInput(messageCreateEvent, daysToConsider, prefix);

        //get the timestamp we should look for messages before
        final Instant valid = Instant.now().minusMillis(daysToConsider * millisInDay);

        System.out.println("Collecting all messages");
        Stream<Message> messageAggregate = getMessagesBeforeInstant(messageCreateEvent, valid);

        //Create hashmap for raw data
        HashMap<Long, HashMap<Long, Integer>> messagesForDay = new HashMap<>();

        //Generate raw data
        messageAggregate.forEach(message -> {
            createDataPointForMessage(messagesForDay, message);
        });

        //generate chart based on raw data
        JFreeChart lineChart = createChartImageFromData(messageCreateEvent, messagesForDay);

        //send message with chart
        messageCreateEvent.getChannel().sendMessage(new EmbedBuilder().setImage(lineChart.createBufferedImage(960, 540), "png"));
    }

    private JFreeChart createChartImageFromData(MessageCreateEvent messageCreateEvent, HashMap<Long, HashMap<Long, Integer>> messagesForDay) {
        TimeSeriesCollection xyDataset = new TimeSeriesCollection();
        messagesForDay.forEach((key, value) -> {
            try {
                User user = apiUtil.getApi().getUserById(key).get();
                TimeSeries seriesForUser = new TimeSeries(user.getDisplayName(messageCreateEvent.getServer().get()));
                //set default as 0 for days where user has no data
                for (long i = 1L; i <= daysToConsider; i++) {
                    if (!value.containsKey(i)) {
                        value.put(i, 0);
                    }
                }
                value.forEach((userKey, userValue) -> {
                    RegularTimePeriod timePeriod = RegularTimePeriod.createInstance(Day.class
                            , Date.from(Instant.now().minusMillis((daysToConsider - userKey) * millisInDay))
                            , TimeZone.getDefault()
                    );
                    seriesForUser.add(timePeriod, userValue);
                });
                xyDataset.addSeries(seriesForUser);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        JFreeChart lineChart = ChartFactory.createTimeSeriesChart(
                "Messages by User by Day (last " + daysToConsider + " days)",
                "Date of Messages Sent", "Number of Messages",
                xyDataset,
                true, false, false);
        lineChart.getXYPlot().getDomainAxis().setUpperBound(
                RegularTimePeriod.createInstance(Day.class
                        , Date.from(Instant.now()), TimeZone.getDefault()).getFirstMillisecond()
        );
        lineChart.getXYPlot().getDomainAxis().setLowerBound(
                RegularTimePeriod.createInstance(Day.class
                        , Date.from(Instant.now().minusMillis(daysToConsider * millisInDay))
                        , TimeZone.getDefault()).getLastMillisecond()
        );
        return lineChart;
    }

    private void createDataPointForMessage(HashMap<Long, HashMap<Long, Integer>> messagesForDay, Message message) {
        long timestamp = (message.getCreationTimestamp().toEpochMilli() - (Instant.now().toEpochMilli() - (millisInDay * daysToConsider))) / millisInDay;
        if (!messagesForDay.containsKey(message.getAuthor().getId())) {
            messagesForDay.put(message.getAuthor().getId(), new HashMap<>());
        }
        HashMap<Long, Integer> messageForUser = messagesForDay.get(message.getAuthor().getId());
        int messages = messageForUser.getOrDefault(timestamp, 0);
        messageForUser.put(timestamp, messages + 1);
    }

    private Stream<Message> getMessagesBeforeInstant(MessageCreateEvent messageCreateEvent, Instant valid) {
        Optional<Message> messageOptional = messageCreateEvent.getChannel().getMessagesAsStream()
                .filter(message -> message.getCreationTimestamp().isBefore(valid))
                .findFirst();
        return messageOptional.isPresent() ? messageCreateEvent.getChannel().getMessagesAfterAsStream(messageOptional.get()) : messageCreateEvent.getChannel().getMessagesAsStream();
    }

    private int setDaysBasedOnInput(MessageCreateEvent messageCreateEvent, int defaultVal, String prefix) {
        if (messageCreateEvent.getMessageContent().length() > prefix.length()) {
            String[] val = apiUtil.extractArguments(messageCreateEvent, prefix);
            if (val.length == 1) {
                try {
                    return Integer.parseInt(val[0]);
                } catch (NumberFormatException e) {
                    //do nothing, we simply take default for daysToConsider
                }
            }
        }
        return defaultVal;
    }
}
