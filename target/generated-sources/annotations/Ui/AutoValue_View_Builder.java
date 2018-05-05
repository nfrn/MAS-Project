
package Ui;

import Ui.renderers.Renderer;
import com.github.rinde.rinsim.core.model.ModelBuilder;
import com.github.rinde.rinsim.event.Listener;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import javax.annotation.Generated;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_View_Builder extends View.Builder {

  private final ImmutableSet<ModelBuilder<? extends Renderer, ?>> renderers;
  private final ImmutableSet<View.ViewOption> viewOptions;
  private final ImmutableMap<MenuItems, Integer> accelerators;
  private final int speedUp;
  private final long stopTime;
  private final String title;
  private final Point screenSize;
  private final Optional<Listener> callback;
  private final Optional<Monitor> monitor;
  private final Optional<Display> display;

  AutoValue_View_Builder(
      ImmutableSet<ModelBuilder<? extends Renderer, ?>> renderers,
      ImmutableSet<View.ViewOption> viewOptions,
      ImmutableMap<MenuItems, Integer> accelerators,
      int speedUp,
      long stopTime,
      String title,
      Point screenSize,
      Optional<Listener> callback,
      Optional<Monitor> monitor,
      Optional<Display> display) {
    if (renderers == null) {
      throw new NullPointerException("Null renderers");
    }
    this.renderers = renderers;
    if (viewOptions == null) {
      throw new NullPointerException("Null viewOptions");
    }
    this.viewOptions = viewOptions;
    if (accelerators == null) {
      throw new NullPointerException("Null accelerators");
    }
    this.accelerators = accelerators;
    this.speedUp = speedUp;
    this.stopTime = stopTime;
    if (title == null) {
      throw new NullPointerException("Null title");
    }
    this.title = title;
    if (screenSize == null) {
      throw new NullPointerException("Null screenSize");
    }
    this.screenSize = screenSize;
    if (callback == null) {
      throw new NullPointerException("Null callback");
    }
    this.callback = callback;
    if (monitor == null) {
      throw new NullPointerException("Null monitor");
    }
    this.monitor = monitor;
    if (display == null) {
      throw new NullPointerException("Null display");
    }
    this.display = display;
  }

  @Override
  ImmutableSet<ModelBuilder<? extends Renderer, ?>> renderers() {
    return renderers;
  }

  @Override
  ImmutableSet<View.ViewOption> viewOptions() {
    return viewOptions;
  }

  @Override
  ImmutableMap<MenuItems, Integer> accelerators() {
    return accelerators;
  }

  @Override
  int speedUp() {
    return speedUp;
  }

  @Override
  long stopTime() {
    return stopTime;
  }

  @Override
  String title() {
    return title;
  }

  @Override
  Point screenSize() {
    return screenSize;
  }

  @Override
  Optional<Listener> callback() {
    return callback;
  }

  @Override
  Optional<Monitor> monitor() {
    return monitor;
  }

  @Override
  Optional<Display> display() {
    return display;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof View.Builder) {
      View.Builder that = (View.Builder) o;
      return (this.renderers.equals(that.renderers()))
           && (this.viewOptions.equals(that.viewOptions()))
           && (this.accelerators.equals(that.accelerators()))
           && (this.speedUp == that.speedUp())
           && (this.stopTime == that.stopTime())
           && (this.title.equals(that.title()))
           && (this.screenSize.equals(that.screenSize()))
           && (this.callback.equals(that.callback()))
           && (this.monitor.equals(that.monitor()))
           && (this.display.equals(that.display()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.renderers.hashCode();
    h *= 1000003;
    h ^= this.viewOptions.hashCode();
    h *= 1000003;
    h ^= this.accelerators.hashCode();
    h *= 1000003;
    h ^= this.speedUp;
    h *= 1000003;
    h ^= (int) ((this.stopTime >>> 32) ^ this.stopTime);
    h *= 1000003;
    h ^= this.title.hashCode();
    h *= 1000003;
    h ^= this.screenSize.hashCode();
    h *= 1000003;
    h ^= this.callback.hashCode();
    h *= 1000003;
    h ^= this.monitor.hashCode();
    h *= 1000003;
    h ^= this.display.hashCode();
    return h;
  }

  private static final long serialVersionUID = -955386603340399937L;

}
