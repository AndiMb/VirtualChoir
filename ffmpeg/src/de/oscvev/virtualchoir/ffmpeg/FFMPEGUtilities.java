package de.oscvev.virtualchoir.ffmpeg;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.openide.modules.InstalledFileLocator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author DOM+ahauffe
 */
public class FFMPEGUtilities {

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
    
    public static Path getFFMPEGExecutable(String localFFMPEGFolder) throws URISyntaxException{
        String execPath = null;
        String execName = null;
        if (isWindows()){
            execPath = "ffmpeg/windows/bin/";
            execName = "ffmpeg.exe";
        }else if(isUnix()){
            execPath = "ffmpeg/linux/";
            execName = "ffmpeg";
        }
        /*try {
            File file = InstalledFileLocator.getDefault().locate(execPath + execName, "de.oscvev.virtualchoir.ffmpeg", false);
            File targetFile = new File(localFFMPEGFolder + File.separator + execName);
            Path newPath = Paths.get(targetFile.toString());
            Files.copy(Paths.get(file.toString()), newPath, REPLACE_EXISTING);
            return newPath;
        } catch (IOException ex) {
            Logger.getLogger(FFMPEGUtilities.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;*/
        return Paths.get(InstalledFileLocator.getDefault().locate(execPath + execName, "de.oscvev.virtualchoir.ffmpeg", false).toString());
    }
    
    private final static String[] videoFormats = new String[]{".mp4", ".mov", ".avi", ".webm"};
    private final static String[] audioFormats = new String[]{".mp3", ".wav"};
    
    public static String[] getSupportedVideoFormats(){
        return videoFormats;
    }
    
    public static String[] getSupportedAudioFormats(){
        return audioFormats;
    }
}
