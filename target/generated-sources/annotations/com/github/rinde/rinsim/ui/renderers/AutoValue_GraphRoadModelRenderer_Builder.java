
package com.github.rinde.rinsim.ui.renderers;

import com.google.common.collect.ImmutableSet;
import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_GraphRoadModelRenderer_Builder extends GraphRoadModelRenderer.Builder {

  private final int margin;
  private final ImmutableSet<GraphRoadModelRenderer.VizOptions> vizOptions;

  AutoValue_GraphRoadModelRenderer_Builder(
      int margin,
      ImmutableSet<GraphRoadModelRenderer.VizOptions> vizOptions) {
    this.margin = margin;
    if (vizOptions == null) {
      throw new NullPointerException("Null vizOptions");
    }
    this.vizOptions = vizOptions;
  }

  @Override
  int margin() {
    return margin;
  }

  @Override
  ImmutableSet<GraphRoadModelRenderer.VizOptions> vizOptions() {
    return vizOptions;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof GraphRoadModelRenderer.Builder) {
      GraphRoadModelRenderer.Builder that = (GraphRoadModelRenderer.Builder) o;
      return (this.margin == that.margin())
           && (this.vizOptions.equals(that.vizOptions()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.margin;
    h *= 1000003;
    h ^= this.vizOptions.hashCode();
    return h;
  }

  private static final long serialVersionUID = -5509180917238606415L;

}
