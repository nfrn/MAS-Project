
package Ui.renderers;

import com.google.common.collect.ImmutableSet;
import javax.annotation.Generated;
import org.eclipse.swt.graphics.RGB;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_CommRenderer_Builder extends CommRenderer.Builder {

  private final RGB reliableColor;
  private final RGB unreliableColor;
  private final ImmutableSet<CommRenderer.ViewOptions> viewOptions;

  AutoValue_CommRenderer_Builder(
      RGB reliableColor,
      RGB unreliableColor,
      ImmutableSet<CommRenderer.ViewOptions> viewOptions) {
    if (reliableColor == null) {
      throw new NullPointerException("Null reliableColor");
    }
    this.reliableColor = reliableColor;
    if (unreliableColor == null) {
      throw new NullPointerException("Null unreliableColor");
    }
    this.unreliableColor = unreliableColor;
    if (viewOptions == null) {
      throw new NullPointerException("Null viewOptions");
    }
    this.viewOptions = viewOptions;
  }

  @Override
  RGB reliableColor() {
    return reliableColor;
  }

  @Override
  RGB unreliableColor() {
    return unreliableColor;
  }

  @Override
  ImmutableSet<CommRenderer.ViewOptions> viewOptions() {
    return viewOptions;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof CommRenderer.Builder) {
      CommRenderer.Builder that = (CommRenderer.Builder) o;
      return (this.reliableColor.equals(that.reliableColor()))
           && (this.unreliableColor.equals(that.unreliableColor()))
           && (this.viewOptions.equals(that.viewOptions()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.reliableColor.hashCode();
    h *= 1000003;
    h ^= this.unreliableColor.hashCode();
    h *= 1000003;
    h ^= this.viewOptions.hashCode();
    return h;
  }

  private static final long serialVersionUID = 3529315729865532162L;

}
