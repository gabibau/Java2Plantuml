/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package java2plant.writer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author arthur
 */
public abstract class AbstractWriter {
    public static Map<String, String> UML_CALSS = new HashMap<String, String>();
    public static StringBuffer UML = new StringBuffer();
    public abstract void write(File fOutputDir);

}
