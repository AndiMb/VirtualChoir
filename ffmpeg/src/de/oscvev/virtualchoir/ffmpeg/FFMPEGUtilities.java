package de.oscvev.virtualchoir.ffmpeg;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.openide.modules.InstalledFileLocator;

/**
 *
 * @author Andreas Hauffe
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
