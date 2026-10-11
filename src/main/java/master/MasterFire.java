package master;

public class MasterFire extends Master{
    private static MasterFire instance;

    private MasterFire() {
        super();
    }

    public static MasterFire getInstance() {
        if (instance == null) {
            instance = new MasterFire();
        }

        return instance;
    }

}
