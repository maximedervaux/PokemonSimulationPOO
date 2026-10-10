package pokemon;

import environment.Occupant;
import interaction.Message;

import java.util.List;

public abstract class Pokemon extends Occupant {
    protected List<Message> messages;
}