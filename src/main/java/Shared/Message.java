package Shared;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable {
    @JsonProperty
    private int Type = -1;
    @JsonProperty
    private String Senders = "";
    @JsonProperty
    private String Contents = "";
    @JsonProperty
    private String username = "";
    @JsonProperty
    private String password = "";
    @JsonProperty
    private boolean loginResult = false;
    @JsonProperty
    private String fileName = "";
    @JsonProperty
    private int fileSize = 0;
    @JsonProperty
    private List<String> listOfFiles = new ArrayList<>();
    public int type; // e.g., 0 = login, 1 = chat message, 2 = file upload, etc.
    public String sender;
    public String content;    // chat text, filename, password, etc.

    public Message() {

    }

}