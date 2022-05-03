package team.proiectextins.utils.observer;


import team.proiectextins.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}
