package org.example.pathfinder.Controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class CoverLetterTemplatesController {

    private String selectedTemplate;
    private String subject;
    private String content;

    public void setSubjectAndContent(String subject, String content) {
        this.subject = subject;
        this.content = content;
    }

    @FXML
    private void selectModernTemplate(Event event) {
        selectedTemplate = "Modern";
        generatePdfWithTemplate(event);
    }

    @FXML
    private void selectClassicTemplate(Event event) {
        selectedTemplate = "Classic";
        generatePdfWithTemplate(event);
    }

    @FXML
    private void selectCreativeTemplate(Event event) {
        selectedTemplate = "Creative";
        generatePdfWithTemplate(event);
    }

    private void generatePdfWithTemplate(Event event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        generatePdfWithTemplate(selectedTemplate, subject, content, stage);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void wrapText(PDPageContentStream contentStream, String text, PDDocument document, PDPage page) throws IOException {
        final float margin = 50;
        final float width = 500; // Width of the text block
        final float maxFontSize = 12f; // Max font size for readability
        float fontSize = maxFontSize;
        final float leading = 14f; // Default line spacing

        // Measure total text height
        PDType1Font font = PDType1Font.HELVETICA_BOLD;
        float totalHeight = getTextHeight(text, font, fontSize, width, leading);

        // If the total height exceeds the page height (minus margins), reduce font size
        while (totalHeight > page.getMediaBox().getHeight() - 2 * margin) {
            fontSize -= 1; // Decrease font size
            totalHeight = getTextHeight(text, font, fontSize, width, leading);
            if (fontSize < 6) break; // Set a lower limit on font size
        }

        float yPosition = page.getMediaBox().getHeight() - margin; // Start from the top of the page
        contentStream.setFont(font, fontSize);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, yPosition); // Start at the top of the page

        // Split the content into paragraphs and handle each
        String[] paragraphs = text.split("\n");
        for (String paragraph : paragraphs) {
            contentStream.newLineAtOffset(0, -leading); // Space before each paragraph
            String[] words = paragraph.split(" ");
            StringBuilder line = new StringBuilder();

            for (String word : words) {
                String lineWithWord = line + " " + word;
                float lineWidth = font.getStringWidth(lineWithWord) / 1000 * fontSize;

                // Check if the word exceeds the page width, and move to the next line
                if (lineWidth > width) {
                    contentStream.showText(line.toString());
                    line = new StringBuilder(word);
                    contentStream.newLineAtOffset(0, -leading);
                } else {
                    line.append(" ").append(word);
                }
            }

            // Show the last line of the paragraph
            if (line.length() > 0) {
                contentStream.showText(line.toString());
            }
        }

        contentStream.endText();
    }

    private float getTextHeight(String text, PDType1Font font, float fontSize, float width, float leading) throws IOException {
        String[] paragraphs = text.split("\n");
        float totalHeight = 0;
        for (String paragraph : paragraphs) {
            String[] words = paragraph.split(" ");
            StringBuilder line = new StringBuilder();

            for (String word : words) {
                String lineWithWord = line + " " + word;
                float lineWidth = font.getStringWidth(lineWithWord) / 1000 * fontSize;

                // If the line exceeds the width, add height for the previous line and start a new line
                if (lineWidth > width) {
                    totalHeight += leading;
                    line = new StringBuilder(word);
                } else {
                    line.append(" ").append(word);
                }
            }

            // Add height for the last line of the paragraph
            if (line.length() > 0) {
                totalHeight += leading;
            }
        }

        return totalHeight;
    }

    private void generatePdfWithTemplate(String template, String subject, String content, Stage stage) {
        // Define file chooser for saving the PDF
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);

                PDPageContentStream contentStream = new PDPageContentStream(document, page);

                // Wrap text to ensure it fits in one page
                wrapText(contentStream, content, document, page);

                contentStream.close();
                // Save the document
                document.save(file.getAbsolutePath());
                openPdfFile(file.getAbsolutePath());

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate PDF.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No File Selected", "You must select a file to save.");
        }
    }



    // Helper method to open the generated PDF
    private void openPdfFile(String filePath) {
        try {
            File pdfFile = new File(filePath);
            if (pdfFile.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    showAlert(Alert.AlertType.WARNING, "Warning", "Desktop operations are not supported on this platform.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "The PDF file does not exist.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open the PDF file.");
        }
    }

    @FXML
    private void handleCloseButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
