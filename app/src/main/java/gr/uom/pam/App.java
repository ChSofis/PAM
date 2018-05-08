package gr.uom.pam;

import android.app.Application;

public class App extends Application {
    public static final String NAMESPACE = "gr.uom.pam";

    static final String INVALID_CHARACTERS = "\"#@,;:<>*^|?\\/";

    public static String CheckInvalid(String string) {
        StringBuilder reply = new StringBuilder();
        for (int idx = 0; idx < INVALID_CHARACTERS.length(); idx++) {
            String chr = INVALID_CHARACTERS.substring(idx, idx + 1);
            if (string.contains(chr))
                reply.append(chr).append(" ");
        }
        return reply.length() > 0 ? reply.deleteCharAt(reply.length() - 1).toString() : null;
    }

}
