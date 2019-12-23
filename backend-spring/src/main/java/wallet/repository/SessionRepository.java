package wallet.repository;

import org.springframework.stereotype.Component;
import wallet.model.Session;
import wallet.model.User;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */
@Component
public class SessionRepository {

    private final Map<String, Session> map = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void deleteById(String id) {
        try {
            lock.writeLock().lock();
            id = id.toLowerCase();
            map.remove(id);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void save(Session session) {
        try {
            lock.writeLock().lock();
            session.setId(session.getId().toLowerCase());
            map.put(session.getId(), session);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<Session> findById(String id) {
        try {
            lock.readLock().lock();
            id = id.toLowerCase();
            return map.containsKey(id) ? Optional.of(map.get(id)) : Optional.empty();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void deleteAll(List<Session> list) {
        try {
            lock.writeLock().lock();
            list.stream().forEach(x -> map.remove(x.getId()));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Session> findExpired(long l) {
        try {
            lock.readLock().lock();
            List<Session> list = new ArrayList<>();
            for (String id : map.keySet()) {
                if (map.get(id).getLastModified() < l) {
                    list.add(map.get(id));
                }
            }
            return list;
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Session> findByUsername(String username) {
        try {
            lock.readLock().lock();
            List<Session> list = new ArrayList<>();
            for (String id : map.keySet()) {
                User user = map.get(id).getUser();
                if ((user != null) && (user.getUsername().equals(username))) {
                    list.add(map.get(id));
                }
            }
            return list;
        } finally {
            lock.readLock().unlock();
        }
    }
}
