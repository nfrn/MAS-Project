
package Ui;

import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_SimulationViewer_Builder extends SimulationViewer.Builder {

  private final View.Builder viewBuilder;

  AutoValue_SimulationViewer_Builder(
      View.Builder viewBuilder) {
    if (viewBuilder == null) {
      throw new NullPointerException("Null viewBuilder");
    }
    this.viewBuilder = viewBuilder;
  }

  @Override
  View.Builder viewBuilder() {
    return viewBuilder;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof SimulationViewer.Builder) {
      SimulationViewer.Builder that = (SimulationViewer.Builder) o;
      return (this.viewBuilder.equals(that.viewBuilder()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.viewBuilder.hashCode();
    return h;
  }

}
