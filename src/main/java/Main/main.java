package Main;

import Control.WorkManager;
import View.Screen;

public class  main {
    public static void main(String[] args) {

        WorkManager control = new WorkManager();
        Screen view = new Screen();

        view.start();
    }

    public static class GenreManagerApp {
    }
}
