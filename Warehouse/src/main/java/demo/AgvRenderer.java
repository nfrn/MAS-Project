package demo;

import com.github.rinde.rinsim.core.model.DependencyProvider;
import com.github.rinde.rinsim.core.model.ModelBuilder.AbstractModelBuilder;
import com.github.rinde.rinsim.core.model.pdp.Vehicle;
import com.github.rinde.rinsim.core.model.road.RoadModel;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.ui.renderers.CanvasRenderer;
import com.github.rinde.rinsim.ui.renderers.UiSchema;
import com.github.rinde.rinsim.ui.renderers.ViewPort;
import com.google.auto.value.AutoValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkState;

public class AgvRenderer extends CanvasRenderer.AbstractCanvasRenderer {

    static final Point LABEL_OFFSET = new Point(-5, -20);
    final UiSchema uiSchema = new UiSchema(false);
    RoadModel roadModel;
    AgvModel agvModel;

    AgvRenderer(RoadModel rm, AgvModel am) {

        roadModel = rm;
        agvModel = am;
        uiSchema.add(AgvAgent.class, "/graphics/perspective/deliverypackage2.png");
    }

    static Builder builder() {
        return new AutoValue_AgvRenderer_Builder();
    }

    @Override
    public void renderStatic(GC gc, ViewPort viewPort) {

    }

    @Override
    public void renderDynamic(GC gc, ViewPort viewPort, long l) {
        uiSchema.initialize(gc.getDevice());

        final Collection<Vehicle> vehicles = agvModel.getVehicles();
        final Image image = uiSchema.getImage(AgvAgent.class);
        checkState(image != null);

        synchronized (agvModel) {
            for (Vehicle v : vehicles) {
                AgvAgent agent = (AgvAgent) v;
                String text = String.format("%.0f ", (double) agent.power / agent.POWERLIMIT * 100) + "%";
                Point pos = roadModel.getPosition(v);
                int x = viewPort.toCoordX(pos.x);
                int y = viewPort.toCoordY(pos.y);
                int textWidth = gc.textExtent(text).x;
                gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_BLUE));
                gc.drawText(text, (int) LABEL_OFFSET.x + x - textWidth / 2,
                        (int) LABEL_OFFSET.y + y, true);

            }
        }
    }

    @AutoValue
    abstract static class Builder
            extends AbstractModelBuilder<AgvRenderer, Void> {
        private static final long serialVersionUID = 3349625514419113101L;

        Builder() {
            setDependencies(RoadModel.class, AgvModel.class);
        }

        @Override
        public AgvRenderer build(DependencyProvider dependencyProvider) {
            final RoadModel rm = dependencyProvider.get(RoadModel.class);
            final AgvModel pm = dependencyProvider.get(AgvModel.class);
            return new AgvRenderer(rm, pm);
        }
    }
}
