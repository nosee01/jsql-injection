/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.interaction;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.jsql.view.GUI;
import com.jsql.view.GUITools;
import com.jsql.view.RoundScroller;
import com.jsql.view.component.TabHeader;

/**
 * Create a new tab for an administration webpage
 */
public class CreateAdminPageTab implements Interaction{
    // The main View
    private GUI gui;

    // Url for the administration webpage
    private final String url;

    /**
     * @param mainGUI
     * @param interactionParams Url of the webpage
     */
    public CreateAdminPageTab(GUI mainGUI, Object[] interactionParams){
        gui = mainGUI;

        url = (String) interactionParams[0];
    }

    /* (non-Javadoc)
     * @see com.jsql.mvc.view.message.ActionOnView#execute()
     */
    public void execute(){

        String htmlSource = "";
        try {
            htmlSource = Jsoup.clean(Jsoup.connect(url).get().html()
                    .replaceAll("<img.*>", "")
                    .replaceAll("<input.*type=\"?hidden\"?.*>", "")
                    .replaceAll("<input.*type=\"?(submit|button)\"?.*>", "<div style=\"background-color:#eeeeee;text-align:center;border:1px solid black;width:100px;\">button</div>") 
                    .replaceAll("<input.*>", "<div style=\"text-align:center;border:1px solid black;width:100px;\">input</div>"),
                    Whitelist.relaxed()
                    .addTags("center","div","span")
                    //              .addAttributes("input","type","value","disabled")
                    //              .addAttributes("img","src","width","height")
                    .addAttributes(":all","style")
                    //              .addEnforcedAttribute("input", "disabled", "disabled")
                    );
        } catch (IOException e) {
            gui.model.sendDebugMessage(e);
        }

        JTextPane browser = new JTextPane();
        browser.setContentType("text/html");
        browser.setEditable( false );
        browser.setText(htmlSource);

        final JPopupMenu menu = new JPopupMenu();
        JMenuItem item = new JMenuItem("Copy page URL");
        item.setIcon(GUITools.EMPTY);
        menu.add(item);

        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                StringSelection stringSelection = new StringSelection(url);
                Clipboard clipboard = Toolkit.getDefaultToolkit ().getSystemClipboard ();
                clipboard.setContents (stringSelection, null);
            }
        });

        browser.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    menu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }

            public void mouseReleased(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    menu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        });

        final RoundScroller scroller = new RoundScroller(browser);
        gui.right.addTab(url.replaceAll(".*/", "")+" ", scroller);

        // Focus on the new tab
        gui.right.setSelectedComponent(scroller);

        // Create a custom tab header with close button
        TabHeader header = new TabHeader(gui.right,
                new ImageIcon(getClass().getResource("/com/jsql/view/images/admin.png")));

        gui.right.setToolTipTextAt(gui.right.indexOfComponent(scroller), "<html>"+url+"</html>");

        // Apply the custom header to the tab
        gui.right.setTabComponentAt(gui.right.indexOfComponent(scroller), header);

        browser.requestFocusInWindow();

        // Get back to the top
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                scroller.getViewport().setViewPosition(new java.awt.Point(0, 0));
            }
        });
    }
}
