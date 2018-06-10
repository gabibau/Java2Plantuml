/*
 * To change c template, choose Tools | Templates
 * and open the template in the editor.
 */
package java2plant.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java2plant.describer.ArgumentDescriber;
import java2plant.describer.ClassDescriber;
import java2plant.describer.ContextDescriber;
import java2plant.describer.FieldDescriber;
import java2plant.describer.MethodDescriber;
import java2plant.describer.Visibility;
import java2plant.model.ClassCollection;

/**
 * 
 * @author arthur
 */
public class PlantWriter extends AbstractWriter {

    private final ClassCollection classes;

    private ArrayList<Relation> relations = new ArrayList();

    public PlantWriter(ClassCollection classes) {

        this.classes = classes;
    }

    @Override
    public void write(File fOutputDir) {

        FileWriter commonFW = null;
        try {
            fOutputDir.mkdirs();

            commonFW = new FileWriter(fOutputDir.getAbsolutePath()
                    + File.separator + "complete-diag.uml");
            commonFW.write("@startuml img/default.png\n");
            UML.append("@startuml img/default-all.png\n");
            for (ClassDescriber c : classes.getClasses()) {
                writeClass(c, fOutputDir);
                commonFW.write("!include " + "classes" + File.separator
                        + c.getName() + ".iuml\n");
            }

            // Create an empty file for user modifications
            File fRelations = new File(fOutputDir, "relations.iuml");
            if (!fRelations.exists()) {
                fRelations.createNewFile();
            }
            commonFW.write("!include " + "relations.iuml\n\n");
            writeRelations(commonFW);

            commonFW.write("\n@enduml\n");
            UML.append("\n@enduml\n");

        } catch (IOException ex) {
            Logger.getLogger(ContextDescriber.class.getName()).log(
                    Level.SEVERE, null, ex);
        } finally {
            try {
                commonFW.close();
            } catch (IOException ex) {
                Logger.getLogger(ContextDescriber.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
        }
        try {
            FileWriter fw = new FileWriter(fOutputDir.getAbsolutePath()
                    + File.separator + "complete-diag-all.uml");
            fw.write(UML.toString());
            fw.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        try {
            FileWriter fw = new FileWriter(fOutputDir.getAbsolutePath()
                    + File.separator + "relations.iuml");
            writeRelations(fw);
            fw.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public boolean relationExists(String class1, String class2) {

        for (Relation r : relations) {
            if (class1.equals(r.getClass1()) && class2.equals(r.getClass2())) {
                return true;
            }
        }
        return false;
    }

    public void addRelation(String class1, String class2) {

        // System.out.println("-->" + class1 + ":" + class2);
        if (class2.contains("<") && class2.indexOf(">") > class2.indexOf("<")) {
            class2 = class2.substring(class2.indexOf("<") + 1,
                    class2.indexOf(">"));
        }
        if (classes.classExists(class1) && classes.classExists(class2)) {
            if (!relationExists(class1, class2)) {
                relations.add(new Relation(class1, class2));
            }
        }

    }

    public void writeRelations(FileWriter fw) {

        for (Relation r : relations) {
            try {
                fw.write(r.getClass1() + " --> " + r.getClass2() + "\n");
                UML.append(r.getClass1()).append(" --> ").append(r.getClass2())
                        .append("\n\n");
            } catch (IOException ex) {
                Logger.getLogger(PlantWriter.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
        }
    }

    public void writeClass(ClassDescriber c, File fOutputDir) {

        BufferedWriter bw = null;
        try {
            String filename = fOutputDir.getAbsolutePath() + File.separator;
            filename += "classes" + File.separator + c.getName() + ".iuml";
            File f = new File(filename);
            f.getParentFile().mkdirs();
            bw = new BufferedWriter(new FileWriter(filename));

            if (!c.getPackage().isEmpty()) {
                // bw.write("package " + c.getPackage());
                bw.write("' ---\npackage " + c.getPackage() + "{");
                bw.newLine();
                bw.newLine();
                UML.append("' ---\npackage ").append(c.getPackage())
                        .append("{").append("\n\n");
            }
            if (c.isAbstract()) {
                bw.write("abstract ");
                UML.append("abstract ");
            }
            if (c.isInterface()) {
                bw.write("interface " + c.getName());
                UML.append("interface ").append(c.getName());
            } else {
                bw.write("class " + c.getName());
                UML.append("class ").append(c.getName());
                
                if ("private".equals(c.getVisibility().toString())) {
                    bw.write(" << Inner >> <<(P, purple) >> ");
                    UML.append(" << Inner >> <<(P, purple) >> ");
                }
            }

            bw.write(" {");
            bw.newLine();
            UML.append(" {").append("\n");
            for (FieldDescriber fd : c.getFields()) {
                writeField(fd, bw);
                addRelation(c.getName(), fd.getType());
            }
            for (MethodDescriber md : c.getMethods()) {
                writeMethod(md, bw);
            }
            // bw.write("}');
            bw.write("}\n'    ----- end class " + c.getName());
            bw.newLine();
            bw.newLine();
            UML.append("}\n'    ----- end class ").append(c.getName())
                    .append("\n\n");

            if (!c.getPackage().isEmpty()) {
                // bw.write("end package");
                bw.write("}\n'    ------------------------ end package "
                        + c.getPackage());
                bw.newLine();
                bw.newLine();
                UML.append("}\n'    ------------------------ end package ")
                        .append(c.getPackage()).append("\n\n");
            }
            
            for (String inh : c.getInheritances()) {
                  bw.write(" " + inh + " <|-- " + c.getName());
                  bw.newLine();
                  UML.append(" ").append(inh).append(" <|-- ")
                        .append(c.getName()).append("\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(ClassDescriber.class.getName()).log(Level.SEVERE,
                    null, ex);
        } finally {
            try {
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(ClassDescriber.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
        }
    }

    public void writeField(FieldDescriber fd, BufferedWriter bw) {

        try {
            String type = fd.isStatic() ? " {static} " : " ";
            writeVisibility(fd.getVisibility(), bw);
            bw.write(type + fd.getName() + ":" + fd.getType());
            bw.newLine();
            UML.append(type).append(fd.getName()).append(":")
                    .append(fd.getType()).append("\n");
        } catch (IOException ex) {
            Logger.getLogger(FieldDescriber.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }

    public void writeMethod(MethodDescriber md, BufferedWriter bw) {

        try {
            String type = md.isStatic() ? " {static} " : " ";
            type = " ".equals(type) ? (md.isAbstract() ? " {abstract} " : " ")
                    : type;
            writeVisibility(md.getVisibility(), bw);
            bw.write(type + md.getName() + "(");
            UML.append(type).append(md.getName()).append("(");
            for (Iterator it = md.getArgs().iterator(); it.hasNext();) {
                ArgumentDescriber arg = (ArgumentDescriber) it.next();
                writeArgument(arg, bw);
                if (it.hasNext()) {
                    bw.write(", ");
                    UML.append(",");
                }
            }
            if (md.getReturnType().equals("void")) {
                bw.write(")");
                UML.append(")");
            } else {
                bw.write("):" + md.getReturnType());
                UML.append("):").append(md.getReturnType());
            }
            bw.newLine();
            UML.append("\n");

        } catch (IOException ex) {
            Logger.getLogger(MethodDescriber.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }

    public void writeArgument(ArgumentDescriber arg, BufferedWriter bw) {

        try {
            bw.write(arg.getName() + ":" + arg.getType());
            UML.append(arg.getName()).append(":").append(arg.getType());
        } catch (IOException ex) {
            Logger.getLogger(ArgumentDescriber.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    public void writeVisibility(Visibility vis, BufferedWriter bw) {

        String result;
        String visibility = vis.toString();
        if (visibility.equals("public")) {
            result = "+";
        } else if (visibility.equals("protected")) {
            result = "#";
        } else if (visibility.equals("package")) {
            result = "~";
        } else if (visibility.equals("static")) {
            result = "{static}";
        } else if (visibility.equals("abstract")) {
            result = "{abstract}";
        } else {
            result = "-";
        }
        try {
            bw.write(result);
            UML.append(result);
        } catch (IOException ex) {
            Logger.getLogger(Visibility.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }

}
