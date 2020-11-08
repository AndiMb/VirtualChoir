/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.filesupport.actions;

import de.oscvev.virtualchoir.core.VirtualChoirLookup;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import org.netbeans.api.actions.Savable;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

@ActionID(
        category = "File",
        id = "de.oscvev.virtualchoir.filesupport.actions.SaveAction"
)
@ActionRegistration(
        iconBase = "de/oscvev/virtualchoir/filesupport/resources/save.png",
        displayName = "#CTL_SaveAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1450),
    @ActionReference(path = "Toolbars/File", position = 200)
})
public final class SaveAction extends AbstractAction implements LookupListener {

    private final Lookup.Result<Savable> result;

    public SaveAction() {
        super();
        result = Savable.REGISTRY.lookupResult(Savable.class);
        result.addLookupListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (VirtualChoirLookup.getDefault().getFileObject().toURI().toString().startsWith("memory://")) {
            (new SaveAsAction()).actionPerformed(e);
        } else {
            if (result.allInstances().isEmpty()) {
                refreshEnabled(false);
            } else {
                for (Savable s : result.allInstances()) {
                    try {
                        s.save();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
    }

    @Override
    public void setEnabled(boolean newValue) {
        super.setEnabled(newValue); //To change body of generated methods, choose Tools | Templates.
    }

    private void refreshEnabled(final boolean isEnabled) {
        if (EventQueue.isDispatchThread()) {
            setEnabled(isEnabled);
        } else {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    setEnabled(isEnabled);
                }
            });
        }
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        refreshEnabled(!result.allItems().isEmpty());
    }
}
