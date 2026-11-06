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

    static void resetCounter() {
        instanceCount = 0;
    }

    /** L'identité repose sur l'id : l'ordre des HashSet, donc les runs, ne dépend que de la graine. */
    @Override
    public boolean equals(Object other) {
        return other instanceof Message message && this._id == message._id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(_id);
    }
}
