/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.praat;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.openide.modules.InstalledFileLocator;

/**
 *
 * @author Andreas Hauffe
 */
public class PraatUtilities {

    private static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
    }

    public static boolean isSolaris() {
        return (OS.indexOf("sunos") >= 0);
    }
    
    public static Path getPraatExecutable(String localFFMPEGFolder) throws URISyntaxException{
        String execPath = null;
        String execName = null;
        if (isWindows()){
            execPath = "Praat/windows/";
            execName = "Praat.exe";
        }else if(isUnix()){
            execPath = "Praat/linux/";
            execName = "praat";
        }
        /*try {
            File file = InstalledFileLocator.getDefault().locate(execPath + execName, "de.oscvev.virtualchoir.praat", false);
            File targetFile = new File(localFFMPEGFolder + File.separator + execName);
            Path newPath = Paths.get(targetFile.toString());
            Files.copy(Paths.get(file.toString()), newPath, REPLACE_EXISTING);
            return newPath;
        } catch (IOException ex) {
            Logger.getLogger(PraatUtilities.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;*/
        return Paths.get(InstalledFileLocator.getDefault().locate(execPath + execName, "de.oscvev.virtualchoir.praat", false).toString());
    }
}

