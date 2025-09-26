package gui.style;

public class MyBorder {

    public static class Rounded implements Border {

        private int radius;
        public Rounded(int radius) {
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(5, 10, 5, 10); // top, left, bottom, right padding
        }

        @Override
        public boolean isBorderOpaque() {
            return false; // background can show through rounded corners
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.GRAY);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }
}
