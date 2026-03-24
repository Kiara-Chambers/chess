package client;

import com.google.gson.Gson;
import model.*;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Map;

public class ServerFacade {
    String serverUrl;
    private HttpClient client = HttpClient.newHttpClient();
    Gson gson = new Gson();
    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
    }

    public void clearDatabase() throws Exception {
        var request = buildRequest("DELETE","/db",null);
        sendRequest(request);
    }

    //
    public AuthData register(String username,String password, String email) throws Exception {
        var request = buildRequest("POST", "/user",new UserData(username,password, email));
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public AuthData login(String username,String password) throws Exception {
        var request = buildRequest("POST", "/session",new UserData(username,password,null));
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }
    public void logout(String authToken) throws Exception {
        HttpRequest request = buildRequestWithAuth("DELETE", "/session", null, authToken);
        HttpResponse<String> response = sendRequest(request);
        handleResponse(response, null);
    }


    public List<GameData> listGames(String authToken) throws Exception {
        HttpRequest request = buildRequestWithAuth("GET", "/game", null, authToken);
        HttpResponse<String> response = sendRequest(request);
        return gson.fromJson(gson.fromJson(response.body(), Map.class).get("games").toString(), List.class);
    }

    public GameData createGame(String gameName,String authToken) throws Exception {
        GameData game = new GameData(0, null, null, gameName, null);
        HttpRequest request = buildRequestWithAuth("POST", "/game", game, authToken);
        HttpResponse<String> response = sendRequest(request);
        var map = new Gson().fromJson(response.body(), Map.class);
        int gameID = ((Double) map.get("gameID")).intValue();
        return new GameData(gameID, null, null, gameName, null);
    }

    public void joinGame(int gameID, String playerColor, String authToken) throws Exception {
        Map<String, Object> body = Map.of(
                "gameID", gameID,
                "playerColor", playerColor
        );

        HttpRequest request = buildRequestWithAuth("PUT", "/game", body, authToken);
        HttpResponse<String> response = sendRequest(request);
        handleResponse(response, null);
    }

    //helpers
    private HttpRequest buildRequestWithAuth(String method, String path, Object body,String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if(authToken!=null){
            request.setHeader("Authorization",authToken);
        }
        return request.build();
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }

        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new Exception("Server request failed"+ ex.getMessage(),ex);
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw new Exception("Server responded with error: " + body);
            }

            throw new Exception("other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
