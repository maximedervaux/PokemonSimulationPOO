package master;

public class MasterWater extends Master{
    private static MasterWater instance;

    private MasterWater() {
        super();
    }

    public static MasterWater getInstance() {
        if (instance == null) {
            instance = new MasterWater();
        }

        return instance;
    }
}
