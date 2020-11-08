/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.filesupport.actions;

import de.oscvev.virtualchoir.core.VirtualChoirLookup;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.*;
import org.openide.util.NbBundle;

@ActionID(category = "File",
id = "de.sandmesh.filesupport.actions.OpenFileAction")
@ActionRegistration(iconBase = "de/oscvev/virtualchoir/filesupport/resources/fileopen.png",
displayName = "#CTL_OpenFileAction")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 0),
    @ActionReference(path = "Toolbars/File", position = 0)
})
public class OpenFileAction implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        File basePath = new File(System.getProperty("user.home"));
        File file = new FileChooserBuilder("database-dir").setTitle(NbBundle.getMessage(OpenFileAction.class, "OpenFileAction.Title")).setDefaultWorkingDirectory(basePath).setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                String name = f.getName().toLowerCase();
                return name.endsWith(".vch") || name.endsWith(".VCH") || (f.isDirectory() && !f.isHidden());
            }

            @Override
            public String getDescription() {
                return NbBundle.getMessage(OpenFileAction.class, "OpenFileAction.Description");
            }
        }).setSelectionApprover(new FileChooserBuilder.SelectionApprover() {

            @Override
            public boolean approve(File[] selection) {
                if (selection.length > 1) return false;
                return selection[0].getName().endsWith(".vch") | selection[0].getName().endsWith(".VCH");
            }
        }).setApproveText(NbBundle.getMessage(OpenFileAction.class, "OpenFileAction.ApproveText")).setFileHiding(true).showOpenDialog();

        if (file != null && file.exists()) {
            FileObject fo = FileUtil.toFileObject(file);
            VirtualChoirLookup.getDefault().setFileObject(fo);
        }
    }
}
