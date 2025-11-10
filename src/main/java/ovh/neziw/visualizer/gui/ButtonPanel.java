/*
 * This file is part of "linear-regression-visualizer", licensed under MIT License.
 *
 *  Copyright (c) 2025 neziw
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package ovh.neziw.visualizer.gui;

import java.awt.FlowLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ButtonPanel extends JPanel {

    public ButtonPanel(final Runnable onSave, final Runnable onLoad, final Runnable onExport) {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        final JButton saveButton = new JButton("Zapisz do pliku");
        saveButton.addActionListener(e -> onSave.run());
        final JButton loadButton = new JButton("Załaduj z pliku");
        loadButton.addActionListener(e -> onLoad.run());
        final JButton exportButton = new JButton("Eksportuj do Excela");
        final ImageIcon excelIcon = this.loadExcelIcon();
        if (excelIcon != null) {
            exportButton.setIcon(excelIcon);
        }
        exportButton.addActionListener(e -> onExport.run());
        this.add(saveButton);
        this.add(loadButton);
        this.add(exportButton);
    }

    private ImageIcon loadExcelIcon() {
        try {
            final java.net.URL iconUrl = ButtonPanel.class.getResource("/excel.png");
            if (iconUrl != null) {
                return new ImageIcon(iconUrl);
            }
        } catch (final Exception exception) {
        }
        return null;
    }
}
