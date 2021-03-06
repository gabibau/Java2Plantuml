package java2plant.builder;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java2plant.describer.ArgumentDescriber;
import java2plant.describer.ClassDescriber;
import java2plant.describer.ContextDescriber;
import java2plant.describer.FieldDescriber;
import java2plant.describer.MethodDescriber;

/**
 *
 * @author arthur
 */
public class FromJavaBuilder extends AbstractBuilder {

    public FromJavaBuilder() {

        this.context = ContextDescriber.getInstance();
    }

    @Override
    public ContextDescriber buildFromFile(File fInputDir) {

        try {
            ArrayList<File> files = new ArrayList();
            ArrayList<File> dirs = new ArrayList();

            if (fInputDir.isDirectory()) {
                dirs.add(fInputDir);
            } else {
                files.add(fInputDir);
            }

            for (int i = 0; i < dirs.size(); i++) {
                File[] childs = dirs.get(i).listFiles();
                for (File child : childs) {
                    if (child.isDirectory()) {
                        dirs.add(child);
                    } else if (child.getName().endsWith(".java")) {
                        files.add(child);
                    }
                }
            }
            for (File f : files)
                System.out.println(f.getAbsolutePath() + " " + f.getName());

            for (File f : files) {
                FileInputStream fis = new FileInputStream(f);
                buildFromStream(fis);
            }

        } catch (Exception ex) {
            Logger.getLogger(FromJavaBuilder.class.getName()).log(Level.SEVERE,
                    null, ex);
        }

        return context;

    }

    public ContextDescriber buildFromStream(InputStream inputStream) {

        this.is = inputStream;

        String str = getNext(is);
        String decla = extractDeclaration(str);
        while (!str.isEmpty()) {

            if (decla.contains("package ")) {
                String[] split = splitString(decla);
                for (int i = 0; i < split.length; i++) {
                    if (split[i].contentEquals("package")) {
                        context.setNamespace(split[i + 1]);
                    }
                }
            } else if (decla.contains(" class ")) {
                buildClassFromString(str);
                // TODO: pas constistant avec le reste (pas de add)
            } else if (str.contains(" interface ")) {
                buildClassFromString(str);
            } else if (str.contains(" enum ")) {
                buildClassFromString(str);
            }
            str = getNext(is);
            decla = extractDeclaration(str);
        }

        return context;
    }

    public String getNext(InputStream is) {

        String str = "";
        try {
            int cOld = 0;
            int cNew = 0;
            boolean parsing = true;
            int openedBraces = 0;

            cNew = is.read();
            if (cNew == -1) {
                throw new EOFException();
            }

            while (parsing) {
                cOld = cNew;
                cNew = is.read();
                if (cNew == '\"' && cOld != '\\') {
                    cOld = cNew;
                    cNew = is.read();
                    while (cNew != '\"' || cOld == '\\') {
                        if (cOld == '\\') {
                            // dont take care of the escaped char
                            cNew = ' ';
                        }
                        cOld = cNew;
                        cNew = is.read();
                    }
                    cOld = cNew;
                    cNew = is.read();

                } else if (cNew == '\'' && cOld != '\\') {
                    cOld = cNew;
                    cNew = is.read();
                    while (cNew != '\'' || cOld == '\\') {
                        if (cOld == '\\') {
                            // dont take care of the escaped char
                            cNew = ' ';
                        }
                        cOld = cNew;
                        cNew = is.read();
                    }
                    cOld = cNew;
                    cNew = is.read();
                } else if (cOld == '/' && cNew == '*') {
                    while (cOld != '*' || cNew != '/') {
                        cOld = cNew;
                        cNew = is.read();
                    }
                    cOld = cNew;
                    cNew = is.read();

                } else if (cOld == '/' && cNew == '/') {
                    while (cNew != '\n') {
                        cOld = cNew;
                        cNew = is.read();
                    }
                    cOld = cNew;
                    cNew = is.read();
                } else {
                    if (cNew == -1) {
                        parsing = false;
                    } else if (cNew == '{') {
                        openedBraces++;
                    } else if (cNew == '}') {
                        openedBraces--;
                        if (openedBraces == 0) {
                            parsing = false;
                        }
                    } else if (cNew == ';' && openedBraces == 0) {
                        parsing = false;
                    }
                    str += (char) cOld;
                }

            }
            str += (char) cNew;
        } catch (EOFException ex) {
        } catch (IOException ex) {
            Logger.getLogger(FromJavaBuilder.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        return str;
    }

    public String getNext(String src) {

        String next = "";

        if (src.indexOf(";") != -1
                && (src.indexOf(";") < src.indexOf("{") || src.indexOf("{") == -1)) {
            next = src.substring(0, src.indexOf(";") + 1);
        } else if (src.indexOf(";") != -1 && src.indexOf("=") != -1 && src.indexOf("{") != -1
                && src.indexOf("=") < src.indexOf("{")) {
            next = src.substring(0, src.indexOf(";") + 1);
        } else if (src.indexOf("{") != -1) {
            int openedBraces = 1;
            int i = src.indexOf("{") + 1;
            while (openedBraces > 0) {
                char c = src.charAt(i);
                if (c == '{') {
                    openedBraces++;
                } else if (c == '}') {
                    openedBraces--;
                }
                i++;
            }
            if (src.substring(i, i + 1).equals(";")) {
                next = src.substring(0, i + 1);
            } else {
                next = src.substring(0, i);
            }
        }

        return next;
    }

    public String extractDeclaration(String str) {

        String declaration = "";
        if (str.indexOf(";") != -1 && str.indexOf("{") != -1) {
            if (str.indexOf(";") < str.indexOf("{")) {
                declaration = str.substring(0, str.indexOf(";") + 1);
            } else {
                declaration = str.substring(0, str.indexOf("{") + 1);
            }
        } else if (str.indexOf("{") == -1) {
            declaration = str.substring(0, str.indexOf(";") + 1);
        } else if (str.indexOf(";") == -1) {
            declaration = str.substring(0, str.indexOf("{") + 1);
        }
        return declaration;
    }

    public ClassDescriber buildClassFromString(String str) {

        ClassDescriber cd = null;

        String declaration = extractDeclaration(str);
        String split[] = splitString(extractDeclaration(str));

        for (int i = 0; i < split.length; i++) {
            if (split[i].equals("class")) {
                i++;
                cd = context.getClass(context.getNamespace(), split[i]);
            } else if (split[i].equals("interface")) {
                i++;
                cd = context.getClass(context.getNamespace(), split[i]);
                cd.setInterface(true);
            } else if (split[i].equals("enum")) {
                i++;
                cd = context.getClass(context.getNamespace(), split[i]);
            }
        }

        cd.setPackage(context.getNamespace());

        for (int i = 0; i < split.length; i++) {
            if (split[i].equals("public") || split[i].equals("private")
                    || split[i].equals("protected")
                    || split[i].equals("package")) {
                cd.setVisibility(split[i]);
            } else if (split[i].equals("abstract")) {
                cd.setAbstract(true);
            } else if (split[i].equals("extends")) {
                i++;
                for (; i < split.length && !split[i].equals("implements"); i++) {
                    cd.addInheritance(split[i].replace(",", ""));
                }
            } else if (split[i].equals("implements")) {
                i++;
                for (; i < split.length && !split[i].equals("extends"); i++) {
                    cd.addInheritance(split[i].replace(",", ""));
                }
            }
        }

        if (declaration.endsWith("{")) {
            str = str.substring(declaration.length(), str.lastIndexOf("}"));
        } else {
            str = "";
        }

        while (!str.isEmpty()) {
            String current = getNext(str);
            declaration = extractDeclaration(current);

            if (current.isEmpty() || current.contains("static {")) { //Not precess static blocks
                str = "";
            } else if (current.endsWith(";") && declaration.contains("=")) {
                FieldDescriber field = buildFieldFromString(current);
                cd.addField(field);
            } else if (current.endsWith(";") && !declaration.contains("(")) {
                FieldDescriber field = buildFieldFromString(current);
                cd.addField(field);
            } else if (declaration.contains("class")) { // inner class
                ClassDescriber buildClassFromString = buildClassFromString(current);
                buildClassFromString.setVisibility("private");
            } else if (declaration.contains("enum")) { // inner class
                ClassDescriber buildClassFromString = buildClassFromString(current);
                buildClassFromString.setVisibility("private");
            } else {
                MethodDescriber method = buildMethodFromString(declaration);
                cd.addMethod(method);
            }
            str = str.substring(current.length());
        }

        return cd;
    }

    public MethodDescriber buildMethodFromString(String str) {

        MethodDescriber md = new MethodDescriber();
        String[] split = splitString(str);
        int i = 0;
        while (i < split.length) {
            if (split[i].isEmpty() || split[i].trim().isEmpty()) {
                i++;
            } else if (split[i].equals("public") || split[i].equals("private")
                    || split[i].equals("protected")
                    || split[i].equals("package")) {
                md.setVisibility(split[i]);
                i++;
            } else if (split[i].equals("static")) {
                md.setStatic(true);
                i++;
            } else if (split[i].contains("final")) {
                i++;
            } else if (split[i].contains("abstract")) {
                md.setAbstract(true);
                i++;
            } else if (split[i].startsWith("@")) {
                i++;
            } else {
                if (split[i].contains("(")) {
                    md.setReturnType("");
                } else {
                    md.setReturnType(split[i]);
                    i++;
                }
                if (split[i].contains("(")) {
                    md.setName(split[i].substring(0, split[i].indexOf('(')));
                } else {
                    md.setName(split[i]);
                }
                i = split.length; // exit
            }

        }
        /* Construction des arguments */
        str = str.replace("@SuppressWarnings)", "");

        System.out.println(" problem string: " + str);

        str = str.substring(str.indexOf("(") + 1, str.indexOf(")"));

        if (!str.isEmpty()) {
            split = splitString(str, ",");
            for (int j = 0; j < split.length; j++) {
                ArgumentDescriber arg = buildArgumentFromString(split[j]);
                md.addArg(arg);
            }

        }
        return md;
    }

    public ArgumentDescriber buildArgumentFromString(String str) {

        ArgumentDescriber ad = new ArgumentDescriber();

        String[] split = splitString(str);
        int i = 0;
        while (i < split.length && (split[i].isEmpty() || split[i].contains("final"))) {
            i++;
        }
        ad.setType(split[i]);
        i++;
        while (i < split.length && split[i].isEmpty()) {
            i++;
        }
        ad.setName(split[i]);

        return ad;
    }

    public FieldDescriber buildFieldFromString(String str) {

        FieldDescriber fd = new FieldDescriber();
        if (str.contains("HashMap")) {
            System.out.println("Field Builder : " + str);
        }
        str = str.replace(";", "");

        String[] split = splitString(str);
        int i = 0;
        while (i < split.length) {
            if (split[i].isEmpty() || split[i].trim().isEmpty()) {
                i++;
            } else if (split[i].equals("public") || split[i].equals("private")
                    || split[i].equals("protected")
                    || split[i].equals("package")) {
                fd.setVisibility(split[i]);
                i++;
            } else if (split[i].contains("final")) {
                i++;
            } else if (split[i].contains("static")) {
                fd.setStatic(true);
                i++;
            } else {
                fd.setType(split[i]);
                fd.setName(split[i + 1]);
                i = split.length; // exit
            }
        }
        return fd;
    }

}
