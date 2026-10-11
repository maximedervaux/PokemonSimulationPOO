package master;

public class MasterPoison extends Master{
    private static MasterPoison instance;

    private MasterPoison() {
        super();
    }

    public static MasterPoison getInstance() {
        if (instance == null) {
            instance = new MasterPoison();
        }

        return instance;
    }

}
