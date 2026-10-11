package master;

public class MasterGhost extends Master{
    private static MasterGhost instance;

    private MasterGhost() {
        super();
    }

    public static MasterGhost getInstance() {
        if (instance == null) {
            instance = new MasterGhost();
        }

        return instance;
    }
}
