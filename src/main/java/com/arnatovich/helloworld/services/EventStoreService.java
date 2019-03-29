package com.arnatovich.helloworld.services;

import com.arnatovich.helloworld.valueobject.StatusEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static com.arnatovich.helloworld.services.EventStore.TIME_OUT_GAP;

@Service
@Slf4j
public class EventStoreService {

    private EventStore store = new EventStore();

    public void addEvent(StatusEntity event){
        log.warn("entity " + event + " is added!");
        store.addEvent(event);
    }

    public ModelObject getRoomStatus() {
        StatusEntity lastEvent = store.getLastEvent();

        ModelObject modelObject = new ModelObject();
        String roomStatus = extractCurrentStatus(lastEvent);
        modelObject.setRoomStatus(roomStatus);

        List<LogObject> logs = store.getLogs();
        String lastActivity = extractLastActivity(logs);
        modelObject.setLastActivity(lastActivity);

        return modelObject;
    }

    private String extractLastActivity(List<LogObject> logs) {
        if (logs.isEmpty()){
            return "Today room wasn't busy! Miracle!";
        }

        LogObject log = logs.get(logs.size() - 1);

        return "Previous time room was busy since " + log.getBusySince() + " up to " + log.getBusyUpTo();
    }

    private String extractCurrentStatus(StatusEntity lastEvent) {
        if (lastEvent == null) {
          return "Room is free!";
        }
        Duration duration = Duration.between(lastEvent.getEventTime(), LocalDateTime.now());

        long pastSeconds = duration.getSeconds();
        if (TIME_OUT_GAP - pastSeconds < 0) {
            return "Room is busy!";
        }

        return "Room is free!";
    }
}
