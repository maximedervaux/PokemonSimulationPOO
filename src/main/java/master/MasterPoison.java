package master;

public class MasterPoison extends Master{
    private static MasterPoison instance;

    /** Construit l'unique Master Poison. */
    private MasterPoison() {
        super();
    }

    /** Retourne l'instance unique du Master Poison. */
    public static MasterPoison getInstance() {
        if (instance == null) {
            instance = new MasterPoison();
        }

        return instance;
    }

    /** Retourne le nom de l'équipe Poison. */
    @Override
    public String getGroupName() { return "Poison"; }

}
