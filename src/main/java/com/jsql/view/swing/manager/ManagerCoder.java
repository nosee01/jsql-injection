/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.manager;

import java.awt.BorderLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jsql.view.swing.manager.util.ActionCoder;
import com.jsql.view.swing.manager.util.MenuBarCoder;
import com.jsql.view.swing.panel.util.HTMLEditorKitTextPaneWrap;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.splitpane.JSplitPaneWithZeroSizeDivider;
import com.jsql.view.swing.text.JPopupTextArea;
import com.jsql.view.swing.text.JPopupTextPane;
import com.jsql.view.swing.text.JTextAreaPlaceholder;
import com.jsql.view.swing.text.listener.DocumentListenerTyping;

/**
 * Manager to code/decode string in various methods.
 */
@SuppressWarnings("serial")
public class ManagerCoder extends JPanel implements Manager {
    
    /**
     * User input to encode.
     */
    private JTextArea textInput;

    /**
     * Encoding choice by user.
     */
    private JMenuItem menuMethod;

    /**
     * JTextArea displaying result of encoding/decoding.
     */
    private JTextPane result;
    
    private transient ActionCoder actionCoder = new ActionCoder(this);
    
    private class ChangeMenuListener implements ChangeListener {
        
        String nameMethod;
        
        ChangeMenuListener(String nameMethod) {
            this.nameMethod = nameMethod;
        }
        
        @Override
        public void stateChanged(ChangeEvent e) {
            
            if (e.getSource() instanceof JMenuItem) {
                
                JMenuItem item = (JMenuItem) e.getSource();
                
                if (item.isSelected() || item.isArmed()) {
                    ManagerCoder.this.actionCoder.actionPerformed(this.nameMethod);
                }
            }
        }
    }

    /**
     * Create a panel to encode a string.
     */
    public ManagerCoder() {
        
        super(new BorderLayout());

        this.textInput = new JPopupTextArea(new JTextAreaPlaceholder("Type a string to convert")).getProxy();
        this.textInput.setEditable(true);
        this.textInput.setLineWrap(true);
        
        this.textInput.getDocument().addDocumentListener(new DocumentListenerTyping() {
            
            @Override
            public void process() {
                
                ManagerCoder.this.actionCoder.actionPerformed();
            }
        });

        JPanel topMixed = this.initializeTopPanel();

        this.result = new JPopupTextPane("Result of conversion").getProxy();
        this.result.setContentType("text/html");
        this.result.setEditorKit(new HTMLEditorKitTextPaneWrap());
        
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(new LightScrollPane(1, 0, 0, 0, this.result), BorderLayout.CENTER);

        JSplitPaneWithZeroSizeDivider divider = new JSplitPaneWithZeroSizeDivider(JSplitPane.VERTICAL_SPLIT);
        divider.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        divider.setDividerSize(0);
        divider.setResizeWeight(0.5);

        divider.setTopComponent(topMixed);
        divider.setBottomComponent(bottom);

        this.add(divider, BorderLayout.CENTER);
    }

    private JPanel initializeTopPanel() {
        
        JPanel topMixed = new JPanel(new BorderLayout());

        final JPanel middleLine = new JPanel();
        middleLine.setLayout(new BorderLayout());
        middleLine.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 1));

        MenuBarCoder comboMenubar = this.initializeMenuBarCoder();
        
        this.menuMethod.setText("Encode to Base64");
        
        middleLine.add(comboMenubar);

        topMixed.add(new LightScrollPane(1, 0, 1, 0, this.textInput), BorderLayout.CENTER);
        topMixed.add(middleLine, BorderLayout.SOUTH);
        
        return topMixed;
    }

    private MenuBarCoder initializeMenuBarCoder() {
        
        Map<String, JMenu> mapMenus = new LinkedHashMap<>();
        
        mapMenus.put("Base64", new JMenu("Base64"));
        mapMenus.put("Hex", new JMenu("Hex"));
        mapMenus.put("Url", new JMenu("Url"));
        
        JMenu menuHtml = new JMenu("Html");
        mapMenus.put("Html", menuHtml);
        mapMenus.put("Base64(zipped)", new JMenu("Base64(zipped)"));
        mapMenus.put("Hex(zipped)", new JMenu("Hex(zipped)"));

        JMenuItem menuEncodeHtmlDecimal = new JMenuItem("Encode to Html (decimal)");
        menuHtml.add(menuEncodeHtmlDecimal);
        menuEncodeHtmlDecimal.addActionListener(this.actionCoder);
        menuEncodeHtmlDecimal.addChangeListener(new ChangeMenuListener("Encode to Html (decimal)"));
        
        mapMenus
        .entrySet()
        .stream()
        .forEach(entryMap -> {
            
            JMenuItem menuEncode = new JMenuItem("Encode to "+ entryMap.getKey());
            menuEncode.addActionListener(this.actionCoder);
            menuEncode.addChangeListener(new ChangeMenuListener("Encode to "+ entryMap.getKey()));
            
            JMenuItem menuDecode = new JMenuItem("Decode from "+ entryMap.getKey());
            menuDecode.addActionListener(this.actionCoder);
            menuDecode.addChangeListener(new ChangeMenuListener("Decode from "+ entryMap.getKey()));
            
            entryMap.getValue().add(menuEncode);
            entryMap.getValue().add(menuDecode);
        });

        mapMenus.put("Hash", new JMenu("Hash"));
        
        Stream
        .of("Adler32", "Crc16", "Crc32", "Crc64", "Md2", "Md4", "Md5", "Sha-1", "Sha-256", "Sha-384", "Sha-512", "Mysql")
        .forEach(hash -> {
            
            JMenuItem menuEncode = new JMenuItem("Hash to "+ hash);
            menuEncode.addActionListener(this.actionCoder);
            menuEncode.addChangeListener(new ChangeMenuListener("Hash to "+ hash));
            
            mapMenus.get("Hash").add(menuEncode);
        });

        JMenu comboMenu = MenuBarCoder.createMenu("Choose method...");
        this.menuMethod = comboMenu;
        
        for (JMenu menu: mapMenus.values()) {
            comboMenu.add(menu);
        }

        MenuBarCoder comboMenubar = new MenuBarCoder(comboMenu);
        comboMenubar.setOpaque(false);
        comboMenubar.setBorder(null);
        
        return comboMenubar;
    }
    
    // Getter and setter

    public JTextArea getTextInput() {
        return this.textInput;
    }

    public JMenuItem getMenuMethod() {
        return this.menuMethod;
    }

    public JTextPane getResult() {
        return this.result;
    }
}
