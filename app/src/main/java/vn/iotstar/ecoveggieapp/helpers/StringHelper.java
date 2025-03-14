package vn.iotstar.ecoveggieapp.helpers;

public class StringHelper {
    public static final String SERVER_IP = "192.168.1.47";
    // Set Regular Expression Pattern for Email:
    public static boolean regexEmailValidationPattern(String email) {
        // Set Pattern:
        String regex = "^[a-zA-Z0-9]+(?:[._-][a-zA-Z0-9]+)*@[a-zA-Z0-9]+(?:[.-][a-zA-Z0-9]+)*\\.[a-zA-Z]{2,}$";

        if (email.matches(regex)) {
            return true;
        }
        return false;
    }
// End Of Set Regular Expression Pattern for Email.

}
