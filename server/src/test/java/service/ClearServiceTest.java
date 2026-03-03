package service;

import dataaccess.*;
import model.UserData;

public class ClearServiceTest {
    UserDAO userDAO = new MemoryUserDAO();
    AuthDAO authDAO = new MemoryAuthDAO();
    GameDAO gameDAO = new MemoryGameDAO();

    ClearService service = new ClearService(userDAO,authDAO,gameDAO);

    service.clear();




}
