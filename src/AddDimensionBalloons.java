import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddDimensionBalloons {

    public static void main(String[] args) {
        try {
            // Load the PDF file
            File file = new File("C:/Users/IMP 04/Downloads/zuelhc1o.pdf");
            PDDocument document = Loader.loadPDF(file);
            PDPage page = document.getPage(0);

            // Extract dimension text and positions
            DimensionTextStripper stripper = new DimensionTextStripper();
            stripper.setStartPage(1);
            stripper.setEndPage(1);
            stripper.getText(document);
            List<DimensionText> dimensions = stripper.getDimensions();

            System.out.println("Extracted dimensions count: " + dimensions.size());
            for (DimensionText dim : dimensions) {
                System.out.println("Dim: " + dim.text + " at (" + dim.x + ", " + dim.y + ")");
            }

            // Prepare to write balloons
            PDPageContentStream contentStream = new PDPageContentStream(document, page,
                    PDPageContentStream.AppendMode.APPEND, true);

            int balloonNumber = 1;
            if (!dimensions.isEmpty()) {
                for (DimensionText dim : dimensions) {
                    drawBalloon(contentStream, dim.x, dim.y, String.valueOf(balloonNumber++));
                }
            } else {
                System.out.println("No dimension text found!");
            }

            contentStream.close();
            document.save("annotated_zuelhc1o.pdf");
            document.close();

            System.out.println("Annotated PDF saved with " + (balloonNumber - 1) + " balloons.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to draw balloons around dimension numbers
    private static void drawBalloon(PDPageContentStream contentStream, float x, float y, String number) throws IOException {
        float radius = 12;
        float k = 0.55228475f;
        float c = k * radius;

        contentStream.setLineWidth(1);

        // Draw circle using Bezier curves
        contentStream.moveTo(x, y + radius);
        contentStream.curveTo(x + c, y + radius, x + radius, y + c, x + radius, y);
        contentStream.curveTo(x + radius, y - c, x + c, y - radius, x, y - radius);
        contentStream.curveTo(x - c, y - radius, x - radius, y - c, x - radius, y);
        contentStream.curveTo(x - radius, y + c, x - c, y + radius, x, y + radius);
        contentStream.stroke();

        // Set font
        var font = new org.apache.pdfbox.pdmodel.font.PDType1Font(Standard14Fonts.FontName.HELVETICA);
        int fontSize = 8; // Reduced font size for better fit

        // Calculate text width for centering
        float textWidth = font.getStringWidth(number) / 1000 * fontSize;
        
        // Adjust text position to center it both horizontally and vertically
        float textX = x - (textWidth / 2);
        float textY = y - (fontSize / 3); // Adjust vertical position

        // Draw number inside the balloon
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.setNonStrokingColor(0, 0, 0); // Ensure text is black
        contentStream.newLineAtOffset(textX, textY);
        contentStream.showText(number);
        contentStream.endText();
    }

    // Class to hold dimension text and coordinates
    static class DimensionText {
        String text;
        float x, y;

        DimensionText(String text, float x, float y) {
            this.text = text;
            this.x = x;
            this.y = y;
        }
    }

    // TextStripper to capture dimension-like text positions
    static class DimensionTextStripper extends PDFTextStripper {
        List<DimensionText> dimensions = new ArrayList<>();

        public DimensionTextStripper() throws IOException {
            super.setSortByPosition(true);
        }

        public List<DimensionText> getDimensions() {
            return dimensions;
        }

        @Override
        protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
            if (isDimension(string)) {
                TextPosition first = textPositions.get(0);
                float x = first.getXDirAdj();
                float y = first.getYDirAdj();
                dimensions.add(new DimensionText(string, x, y));
            }
        }

        private boolean isDimension(String s) {
            return s.matches(".*[\\dØ\\.]+.*"); // Crude check: has digit or diameter symbol
        }
    }
}