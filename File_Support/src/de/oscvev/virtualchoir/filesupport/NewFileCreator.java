/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.filesupport;

import de.oscvev.virtualchoir.core.VirtualChoirLookup;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Andreas Hauffe
 */
public class NewFileCreator {
    private static AtomicInteger _integer = new AtomicInteger(0);
    
    public static void create(){
        try {
            URL ulr = NewFileCreator.class.getResource("/de/oscvev/virtualchoir/filesupport/vchTemplate.vch");
            FileObject fo = URLMapper.findFileObject(ulr);
            DataObject template = DataObject.find(fo);
            FileSystem memFS = FileUtil.createMemoryFileSystem();
            FileObject root = memFS.getRoot();
            DataFolder dataFolder = DataFolder.findFolder(root);
            DataObject gdo = template.createFromTemplate(dataFolder,
                    NbBundle.getMessage(NewFileCreator.class, "NewFile.name") + _integer.incrementAndGet());
            VirtualChoirLookup.getDefault().setFileObject(gdo.getPrimaryFile());
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
