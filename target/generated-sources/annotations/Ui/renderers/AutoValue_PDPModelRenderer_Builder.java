
package Ui.renderers;

import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_PDPModelRenderer_Builder extends PDPModelRenderer.Builder {

  private final boolean drawDestLines;

  AutoValue_PDPModelRenderer_Builder(
      boolean drawDestLines) {
    this.drawDestLines = drawDestLines;
  }

  @Override
  boolean drawDestLines() {
    return drawDestLines;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof PDPModelRenderer.Builder) {
      PDPModelRenderer.Builder that = (PDPModelRenderer.Builder) o;
      return (this.drawDestLines == that.drawDestLines());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.drawDestLines ? 1231 : 1237;
    return h;
  }

  private static final long serialVersionUID = 8354062467527222977L;

}
