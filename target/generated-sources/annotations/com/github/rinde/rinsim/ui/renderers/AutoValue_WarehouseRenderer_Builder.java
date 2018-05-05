
package com.github.rinde.rinsim.ui.renderers;

import com.google.common.collect.ImmutableSet;
import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_WarehouseRenderer_Builder extends WarehouseRenderer.Builder {

  private final ImmutableSet<WarehouseRenderer.VizOptions> vizOptions;
  private final double margin;

  AutoValue_WarehouseRenderer_Builder(
      ImmutableSet<WarehouseRenderer.VizOptions> vizOptions,
      double margin) {
    if (vizOptions == null) {
      throw new NullPointerException("Null vizOptions");
    }
    this.vizOptions = vizOptions;
    this.margin = margin;
  }

  @Override
  ImmutableSet<WarehouseRenderer.VizOptions> vizOptions() {
    return vizOptions;
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
    if (o instanceof WarehouseRenderer.Builder) {
      WarehouseRenderer.Builder that = (WarehouseRenderer.Builder) o;
      return (this.vizOptions.equals(that.vizOptions()))
           && (Double.doubleToLongBits(this.margin) == Double.doubleToLongBits(that.margin()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.vizOptions.hashCode();
    h *= 1000003;
    h ^= (int) ((Double.doubleToLongBits(this.margin) >>> 32) ^ Double.doubleToLongBits(this.margin));
    return h;
  }

  private static final long serialVersionUID = 2640504685565091840L;

}
