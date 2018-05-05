
package com.github.rinde.rinsim.ui.renderers;

import com.google.common.collect.ImmutableSet;
import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_AGVRenderer_Builder extends AGVRenderer.Builder {

  private final ImmutableSet<AGVRenderer.VizOptions> vizOptions;

  AutoValue_AGVRenderer_Builder(
      ImmutableSet<AGVRenderer.VizOptions> vizOptions) {
    if (vizOptions == null) {
      throw new NullPointerException("Null vizOptions");
    }
    this.vizOptions = vizOptions;
  }

  @Override
  ImmutableSet<AGVRenderer.VizOptions> vizOptions() {
    return vizOptions;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof AGVRenderer.Builder) {
      AGVRenderer.Builder that = (AGVRenderer.Builder) o;
      return (this.vizOptions.equals(that.vizOptions()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.vizOptions.hashCode();
    return h;
  }

  private static final long serialVersionUID = -8359744710512375486L;

}
