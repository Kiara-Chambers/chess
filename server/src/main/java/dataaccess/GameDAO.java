package dataaccess;

import model.UserData;

public interface GameDAO {
    void clear();

    void createGame(UserData u);

    void getGame(UserData u);

    void listGames(UserData u);

    void updateGame(UserData u);
}
