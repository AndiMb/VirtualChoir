/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.filesupport;

import de.oscvev.virtualchoir.core.VirtualChoirLookup;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import javax.swing.Action;
import org.openide.DialogDisplayer;
import org.openide.LifecycleManager;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileUtil;
import org.openide.modules.Places;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = LifecycleManager.class, position = 1)
public class virtualChoirLifeCycleManager extends LifecycleManager {

    @Override
    public void saveAll() {
    }

    @Override
    public void exit() {
        boolean close = true;
        if (VirtualChoirLookup.getDefault().getDataObject().isModified()) {
            NotifyDescriptor.Confirmation message = new NotifyDescriptor.Confirmation(NbBundle.getMessage(Installer.class, "SaveFileMessage", VirtualChoirLookup.getDefault().getFileObject().getName()),
                    NotifyDescriptor.YES_NO_CANCEL_OPTION,
                    NotifyDescriptor.QUESTION_MESSAGE);

            Object result = DialogDisplayer.getDefault().notify(message);
            //When user clicks "Yes", indicating they really want to save,
            //we need to disable the Save action,
            //so that it will only be usable when the next change is made
            //to the JTextField:
            if (NotifyDescriptor.YES_OPTION.equals(result)) {
                Action a = FileUtil.getConfigObject("Actions/File/de-elamx-filesupport-SaveAction.instance", Action.class);
                if (a != null) {
                    a.actionPerformed(null);
                }
                if (VirtualChoirLookup.getDefault().getDataObject().isModified()){
                    close = false;
                }
            } else if (NotifyDescriptor.NO_OPTION.equals(result)) {
                VirtualChoirLookup.getDefault().setModified(false);
            }else{
                close = false;
            }
        }
        if (close) {
            Collection<? extends LifecycleManager> c = Lookup.getDefault().lookupAll(LifecycleManager.class);
            for (LifecycleManager lm : c) {
                if (lm != this) {
                    lm.exit();
                }
            }
        }
    }

    @Override
    public void markForRestart() throws UnsupportedOperationException {
        /*String classLoaderName = TopSecurityManager.class.getClassLoader().getClass().getName();
        if (!classLoaderName.endsWith(".Launcher$AppClassLoader") && !classLoaderName.endsWith(".ClassLoaders$AppClassLoader")) {   // NOI18N
            throw new UnsupportedOperationException("not running in regular module system, cannot restart"); // NOI18N
        }*/
        File userdir = Places.getUserDirectory();
        if (userdir == null) {
            throw new UnsupportedOperationException("no userdir"); // NOI18N
        }
        File restartFile = new File(userdir, "var/restart"); // NOI18N
        if (!restartFile.exists()) {
            try {
                restartFile.createNewFile();
            } catch (IOException x) {
                throw new UnsupportedOperationException(x);
            }
        }
    }

}