package service;

import dataaccess.*;

public class ClearService
{
    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;


    public ClearService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }
}
