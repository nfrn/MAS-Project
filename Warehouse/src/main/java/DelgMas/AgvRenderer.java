package DelgMas;

import com.github.rinde.rinsim.core.model.DependencyProvider;
import com.github.rinde.rinsim.core.model.ModelBuilder.AbstractModelBuilder;
import com.github.rinde.rinsim.core.model.pdp.Parcel;
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
import java.util.Iterator;

import static DelgMas.AgvExample.NUM_AGVS;
import static com.google.common.base.Preconditions.checkState;

public class AgvRenderer extends CanvasRenderer.AbstractCanvasRenderer {

    static final Point LABEL_OFFSET = new Point(-5, -20);
    final UiSchema uiSchema;
    RoadModel roadModel;
    AgvModel agvModel;

    AgvRenderer(RoadModel rm, AgvModel am) {

        roadModel = rm;
        agvModel = am;
        uiSchema = new UiSchema(false);
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

        synchronized (agvModel) {
            final Collection<Vehicle> vehicles = agvModel.getVehicles();
            final Image image = uiSchema.getImage(AgvAgent.class);
            checkState(image != null);

            for (Iterator<Vehicle> it = vehicles.iterator(); it.hasNext(); ) {
                Vehicle v = it.next();
                AgvAgent agent = (AgvAgent) v;
                Battery battery = agent.getBattery();
                long power = 0;
                if (battery != null) {
                    power = battery.capacity;
                }
                final String text = String.format("%.0f ", (double) power / battery.POWERLIMIT * 100) + "%";
                if(roadModel.getObjectsOfType(Vehicle.class).size()!=NUM_AGVS){break;}
                try {
                    final Point pos = roadModel.getPosition(v);
                    final int x = viewPort.toCoordX(pos.x);
                    final int y = viewPort.toCoordY(pos.y);
                    final int textWidth = gc.textExtent(text).x;
                    gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_BLUE));
                    gc.drawText(text, (int) LABEL_OFFSET.x + x - textWidth / 2,
                            (int) LABEL_OFFSET.y + y, true);
                }catch (Exception e){continue;}
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
