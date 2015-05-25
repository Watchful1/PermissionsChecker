package gr.watchful.permchecker.panels;

import gr.watchful.permchecker.utils.DetectHtml;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class HTMLField extends JPanel {
    private JLabel label;
    private JTextArea textField;
    private JTabbedPane tabbedPane;
    private JLabel viewHTML;
    private ChangeListener changeListener;
    private String oldText;

    public HTMLField(String name) {
        this(name, null);
    }

    public HTMLField(String name, ChangeListener changeListener) {
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
		this.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100));
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setAlignmentX(0);

        this.changeListener = changeListener;
        oldText = "";

        label = new JLabel(name);
        label.setMinimumSize(new Dimension(90, 21));
        label.setMaximumSize(new Dimension(90, 21));
        label.setPreferredSize(new Dimension(90, 21));
        this.add(label);

        tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(JTabbedPane.RIGHT);

        textField = new JTextArea();
        textField.setLineWrap(true);
        textField.setWrapStyleWord(true);
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                //do nothing
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(oldText.equals(textField.getText())) return;
                oldText = textField.getText();

                notifyChanged();
            }
        });

        JScrollPane scrollTextArea = new JScrollPane(textField);

        VerticalTextIcon.addTab(tabbedPane, "Edit", scrollTextArea);

        viewHTML = new JLabel();
        viewHTML.setVerticalAlignment(SwingConstants.TOP);
        JScrollPane scrollLabel = new JScrollPane(viewHTML);
        scrollLabel.setMinimumSize(new Dimension(1, 100));

        VerticalTextIcon.addTab(tabbedPane, "View", scrollLabel);

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(tabbedPane.getSelectedIndex() == 1) {
                    viewHTML.setText("<html>".concat(getText()).concat("<html>"));
                }
            }
        });
        this.add(tabbedPane);
    }

    public void setText(String in) {
        textField.setText(in);
        oldText = in;
    }

    public String getText() {
        if(DetectHtml.isHtml(textField.getText())) return textField.getText();
        else return textField.getText().replaceAll("\n","<br>");
    }

    public String getPrettyText() {
        return "";//TODO
    }

    public void notifyChanged() {
        if(changeListener == null) return;
        changeListener.stateChanged(new ChangeEvent(this));
    }
}
