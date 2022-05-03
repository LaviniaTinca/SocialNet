package team.proiectextins.service;


import team.proiectextins.domain.*;
import team.proiectextins.domain.validators.exceptions.ValidationException;
import team.proiectextins.repository.Repository;
import team.proiectextins.utils.events.FriendshipChangeEvent;
import team.proiectextins.utils.events.MessageChangeEvent;
import team.proiectextins.utils.events.MessageEventType;
import team.proiectextins.utils.observer.Observable;
import team.proiectextins.utils.observer.Observer;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessageService implements Observable<MessageChangeEvent> {
    public final Repository<Long, User> repoUser;
    public final Repository<Long, Message> repoMessage;
    public final Repository<Tuple<Long, Long>, Friendship> repoFriendship;

    /**
     * @param repoUser      User Repository
     * @param repoMessage   Message Repository
     */
    public MessageService(Repository<Long, User> repoUser,
                          Repository<Long, Message> repoMessage,
                          Repository<Tuple<Long, Long>, Friendship> repoFriendship) {
        this.repoUser = repoUser;
        this.repoMessage = repoMessage;
        this.repoFriendship = repoFriendship;
    }

    public void sendMessage(Long from, List<Long> to, String text) {
        if (to.contains(from)) {
            throw new ValidationException("Nu puteti sa va trimiteti singur mesaj");
        }

        if (text.length() == 0) {
            throw new ValidationException("Mesajul este gol!");
        }

        List<Long> friendsID = new ArrayList<>();
        repoFriendship.findAll().forEach(friendship -> {
            if (Objects.equals(friendship.getId().getLeft(), from)) {
                friendsID.add(friendship.getId().getRight());
            } else if (Objects.equals(friendship.getId().getRight(), from)) {
                friendsID.add(friendship.getId().getLeft());
            }
        });

        for (Long recipient : to) {
            if (!friendsID.contains(recipient)) {
                throw new ValidationException("Nu puteti trimite mesaj unei persoane cu care nu sunteti prieteni");
            }

            Message msg = new Message(from, recipient, text, LocalDateTime.now());
            msg.setReply(0L);

            Message result = repoMessage.save(msg);

            if (result == null) {
                notifyObservers(new MessageChangeEvent(MessageEventType.ADD, msg));
            }
        }
    }

    public void sendReply(Long from, Long replyTo, String text) {
        Message msg = repoMessage.findOne(replyTo);

        Message savedMessage = new Message(from, msg.getFrom(), text, LocalDateTime.now());
        savedMessage.setReply(msg.getId());

        Message result = repoMessage.save(savedMessage);

        if (result == null) {
            notifyObservers(new MessageChangeEvent(MessageEventType.ADD, savedMessage));
        }
    }

    public List<MessageDTO> getChat(Long currentUserID, Long friendID) {
        List<Message> messages = repoMessage.findMatching(currentUserID, friendID);
        List<MessageDTO> result = new ArrayList<>();

        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        for (Message msg : messages) {
            MessageDTO msgDTO = new MessageDTO(repoUser.findOne(msg.getFrom()).getUsername(),
                    repoUser.findOne(msg.getTo()).getUsername(), msg.getText(), DATE_TIME_FORMATTER.format(msg.getTimestamp()));
            msgDTO.setId(msg.getId());

            Message msgReply = repoMessage.findOne(msg.getReply());

            if (msgReply != null) {
                msgDTO.setReply(msgReply.getText());
            }

            result.add(msgDTO);
        }

        Comparator<MessageDTO> compareByTimeStamp = Comparator.comparing(MessageDTO::getTimestamp);
        result.sort(compareByTimeStamp);

        return result;
    }


    /**
     * lista de observatori
     */
    private final List<Observer<MessageChangeEvent>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<MessageChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<MessageChangeEvent> e) {
        //observers.remove(e);
    }

    @Override
    public void notifyObservers(MessageChangeEvent t) {
        observers.forEach(x -> x.update(t));
    }
}

