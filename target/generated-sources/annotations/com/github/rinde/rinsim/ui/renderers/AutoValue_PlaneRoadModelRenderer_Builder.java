
package com.github.rinde.rinsim.ui.renderers;

import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_PlaneRoadModelRenderer_Builder extends PlaneRoadModelRenderer.Builder {

  private final double margin;

  AutoValue_PlaneRoadModelRenderer_Builder(
      double margin) {
    this.margin = margin;
  }

  @Override
  double margin() {
    return margin;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof PlaneRoadModelRenderer.Builder) {
      PlaneRoadModelRenderer.Builder that = (PlaneRoadModelRenderer.Builder) o;
      return (Double.doubleToLongBits(this.margin) == Double.doubleToLongBits(that.margin()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= (int) ((Double.doubleToLongBits(this.margin) >>> 32) ^ Double.doubleToLongBits(this.margin));
    return h;
  }

  private static final long serialVersionUID = -3124446663942895548L;

}
