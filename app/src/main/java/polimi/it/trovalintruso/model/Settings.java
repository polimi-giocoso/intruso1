package polimi.it.trovalintruso.model;

/**
 * Created by poool on 09/02/15.
 */
public class Settings {

    public static enum ObjectsForPage { Four, Five, Six }

    private ObjectsForPage numOfObjects;
    private int numOfScreens;
    private Boolean timeLimitEnabled;
    private Boolean randomCategory;

    public ObjectsForPage getNumOfObjects() {
        return numOfObjects;
    }

    public void setNumOfObjects(ObjectsForPage numOfObjects) {
        this.numOfObjects = numOfObjects;
    }

    public Boolean getTimeLimitEnabled() {
        return timeLimitEnabled;
    }

    public void setTimeLimitEnabled(Boolean timeLimit) {
        this.timeLimitEnabled = timeLimit;
    }

    public int getNumOfScreens() {
        return numOfScreens;
    }

    public void setNumOfScreens(int numOfScreens) {
        this.numOfScreens = numOfScreens;
    }

    public Boolean getRandomCategory() {
        return randomCategory;
    }

    public void setRandomCategory(Boolean randomCategory) {
        this.randomCategory = randomCategory;
    }


}