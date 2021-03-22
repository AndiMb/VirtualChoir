/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.filesupport;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.swing.SwingUtilities;
import org.netbeans.spi.actions.AbstractSavable;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Confirmation;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Messages({
    "LBL_vch_LOADER=Files of vch"
})
@MIMEResolver.ExtensionRegistration(
        displayName = "#LBL_vch_LOADER",
        mimeType = "text/vch+xml",
        extension = {"vch", "VCH"}
)
@DataObject.Registration(
        mimeType = "text/vch+xml",
        iconBase = "de/oscvev/virtualchoir/filesupport/resources/ilr_icon16.png",
        displayName = "#LBL_vch_LOADER",
        position = 300
)
@ActionReferences({
    @ActionReference(
            path = "Loaders/text/vch+xml/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
            position = 100,
            separatorAfter = 200
    ),
    @ActionReference(
            path = "Loaders/text/vch+xml/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300
    ),
    @ActionReference(
            path = "Loaders/text/vch+xml/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500
    ),
    @ActionReference(
            path = "Loaders/text/vch+xml/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600
    ),
    @ActionReference(
            path = "Loaders/text/vch+xml/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
            position = 700,
            separatorAfter = 800
    ),
    @ActionReference(
            path = "Loaders/text/vch+xml/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
            position = 900,
            separatorAfter = 1000
    ),
    @ActionReference(
            path = "Loaders/text/vch+xml/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
            position = 1100,
            separatorAfter = 1200
    ),
    @ActionReference(
            path = "Loaders/text/vch+xml/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
            position = 1300
    ),
    @ActionReference(
            path = "Loaders/text/vch+xml/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400
    )
})
public class vchDataObject extends MultiDataObject {
    
    private final Lookup lookup;
    private final InstanceContent lookupContents = new InstanceContent();

    private static final int actVirtualChoirFileVersion = 1;

    public vchDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        lookup = new ProxyLookup(getCookieSet().getLookup(), new AbstractLookup(lookupContents));
        loadData();
        this.addVetoableChangeListener(new VetoableChangeListener() {

            @Override
            public void vetoableChange(PropertyChangeEvent e) throws PropertyVetoException {
                if (isModified()) {
                    Confirmation message = new NotifyDescriptor.Confirmation(NbBundle.getMessage(vchDataObject.class, "SaveFileMessage", vchDataObject.this.getPrimaryFile().getName()),
                            NotifyDescriptor.YES_NO_OPTION,
                            NotifyDescriptor.QUESTION_MESSAGE);

                    Object result = DialogDisplayer.getDefault().notify(message);
                    //When user clicks "Yes", indicating they really want to save,
                    //we need to disable the Save action,
                    //so that it will only be usable when the next change is made
                    //to the JTextField:
                    if (NotifyDescriptor.YES_OPTION.equals(result)) {
                        MySavable saveC = getLookup().lookup(MySavable.class);
                        try {
                            saveC.save();
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            }
        });
        registerEditor("text/vch+xml", false);
    }

    @Override
    protected Node createNodeDelegate() {
        return new DataNode(this, Children.LEAF, getLookup());
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public void setModified(final boolean isModified) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    setModified(isModified);
                }
            });
        } else {
            // I tied the SaveCookie implementation into this such that
            // the Save action is enabled whenever the object is modified.
            if (isModified) {
                if (getLookup().lookup(MySavable.class) == null) {
                    lookupContents.add(new MySavable());
                }
            } else {
                MySavable savable = getLookup().lookup(MySavable.class);
                if (savable != null) {
                    lookupContents.remove(savable);
                    savable.myUnregister();
                }
            }
            super.setModified(isModified);
        }
    }

    private void loadData() {
        try {
            //Get the InputStream of the file:
            InputStream is = getPrimaryFile().getInputStream();
            //Use the NetBeans org.openide.xml.XMLUtil class to create an org.w3c.dom.Document:
            Document doc = XMLUtil.parse(new InputSource(is), false, false, null, null);
            is.close();

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            //Find the car node:
            org.w3c.dom.Node nNode = doc.getElementsByTagName("virtualchoir").item(0);
            String versionString = ((Element) nNode).getAttribute("version");
            int version = 1;
            if (!versionString.isEmpty()) {
                version = Integer.parseInt(versionString);
            }
            if (version > actVirtualChoirFileVersion) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(vchDataObject.class, "Error.wrongFileVersion"), NotifyDescriptor.ERROR_MESSAGE));
            } else {
                if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element virtualChoirElement = (Element) nNode;
                    for (LoadSaveHook lsh : Lookup.getDefault().lookupAll(LoadSaveHook.class)) {
                        lsh.load(virtualChoirElement);
                    }
                }
            }
        } catch (IOException | SAXException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void storeData() {
        try {
            //Get the InputStream of the file:
            InputStream is = getPrimaryFile().getInputStream();
            //Use the NetBeans org.openide.xml.XMLUtil class to create an org.w3c.dom.Document:
            Document doc = XMLUtil.parse(new InputSource(is), true, true, null, null);
            //Find the car node:
            org.w3c.dom.Node nNode = doc.getElementsByTagName("virtualchoir").item(0);
            Attr attr = doc.createAttribute("version");
            attr.setValue("" + actVirtualChoirFileVersion);
            ((Element) nNode).setAttributeNode(attr);
            if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element virtualChoirElement = (Element) nNode;
                for (LoadSaveHook lsh : Lookup.getDefault().lookupAll(LoadSaveHook.class)) {
                    lsh.store(doc, virtualChoirElement);
                }
            }

            is.close();
            //Write the changed document to the underlying file:
            OutputStream fos = null;
            try {
                // fos = new FileOutputStream(FileUtil.toFile(getPrimaryFile().));
                fos = getPrimaryFile().getOutputStream();
                XMLUtil.write(doc, fos, "UTF-8"); // NOI18N
            } catch (Exception e) {
                Exceptions.printStackTrace(e);
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (Exception e) {
                    Exceptions.printStackTrace(e);
                }
            }
        } catch (IOException | SAXException ex) {
            Exceptions.printStackTrace(ex);
        }
        setModified(false);
    }

    private class MySavable extends AbstractSavable {

        MySavable() {
            register();
        }

        @Override
        protected String findDisplayName() {
            return vchDataObject.this.getName();
        }

        @Override
        protected void handleSave() throws IOException {
            storeData();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MySavable) {
                return obj == this;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return vchDataObject.this.hashCode();
        }

        public void myUnregister() {
            this.unregister();
        }
    }

}
