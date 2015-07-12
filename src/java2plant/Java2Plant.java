package java2plant;

import java.io.File;
import java2plant.control.Controller;
import java2plant.control.ToCtrl;
import java2plant.model.ClassList;

/**
 * 
 * @author arthur
 */
public class Java2Plant {

    // TODO: clean up
    private static File fInputDir;

    private static File fOutputDir;

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {

        if (args.length < 2) {
            return;
        }
        fInputDir = new File(args[0]);
        fOutputDir = new File(args[1]);
        ToCtrl ctrl = Controller.getInstance();
        ctrl.setInputFile(fInputDir);
        ctrl.setOutputFile(fOutputDir);
        ctrl.parseJava();
        ctrl.writePlant(ClassList.getInstance());
    }
}
