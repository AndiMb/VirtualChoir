/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.caprica.vlcj;

import java.util.ArrayList;
import java.util.List;
import org.openide.modules.InstalledFileLocator;
import uk.co.caprica.vlcj.binding.LibC;
import uk.co.caprica.vlcj.binding.RuntimeUtil;
import uk.co.caprica.vlcj.factory.discovery.strategy.BaseNativeDiscoveryStrategy;



public class BundledVLCLibsDiscoveryStrategy extends BaseNativeDiscoveryStrategy {

    private static final String[] FILENAME_PATTERNS = new String[] {
        "libvlc\\.dll",
        "libvlccore\\.dll"
    };

    private static final String[] PLUGIN_PATH_FORMATS = new String[] {
        "%s\\plugins",
        "%s\\vlc\\plugins"
    };

    public BundledVLCLibsDiscoveryStrategy() {
        super(FILENAME_PATTERNS, PLUGIN_PATH_FORMATS);
    }

    protected List<String> discoveryDirectories() {
        ArrayList<String> directories = new ArrayList<String>();
        String dir = InstalledFileLocator.getDefault().locate("vlclibs/windows/libvlc.dll", "uk.co.caprica.vlcj", false).toString();
        directories.add(dir.replace("libvlc.dll", ""));
        return directories;
    }

    protected boolean setPluginPath(String pluginPath) {
        return LibC.INSTANCE._putenv(String.format("%s=%s", PLUGIN_ENV_NAME, pluginPath)) == 0;
    }

    public boolean supported() {
        return RuntimeUtil.isWindows();
    }

}
