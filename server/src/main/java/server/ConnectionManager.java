package server;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        connections.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void remove(int gameID, Session session) {
        var set = connections.get(gameID);
        if (set != null) {
            set.remove(session);
        }
    }

    //added gameid to pass the Multiple concurrent games test that was so annoying
    public void broadcast(int gameID, Session excludeSession, Notification notification) throws IOException {
        var sessions = connections.get(gameID);
        if (sessions == null) return;

        String msg = notification.toString();

        for (Session c : sessions) {
            if (c.isOpen() && (excludeSession == null || !c.equals(excludeSession))) {
                c.getRemote().sendString(msg);
            }
        }
    }
    public Set<Session> getGameSessions(int gameID) {
        return connections.getOrDefault(gameID, ConcurrentHashMap.newKeySet());
    }
}