package master;

public class MasterWater extends Master{
    private static MasterWater instance;

    /** Construit l'unique Master Water. */
    private MasterWater() {
        super();
    }

    /** Retourne l'instance unique du Master Water. */
    public static MasterWater getInstance() {
        if (instance == null) {
            instance = new MasterWater();
        }

        return instance;
    }

    /** Retourne le nom de l'équipe Water. */
    @Override
    public String getGroupName() { return "Water"; }
}
