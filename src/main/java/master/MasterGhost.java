package master;

public class MasterGhost extends Master{
    private static MasterGhost instance;

    /** Construit l'unique Master Ghost. */
    private MasterGhost() {
        super();
    }

    /** Retourne l'instance unique du Master Ghost. */
    public static MasterGhost getInstance() {
        if (instance == null) {
            instance = new MasterGhost();
        }

        return instance;
    }

    /** Retourne le nom de l'équipe Ghost. */
    @Override
    public String getGroupName() { return "Ghost"; }
}
