package frc.team670.libs.utils;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;

public class Color {

  private int r;
  private int g;
  private int b;

  public int[] getHSV() {
    int h, s, v;
    double rNorm = r / 255.0;
    double gNorm = g / 255.0;
    double bNorm = b / 255.0;

    double cMax = Math.max(rNorm, Math.max(gNorm, bNorm));
    double cMin = Math.min(rNorm, Math.min(gNorm, bNorm));
    double delta = cMax - cMin;

    h = 0;
    if (delta == 0) {
      h = 0;
    } else if (cMax == rNorm) {
      h = (int) (60 * (((gNorm - bNorm) / delta) % 6));
    } else if (cMax == gNorm) {
      h = (int) (60 * (((bNorm - rNorm) / delta) + 2));
    } else {
      h = (int) (60 * (((rNorm - gNorm) / delta) + 4));
    }
    if (h < 0) {
      h += 360;
    }

    s = (cMax == 0) ? 0 : (int) (delta / cMax);
    v = (int) cMax;
    return new int[] {h, s, v};
  }

  public int[] getRGB() {
    return new int[] {r, g, b};
  }

  public int getHEX() {
    int[] rgb = getRGB();

    int hex = rgb[0] << 16 | rgb[1] << 8 | rgb[2];

    return hex;
  }

  public void applyTo(AddressableLEDBuffer target) {
    for (int i = 0; i < target.getLength(); i++) {
      target.setRGB(i, r, g, b);
    }
  }

  public static Color fromHSV(int h, int s, int v) {
    Color color = new Color();

    int i = (int) (h * 6);
    double f = h * 6 - i;
    double p = v * (1 - s);
    double q = v * (1 - f * s);
    double t = v * (1 - (1 - f) * s);

    switch (i % 6) {
      case 0:
        color.r = (int) (v * 255);
        color.g = (int) (t * 255);
        color.b = (int) (p * 255);
        break;
      case 1:
        color.r = (int) (q * 255);
        color.g = (int) (v * 255);
        color.b = (int) (p * 255);
        break;
      case 2:
        color.r = (int) (p * 255);
        color.g = (int) (v * 255);
        color.b = (int) (t * 255);
        break;
      case 3:
        color.r = (int) (p * 255);
        color.g = (int) (q * 255);
        color.b = (int) (v * 255);
        break;
      case 4:
        color.r = (int) (t * 255);
        color.g = (int) (p * 255);
        color.b = (int) (v * 255);
        break;
      case 5:
        color.r = (int) (v * 255);
        color.g = (int) (p * 255);
        color.b = (int) (q * 255);
        break;
      default:
        throw new RuntimeException("Invalid hue value.");
    }
    return color;
  }

  public static Color fromHEX(int hex) {

    int r = (hex >> 16) & 0xFF;
    int g = (hex >> 8) & 0xFF;
    int b = hex & 0xFF;

    return fromRGB(r, g, b);
  }

  public static Color fromRGB(int r, int g, int b) {
    Color color = new Color();
    color.r = r;
    color.g = g;
    color.b = b;
    return color;
  }

  public static Color fromRGB(double r, double g, double b) {
    return fromRGB((int) r, (int) g, (int) b);
  }

  public void setR(int r) {
    this.r = r;
  }

  public int getR() {
    return r;
  }

  public void setG(int g) {
    this.g = g;
  }

  public int getG() {
    return g;
  }

  public void setB(int b) {
    this.b = b;
  }

  public int getB() {
    return b;
  }

  public static final Color WHITE = fromRGB(255, 255, 255);
  public static final Color RED = fromRGB(255, 0, 0);
  public static final Color ORANGE = fromRGB(105, 35, 5);
  public static final Color YELLOW = fromRGB(255, 165, 0);
  public static final Color GREEN = fromRGB(0, 255, 0);
  public static final Color BLUE = fromRGB(0, 0, 255);
  public static final Color PURPLE = fromRGB(157, 0, 255);
  public static final Color SEXY_PURPLE = fromRGB(113, 52, 235);
  public static final Color SPOOKY_ORANGE = fromHSV(3, 1, 1);
  public static final Color LEMONADE = fromRGB(230, 174, 34);
  public static final Color SEXY_BLUE = fromRGB(78, 30, 156);
  public static final Color LOVLEY_PINK = fromRGB(141, 93 / 3, 31 / 3);
  public static final Color ALGAY_GREEN = fromRGB(37, 243, 108);
  public static final Color OFF = fromHSV(0, 0, 0);
}
