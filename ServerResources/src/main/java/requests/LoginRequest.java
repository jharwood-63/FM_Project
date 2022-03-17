package requests;

/**
 * LoginRequest object holds the information that is needed to log in a user
 */

public class LoginRequest extends UserRequest {
    /**
     * Constructor receives attribute values that are read from the JSON object
     * @param username Unique username for user
     * @param password User's password
     */

    public LoginRequest(String username, String password) {
        super(username, password);
    }
}
