package com.oliveryasuna.idea.qemu.run.ui;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.JBColor;
import com.intellij.ui.ListCellRendererWithRightAlignedComponent;
import com.jetbrains.cidr.cpp.cmake.model.CMakeTarget;
import com.jetbrains.cidr.cpp.execution.CMakeBuildConfigurationHelper;
import com.oliveryasuna.idea.qemu.run.config.QemuRunConfiguration;
import com.oliveryasuna.idea.qemu.run.config.QemuRunConfigurationOptions;
import com.oliveryasuna.idea.qemu.util.ExecutableUtils;
import com.oliveryasuna.idea.qemu.util.QemuExecutableFinder;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;

// I hate Swing...
public class QemuRunConfigurationEditor extends SettingsEditor<QemuRunConfiguration> {

  // Constructors
  //--------------------------------------------------

  public QemuRunConfigurationEditor(final Project project) {
    super();

    this.project = project;
  }

  // Fields
  //--------------------------------------------------

  private final Project project;

  private Border qemuExecutableFieldDefaultBorder;

  private Border qemuExecutableFieldErrorBorder;

  // UI components
  //--------------------------------------------------

  private JPanel rootPanel;

  // TODO: Allow custom items.
  private JComboBox<String> qemuExecutableField;

  private JRadioButton cmakeTargetRadio;

  private JComboBox<CMakeTarget> cmakeTargetField;

  private JRadioButton cdromFileRadio;

  private TextFieldWithBrowseButton cdromFileField;

  // SettingsEditor methods
  //--------------------------------------------------

  @Override
  protected final void resetEditorFrom(final QemuRunConfiguration runConfig) {
    qemuExecutableField.setSelectedItem(runConfig.getQemuExecutable());

    switch(runConfig.getDiskImageSource()) {
      case CMAKE_TARGET:
        cmakeTargetRadio.doClick();
        break;
      case CDROM_FILE:
        cdromFileRadio.doClick();
        break;
    }

    cmakeTargetField.setSelectedItem(runConfig.getCMakeTarget());
    cdromFileField.setText(runConfig.getCdromFile());
  }

  @Override
  protected final void applyEditorTo(final QemuRunConfiguration runConfig) throws ConfigurationException {
    runConfig.setQemuExecutable((String)qemuExecutableField.getSelectedItem());

    if(cmakeTargetRadio.isSelected()) {
      runConfig.setDiskImageSource(QemuRunConfigurationOptions.DiskImageSource.CMAKE_TARGET);
    } else if(cdromFileRadio.isSelected()) {
      runConfig.setDiskImageSource(QemuRunConfigurationOptions.DiskImageSource.CDROM_FILE);
    }

    runConfig.setCMakeTarget((CMakeTarget)cmakeTargetField.getSelectedItem());
    runConfig.setCdromFile(cdromFileField.getText());
  }

  @Override
  protected final JComponent createEditor() {
    qemuExecutableFieldDefaultBorder = qemuExecutableField.getBorder();

    qemuExecutableField.setModel(new DefaultComboBoxModel<>(QemuExecutableFinder.getQemuExecutables().stream()
        .map(File::getAbsolutePath)
        .toArray(String[]::new)));
    qemuExecutableField.addItemListener(this::qemuExecutableFieldChanged);

    cmakeTargetRadio.addActionListener(this::diskImageSourceRadioChanged);
    cdromFileRadio.addActionListener(this::diskImageSourceRadioChanged);

    cmakeTargetField.setModel(new ListComboBoxModel<>(new CMakeBuildConfigurationHelper(project).getTargets()));
    cmakeTargetField.setRenderer(new CMakeTargetCellRenderer());

    return rootPanel;
  }

  // Listener callbacks
  //--------------------------------------------------

  private void qemuExecutableFieldChanged(final ItemEvent event) {
    final String qemuExecutableCandidate = (String)event.getItem();

    new Thread(() -> {
      // TODO: Better way of doing border.
      if(ExecutableUtils.canExecute(qemuExecutableCandidate)) {
        qemuExecutableField.setBorder(qemuExecutableFieldDefaultBorder);
      } else {
        qemuExecutableField.setBorder(BorderFactory.createLineBorder(JBColor.RED));
      }
    }).start();
  }

  private void diskImageSourceRadioChanged(final ActionEvent event) {
    final JRadioButton source = (JRadioButton)event.getSource();

    if(source == cmakeTargetRadio) {
      cmakeTargetField.setEnabled(true);
      cdromFileField.setEnabled(false);
    } else if(source == cdromFileRadio) {
      cmakeTargetField.setEnabled(false);
      cdromFileField.setEnabled(true);
    }
  }

  // CMakeTargetCellRenderer class
  //--------------------------------------------------

  private static final class CMakeTargetCellRenderer extends ListCellRendererWithRightAlignedComponent<CMakeTarget> {

    // Constructors
    //--------------------------------------------------

    private CMakeTargetCellRenderer() {
      super();
    }

    // ListCellRendererWithRightAlignedComponent methods
    //--------------------------------------------------

    @Override
    protected final void customize(final CMakeTarget cmakeTarget) {
      if(cmakeTarget != null) {
        setIcon(cmakeTarget.getIcon());
        setLeftText(cmakeTarget.getName());
      }
    }

  }

}
