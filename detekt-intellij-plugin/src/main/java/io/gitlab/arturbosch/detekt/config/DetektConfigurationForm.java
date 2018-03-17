package io.gitlab.arturbosch.detekt.config;

import com.intellij.openapi.fileChooser.*;
import com.intellij.openapi.ui.*;
import com.intellij.openapi.util.*;
import com.intellij.ui.*;
import javax.swing.*;
import org.jetbrains.annotations.*;

/**
 * @author Dmytro Primshyts
 */
public class DetektConfigurationForm {
    private JCheckBox enableDetekt;
    private JCheckBox checkTestSources;
    private JComboBox detektVersion;
    private TextFieldWithBrowseButton configurationFilePath;
    private JPanel myMainPanel;

    private DetektConfigStorage detektConfigStorage;

    @NotNull
    public JComponent createPanel(@NotNull DetektConfigStorage detektConfigStorage) {
        this.detektConfigStorage = detektConfigStorage;

        myMainPanel.setBorder(IdeBorderFactory.createTitledBorder("Detekt settings"));

        FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(
                true,
                false,
                false,
                false,
                false,
                false);

        configurationFilePath.addBrowseFolderListener(
                "",
                "Detekt rules file",
                null,
                fileChooserDescriptor,
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT
        );

        return myMainPanel;
    }

    public void apply() {
        detektConfigStorage.setEnableDetekt(enableDetekt.isSelected());
        detektConfigStorage.setCheckTestFiles(checkTestSources.isSelected());

        Object selectedDetektVersion = detektVersion.getSelectedItem();

        if (selectedDetektVersion != null) {
            detektConfigStorage.setDetektVersion(selectedDetektVersion.toString());
        } else {
            detektConfigStorage.setDetektVersion("");
        }

        detektConfigStorage.setRulesPath(configurationFilePath.getText());
    }

    public void reset() {
        enableDetekt.setSelected(detektConfigStorage.getEnableDetekt());
        checkTestSources.setSelected(detektConfigStorage.getCheckTestFiles());
        configurationFilePath.setText(detektConfigStorage.getRulesPath());
    }

    public boolean isModified() {
        return !Comparing.equal(detektConfigStorage.getEnableDetekt(), enableDetekt.isSelected())
                || !Comparing.equal(detektConfigStorage.getCheckTestFiles(), checkTestSources.isSelected())
                || !Comparing.equal(detektConfigStorage.getDetektVersion(), detektVersion.getSelectedItem())
                || !Comparing.equal(detektConfigStorage.getRulesPath(), configurationFilePath.getText());
    }
}
