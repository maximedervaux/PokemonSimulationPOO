package interaction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class MessageFactory {
    private MessageFactory() { }

    public static List<Message> load(Path file, int count, utils.RandomGenerator random) throws IOException {
        List<String> lines = new ArrayList<>(Files.readAllLines(file));
        lines.removeIf(String::isBlank);

        if (count > lines.size()) {
            throw new IllegalArgumentException(
                    "Demandé " + count + " messages, le fichier n'en contient que " + lines.size());
        }

        Message.resetCounter();
        random.shuffle(lines);

        List<Message> universe = new ArrayList<>();
        for (String line : lines.subList(0, count)) {
            universe.add(new Message(line.trim()));
        }
        return universe;
    }
}
