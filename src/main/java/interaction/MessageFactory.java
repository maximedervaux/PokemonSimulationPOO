package interaction;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class MessageFactory {
    private MessageFactory() { }

    public static List<Message> load(String resource, int count, utils.RandomGenerator random) throws IOException {
        InputStream in = MessageFactory.class.getClassLoader().getResourceAsStream(resource);
        if (in == null) {
            throw new FileNotFoundException("Ressource introuvable : " + resource);
        }

        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            lines = new ArrayList<>(reader.lines().map(String::trim).filter(line -> !line.isEmpty()).distinct().toList());
        }

        if (count > lines.size()) {
            throw new IllegalArgumentException(
                    "Demandé " + count + " messages, le fichier n'en contient que " + lines.size() + " distincts");
        }

        Message.resetCounter();
        random.shuffle(lines);

        List<Message> universe = new ArrayList<>();
        for (String line : lines.subList(0, count)) {
            universe.add(new Message(line));
        }
        return universe;
    }
}
