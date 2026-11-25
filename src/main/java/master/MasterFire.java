package master;

public class MasterFire extends Master{
    private static MasterFire instance;

    /** Construit l'unique Master Fire. */
    private MasterFire() {
        super();
    }

    /** Retourne l'instance unique du Master Fire. */
    public static MasterFire getInstance() {
        if (instance == null) {
            instance = new MasterFire();
        }

        return instance;
    }

    /** Retourne le nom de l'équipe Fire. */
    @Override
    public String getGroupName() { return "Fire"; }

}
