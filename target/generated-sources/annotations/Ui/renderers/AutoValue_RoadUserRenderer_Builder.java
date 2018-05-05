
package Ui.renderers;

import com.google.common.collect.ImmutableMap;
import javax.annotation.Generated;
import org.eclipse.swt.graphics.RGB;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_RoadUserRenderer_Builder extends RoadUserRenderer.Builder {

  private final boolean useEncirclement;
  private final boolean useTextLabel;
  private final ImmutableMap<Class<?>, RGB> colorMap;
  private final ImmutableMap<Class<?>, String> imageMap;

  AutoValue_RoadUserRenderer_Builder(
      boolean useEncirclement,
      boolean useTextLabel,
      ImmutableMap<Class<?>, RGB> colorMap,
      ImmutableMap<Class<?>, String> imageMap) {
    this.useEncirclement = useEncirclement;
    this.useTextLabel = useTextLabel;
    if (colorMap == null) {
      throw new NullPointerException("Null colorMap");
    }
    this.colorMap = colorMap;
    if (imageMap == null) {
      throw new NullPointerException("Null imageMap");
    }
    this.imageMap = imageMap;
  }

  @Override
  boolean useEncirclement() {
    return useEncirclement;
  }

  @Override
  boolean useTextLabel() {
    return useTextLabel;
  }

  @Override
  ImmutableMap<Class<?>, RGB> colorMap() {
    return colorMap;
  }

  @Override
  ImmutableMap<Class<?>, String> imageMap() {
    return imageMap;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof RoadUserRenderer.Builder) {
      RoadUserRenderer.Builder that = (RoadUserRenderer.Builder) o;
      return (this.useEncirclement == that.useEncirclement())
           && (this.useTextLabel == that.useTextLabel())
           && (this.colorMap.equals(that.colorMap()))
           && (this.imageMap.equals(that.imageMap()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.useEncirclement ? 1231 : 1237;
    h *= 1000003;
    h ^= this.useTextLabel ? 1231 : 1237;
    h *= 1000003;
    h ^= this.colorMap.hashCode();
    h *= 1000003;
    h ^= this.imageMap.hashCode();
    return h;
  }

  private static final long serialVersionUID = -7137180979168032846L;

}
