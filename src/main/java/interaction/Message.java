package interaction;

import java.util.Objects;

public class Message {
    private static int instanceCount = 0;

    private final int _id;
    private final String _text;

    public Message(String text) {
        this._id = instanceCount++;
        this._text = Objects.requireNonNull(text);
    }

    public int getId() {
        return _id;
    }

    public String getText() {
        return _text;
    }

    public static int getInstanceCount() {
        return instanceCount;
    }

    public static void resetCounter() {
        instanceCount = 0;
    }
}
