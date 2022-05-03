package team.proiectextins.service;


import team.proiectextins.domain.*;
import team.proiectextins.domain.validators.exceptions.FriendshipException;
import team.proiectextins.repository.Repository;
import team.proiectextins.utils.events.FriendRequestChangeEvent;
import team.proiectextins.utils.events.FriendRequestEventType;
import team.proiectextins.utils.events.FriendshipChangeEvent;
import team.proiectextins.utils.events.FriendshipEventType;
import team.proiectextins.utils.observer.Observable;
import team.proiectextins.utils.observer.Observer;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.StreamSupport;

public class FriendRequestService implements Observable<FriendRequestChangeEvent> {
    public final Repository<Long, User> repoUser;
    public final Repository<Tuple<Long, Long>, Friendship> repoFriendship;
    public final Repository<Tuple<Long, Long>, FriendRequest> repoFriendRequest;

    /**
     * constructor
     *
     * @param repoUser          Repository for user
     * @param repoFriendship    Repository for friendship
     * @param repoFriendRequest Repository for friendrequest
     */
    public FriendRequestService(Repository<Long, User> repoUser,
                                Repository<Tuple<Long, Long>, Friendship> repoFriendship,
                                Repository<Tuple<Long, Long>, FriendRequest> repoFriendRequest) {
        this.repoUser = repoUser;
        this.repoFriendship = repoFriendship;
        this.repoFriendRequest = repoFriendRequest;
    }

    public int friendRequestsCount(Long userID) {
        return ((Collection<?>) getFriendRequests(userID)).size();
    }

    /**
     * obtine cererile de prietenie primite
     *
     * @param id Long
     * @return iterabil cu cererile primite
     */
    public Iterable<FriendRequest> getFriendRequests(Long id) {
        Map<Tuple<Long, Long>, FriendRequest> requests = new HashMap<>();
        StreamSupport.stream(repoFriendRequest.findAll().spliterator(), false)
                .forEach(x -> {
                    if (x.getId().getRight().equals(id) && x.getStatus().equals(Status.PENDING))
                        requests.put(x.getId(), x);
                });

        return requests.values();
    }

    /**
     * obtine cererile de prietenie primite sub format FriendRequestDTO
     *
     * @param id Long
     * @return iterabil cu cererile primite- FriendRequestDTO
     */
    public Iterable<FriendRequestDTO> getFriendRequests1(Long id) {
        Map<Long, FriendRequestDTO> requests = new HashMap<>();
        User currentUser = repoUser.findOne(id);
        StreamSupport.stream(repoFriendRequest.findAll().spliterator(), false)
                .forEach(x -> {
                    if (x.getId().getRight().equals(id) && x.getStatus().equals(Status.PENDING)) {
                        User user = getUserById(x.getId().getLeft());

                        FriendRequestDTO y = new FriendRequestDTO(
                                user.getFirstName() + " " + user.getLastName(),
                               // user.getId().toString(),
                                currentUser.getFirstName() + " " + currentUser.getLastName(),

                                x.getStatus().toString(),
                                x.getDate());
                        Tuple<Long, Long> friendRequestId = new Tuple<>(x.getId().getLeft(), id);
                        y.setId(friendRequestId);
                        requests.put(x.getId().getLeft(), y);
                    }
                });
        return requests.values();
    }

    public User getUserById(Long id) {
        return repoUser.findOne(id);
    }

    /**
     * obtine cererile de prietenie trimise de utilizatorul logat
     *
     * @param id Long
     * @return iterabil cu cererile trimise
     */
    public Iterable<FriendRequest> getSentFriendRequests(Long id) {
        Map<Tuple<Long, Long>, FriendRequest> requests = new HashMap<>();
        StreamSupport.stream(repoFriendRequest.findAll().spliterator(), false)
                .forEach(x -> {
                    if (x.getId().getLeft().equals(id) && x.getStatus().equals(Status.PENDING))
                        requests.put(x.getId(), x);
                });

        return requests.values();
    }

    /**
     * obtine cererile de prietenie trimise
     * @param id Long
     * @return iterabil FriendRequestDTO cu cererile trimise
     */
    public Iterable<FriendRequestDTO> getSentFriendRequests1(Long id) {
        Map<Long, FriendRequestDTO> requests = new HashMap<>();
        User currentUser = repoUser.findOne(id);
        StreamSupport.stream(repoFriendRequest.findAll().spliterator(), false)
                .forEach(x -> {
                    if (x.getId().getLeft().equals(id) && x.getStatus().equals(Status.PENDING)) {
                        User user = getUserById(x.getId().getRight());

                        FriendRequestDTO y = new FriendRequestDTO(
                                currentUser.getFirstName() + " " + currentUser.getLastName(),
                                user.getFirstName() + " " + user.getLastName(),
                                x.getStatus().toString(),
                                x.getDate());
                        Tuple<Long, Long> friendRequestId = new Tuple<>(id, x.getId().getRight());
                        y.setId(friendRequestId);
                        requests.put(x.getId().getRight(), y);
                    }
                });
        return requests.values();
    }

    /**
     * trimite o cerere de prietenie
     *
     * @param from Long id-ul userului care trimite cererea de prietenie
     * @param to   Long id-ul userului care primeste cererea de prietenie
     */
    public void sendFriendRequest(Long from, Long to) {
        // validate if users exist
        validateSendFriendRequest(from, to);

        FriendRequest existingRequest = repoFriendRequest.findOne(new Tuple<>(from, to));
        if (existingRequest != null) {
            existingRequest.setStatus(Status.PENDING);
            existingRequest.setDate(LocalDate.now());
            repoFriendRequest.update(existingRequest);
        } else {
            // if the request does not exist, create one
            FriendRequest friendRequest = new FriendRequest();
            friendRequest.setId(new Tuple<>(from, to));
            friendRequest.setStatus(Status.PENDING);
            friendRequest.setDate(LocalDate.now());

            repoFriendRequest.save(friendRequest);
        }
    }

    /**
     * sterge o cerere trimisa
     *
     * @param from Long id-ul celui care cere
     * @param to   Long ID catre
     */
    public void deleteFriendRequest(Long from, Long to) {
        if (repoUser.findOne(to) == null) {
            throw new FriendshipException(("Id-ul prietenului nu exista!"));
        }

        FriendRequest removed = repoFriendRequest.delete(new Tuple<>(from, to));

        if (removed == null) {
            throw new FriendshipException("Nu a fost trimisa cerere de prietenie!");
        } else {
            notifyObservers(new FriendRequestChangeEvent(FriendRequestEventType.DELETE, removed));
        }
    }

    /**
     * valideaza  trimiterea unei cereri
     *
     * @param from Long id-ul celui care cere
     * @param to   Long ID catre
     */
    public void validateSendFriendRequest(Long from, Long to) {
        if (repoUser.findOne(from) == null || repoUser.findOne(to) == null) {
            throw new FriendshipException("Unul dintre utilizatori nu exista!");
        }

        if (repoFriendship.findOne(new Tuple<>(from, to)) != null ||
                repoFriendship.findOne(new Tuple<>(to, from)) != null) {
            throw new FriendshipException("Sunteti deja prieteni!");
        }

        FriendRequest one = repoFriendRequest.findOne(new Tuple<>(from, to));
        FriendRequest two = repoFriendRequest.findOne(new Tuple<>(to, from));
        if (one != null && one.getStatus() == Status.PENDING ||
                two != null && two.getStatus() == Status.PENDING) {
            throw new FriendshipException("Exista deja o cerere!");
        }
    }

    /**
     * valideaza acceptarea/respingerea unei cereri de prietenie
     */
    public void validateApproveRejectFriendRequest(Tuple<Long, Long> idFriendShip) {
        if (repoUser.findOne(idFriendShip.getLeft()) == null) {
            throw new FriendshipException(("Id-ul dat nu exista!"));
        }

        if (repoFriendRequest.findOne(idFriendShip) == null) {
            throw new FriendshipException(("Cererea nu exista!"));
        }

        if (repoFriendRequest.findOne(idFriendShip) != null &&
                repoFriendRequest.findOne(idFriendShip).getStatus() != Status.PENDING) {
            throw new FriendshipException(("Nu se poate, cererea nu este 'Pending'!"));
        }
    }

    /**
     * accepta o cerere de prietenie
     *
     * @param from Long id-ul celui care cere
     * @param to   Long ID catre
     */
    public void manageFriendRequest(Long from, Long to, Status status) {
        Tuple<Long, Long> idFriendRequest = new Tuple<>(from, to);

        // check request is valid
        validateApproveRejectFriendRequest(idFriendRequest);

        // accept the friend request
        FriendRequest fRequest = repoFriendRequest.findOne(idFriendRequest);
        fRequest.setStatus(status);
        FriendRequest f = repoFriendRequest.update(fRequest);

        if (status == Status.APPROVED) {
            // create a friendship
            Friendship fShip = new Friendship();
            fShip.setId(idFriendRequest);
            fShip.setFriendshipDate(LocalDate.now());
            repoFriendship.save(fShip);
            if (f == null) {
                notifyObservers(new FriendRequestChangeEvent(FriendRequestEventType.APPROVED, f));
            }
        } else {
            if (f == null) {
                notifyObservers(new FriendRequestChangeEvent(FriendRequestEventType.REJECTED, f));
            }
        }
    }

    private final List<Observer<FriendRequestChangeEvent>> observers = new ArrayList<>();


    @Override
    public void addObserver(Observer<FriendRequestChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<FriendRequestChangeEvent> e) {
        //observers.remove(e);
    }

    @Override
    public void notifyObservers(FriendRequestChangeEvent t) {
        observers.forEach(x -> x.update(t));
    }
}


