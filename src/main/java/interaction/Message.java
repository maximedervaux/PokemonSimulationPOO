package interaction;

import java.util.Objects;

public class Message {
    private static int instanceCount = 0;

    private final int _id;
    private final String _text;

    /** Crée un message identifié de façon unique pour cette partie. */
    public Message(String text) {
        this._id = instanceCount++;
        this._text = Objects.requireNonNull(text);
    }

    /** Retourne l'identifiant unique du message. */
    public int getId() {
        return _id;
    }

    /** Retourne le texte du message. */
    public String getText() {
        return _text;
    }

    /** Retourne le nombre de messages créés depuis la dernière remise à zéro. */
    public static int getInstanceCount() {
        return instanceCount;
    }

    /** Réinitialise la numérotation avant la création d'un nouvel univers de messages. */
    static void resetCounter() {
        instanceCount = 0;
    }

    /** L'identité repose sur l'id : l'ordre des HashSet, donc les runs, ne dépend que de la graine. */
    @Override
    public boolean equals(Object other) {
        return other instanceof Message message && this._id == message._id;
    }

    /** Retourne le hachage cohérent avec l'égalité fondée sur l'identifiant. */
    @Override
    public int hashCode() {
        return Integer.hashCode(_id);
    }
}
